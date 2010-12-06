/******************************************************************************
JSoundSystem is a simple and easy sound API to use sound in your Java applications.
Copyright (C) 2010  Johan Jansen

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
************************************************************************/

package net.jsoundsystem;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.jsoundsystem.utils.Vector3f;


class AudioThread extends Thread {

	//Effect mixer modifiers. They are used in a bitmap so each much be a power of two
	private static final int MIX_VOLUME 	=  	1 << 0;
	private static final int MIX_PANNING 	= 	1 << 1;
	private static final int MIX_SPEED 		=   1 << 2;

	//Thread sound effects
	private boolean looping, paused, stopped;
	private boolean killThread;
	
	private float volume;
	private float panning;
	private float speed;
	
	//3D sound simulation
	private boolean simulate3DEffect;
	private Vector3f source;
	private float lastVolume;

	//Playback data
	private AudioFormat soundFormat;
	private byte[] soundData;
	private File filePath;
	private SourceDataLine audioChannel;

	
	/**
	 * Constructs a new SoundThread, should only be called by the SoundSystem
	 * @throws IOException 
	 * @throws UnsupportedAudioFileException 
	 */
	AudioThread( File path, byte[] data, AudioFormat format ) throws UnsupportedAudioFileException, IOException {
		super( path.getName() );
		filePath = path;
		setPriority( Thread.MIN_PRIORITY );		//Sounds have low priority
		setDaemon(true);						//And run independently
		
		//Set default values
		volume = 1.00f;
		speed = 1.00f;
		panning = 0.00f;
		
		//Get the audio format for this sound
		soundFormat = format;
		soundData = data;
	}
	
	public void enableSpatializedSound(){
		source = new Vector3f();
		simulate3DEffect = true;
	}

	protected void setLooped( boolean looping ){
		this.looping = looping;
	}

	protected void setPanning( float panning ){
		this.panning = panning;
		mixSoundEffects( audioChannel, MIX_PANNING );
	}

	protected void setVolume( float volume ){
		this.volume = volume;
		mixSoundEffects( audioChannel, MIX_VOLUME );
	}

	protected void setSpeed( float speed ){
		this.speed = speed;
		mixSoundEffects( audioChannel, MIX_SPEED );
	}

	/**
	 * Internal run function inherited from the Thread class. This is where the actual sound playing
	 * happens. This function should not be run directly, but rather activated by the play() function.
	 */
	public void run() {

		try {

			//Keep data ready until we are disposed of
			while( !killThread ){
				
				//Open and reserve a sound channel
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, soundFormat);
				audioChannel = (SourceDataLine) AudioSystem.getLine(info);
				audioChannel.open( soundFormat );
				JSoundSystem.channelsPlaying++;
				
				//Ready the sound stream
				InputStream stream = null;
				
				//It's a sound loaded into memory
				if( soundData != null ) {
					stream = new ByteArrayInputStream(soundData);
					
					//Use mark if supported
					if( stream.markSupported() ) stream.mark( stream.available() );
				}

				//This might be do once or in infinity, depending on the loop variable
				do{
					//Reset sound and reopen a sound stream if needed
					if( stream != null && stream.markSupported() ) stream.reset();
					else {
						//Need to reopen the sound to reset it
						if( stream != null) stream.close();
						stream = JSoundSystem.getAudioInputStream(filePath);
						if( stream.markSupported() ) stream.mark( stream.available() );
					}

					//begin playing
					audioChannel.start();

					//Apply various sound effects
					mixSoundEffects( audioChannel, MIX_PANNING | MIX_VOLUME | MIX_SPEED );

					//This actually plays the sound
					int len = 0;					
					int bytesPerFrame = soundFormat.getFrameSize();

					// some audio formats may have unspecified frame size
					// in that case we may read any amount of bytes
					if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) bytesPerFrame = 1;

					// Set an arbitrary buffer size of 1024 frames.
					int numBytes = 1024 * bytesPerFrame; 
					byte[] audioBytes = new byte[numBytes];
					
					//Keep playing as long as there is data left and sound has not been stopped
					while ( !stopped && (len = stream.read(audioBytes) ) != -1 ) {
						
						//Pause until we are told to continue
						if( paused ) sleepThread();
						
						//Update 3D sound effects
						if( simulate3DEffect ) update3DSound();

						audioChannel.write(audioBytes, 0, len);					
					}

					//Finish the rest of the data
					if( !stopped ) audioChannel.drain();

					//Release resources
					audioChannel.stop();
					stream.close();
				}
				while( looping && !stopped );

				audioChannel.close();
				audioChannel = null;
				JSoundSystem.channelsPlaying--;

				//Pause until further notice
				sleepThread();
			} 
		} catch(LineUnavailableException e){
			System.err.println("Could not play sound ("+ getName() +"): Audio Drivers doesnt support more than " + JSoundSystem.channelsPlaying + "sound channels.");
		} catch (Exception e)  {
			System.err.println("Error playing sound ("+ getName() +"): " + e);
			e.printStackTrace();
		}
		
		//This thread should be killed now
	}

	private void sleepThread(){
		if( killThread ) return;
		paused = true;

		//This causes the thread to sleep until a notify() is called
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void resumeThread(){
		//No need to wake up if we are not sleeping
		if( !paused || killThread ) return;

		synchronized (this) {
			this.notifyAll();	//Wake up!
			paused = false;

			if( audioChannel != null ) audioChannel.start();
		}
	}

	/**
	 * Begins playing the sound or resumes if it was paused
	 */
	protected void play(){
		if( killThread ) return;
		stopped = false;

		//Begin playing for the first time
		if( !this.isAlive() ){
			start();
			return;
		}

		//Already playing, try to unpause
		resumeThread();		
	}

	protected void pause() {
		paused = true;
		if( audioChannel != null ) audioChannel.stop();
	}

	protected void dispose(){
		stopped = true;
		killThread = true;
	}
	
	/**
	 * JJ> This updates sound mixer effects like volume and sound balance to an active audio channel
	 * @param line Which audio line to update
	 * @param effects A bitmask with the effects to update
	 */
	private void mixSoundEffects( Line line, int effects ) {
		
		//No need to mix something that isn't playing
		if( audioChannel == null ) return;
					
		//Adjust sound speed
		if( (effects & MIX_SPEED) != 0 && line.isControlSupported(FloatControl.Type.SAMPLE_RATE) ) {
			FloatControl gainControl = (FloatControl)line.getControl(FloatControl.Type.SAMPLE_RATE);
			float sampleRate = soundFormat.getFrameRate() * speed;
			sampleRate = Math.max(gainControl.getMinimum(), Math.min(sampleRate, gainControl.getMaximum()));
			gainControl.setValue(sampleRate);
		}
		
		//Adjust sound balance
		if( (effects & MIX_PANNING) != 0 && line.isControlSupported(FloatControl.Type.PAN) ) {
			FloatControl gainControl = (FloatControl)line.getControl(FloatControl.Type.PAN);
			panning = Math.max(gainControl.getMinimum(), Math.min(panning, gainControl.getMaximum()));
			gainControl.setValue(panning);
		}

		//Set sound volume
		if( (effects & MIX_VOLUME) != 0 && line.isControlSupported(FloatControl.Type.MASTER_GAIN) ) {
			FloatControl gainControl = (FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);	
			float gain = (float)(Math.log(volume)/Math.log(10.0f)*20.0f);
			gain = Math.max(gainControl.getMinimum(), Math.min(gain, gainControl.getMaximum()));
			gainControl.setValue(gain);	
		}
	}

	protected boolean isPlaying() {
		return !paused && !stopped;
	}

	protected void stopPlaying(){
		resumeThread();
		stopped = true;
		
		if( audioChannel != null ){
			audioChannel.stop();
			audioChannel.flush();
		}
	}

	private void update3DSound() {
		Vector3f listenerPosition = JSoundSystem.getListenerPosition();
		float distance = listenerPosition.getDistance(source);
		
        //Calculate how loud the sound is
		float newVolume = (JSoundSystem.maxDistance - distance) / JSoundSystem.maxDistance;
	    if (newVolume <= 0) newVolume = 0;

        //Calculate if the sound is left or right oriented
        float newPanning = (2.00f/JSoundSystem.maxDistance) * -(listenerPosition.x - source.x);
        
        //Now actually update the effects
        setVolume( (newVolume + lastVolume) / 2);
        setPanning( newPanning );
        lastVolume = newVolume;
	}
	
	protected void setSourcePosition( Vector3f pos ){
		source = pos;
	}

	public boolean isPaused() {
		return paused;
	}
	
	/**
	 * This function makes an exact copy of this JSoundThread, also cloning the sound data, format, etc.
	 */
	public AudioThread clone() {
		AudioThread copy;
		
		//Try to clone the actual thread
		try {
			copy = new AudioThread( filePath, soundData, soundFormat );
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		//Copy attributes
		copy.volume = this.volume;
		copy.looping = this.looping;
		copy.speed = this.speed;
		copy.source = new Vector3f(source);
		
		//Finished cloning
		return copy;
	}
	
	public AudioFormat getAudioFormat() {
		return soundFormat;
	}
	
	
	public void invertSoundData(){
		//Simply change reference to the byte array instead of actually modifying
		//the array itself. Others might be using the old non-inversed array through
		//the clone() method. Slower, but it's safe.
		
		byte[] inverseData = new byte[soundData.length];
		
		for( int i = 0; i < soundData.length; i++ ){
			inverseData[soundData.length-i-1] = soundData[i];
		}
		
		soundData = inverseData;
	}
}