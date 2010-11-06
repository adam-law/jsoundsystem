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

package JSoundSystem;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


class JSoundThread extends Thread {
	private boolean looping, paused, stopped;
	private boolean killThread;
	private File audioFile;
	private float volume;
	private float panning;
	private float speed;
	private SourceDataLine audioChannel;
	private final float DEFAULT_SAMPLE_RATE;
	
	private static final int MIX_VOLUME = 1;
	private static final int MIX_PANNING = 2;
	private static final int MIX_SPEED = 3;

	/**
	 * Constructs a new SoundThread, should only be called by the SoundSystem
	 * @throws IOException 
	 * @throws UnsupportedAudioFileException 
	 */
	JSoundThread( String name, File file, float volume, float panning, boolean looping ) throws UnsupportedAudioFileException, IOException {
		super(name);
		setPriority( Thread.MIN_PRIORITY );		//Sounds have low priority
		setDaemon(true);						//And run independently

		this.audioFile = file;
		this.volume = volume;
		this.panning = panning;
		this.looping = looping;
		this.speed = 1.00f;
		DEFAULT_SAMPLE_RATE = AudioSystem.getAudioFileFormat( audioFile ).getFormat().getSampleRate();
	}

	public void setLooped( boolean looping ){
		this.looping = looping;
	}

	public void setPanning( float panning ){
		this.panning = panning;
		mixSoundEffects( audioChannel, MIX_PANNING );
	}

	public void setVolume( float volume ){
		this.volume = volume;
		mixSoundEffects( audioChannel, MIX_VOLUME );
	}

	public void setSpeed( float speed ){
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
				
				//Try to open a new AudioLine to the sound
				AudioInputStream audioStream = JSoundSystem.getAudioStream(audioFile);
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioStream.getFormat(), ((int) audioStream.getFrameLength() * audioStream.getFormat().getFrameSize()));
				audioChannel = (SourceDataLine) AudioSystem.getLine(info);
				audioChannel.open( audioStream.getFormat() );
				
				//This might be do once or in infinity, depending on the loop variable
				do{
					JSoundSystem.channelsPlaying++;
					audioChannel.start();		//begin playing

					//Apply various sound effects
					mixSoundEffects( audioChannel, MIX_PANNING | MIX_VOLUME | MIX_SPEED );

					//This actually plays the sound
					int len = 0;					
					int bytesPerFrame = audioStream.getFormat().getFrameSize();

					// some audio formats may have unspecified frame size
					// in that case we may read any amount of bytes
					if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) bytesPerFrame = 1;

					// Set an arbitrary buffer size of 1024 frames.
					int numBytes = 1024 * bytesPerFrame; 
					byte[] audioBytes = new byte[numBytes];

					//Keep playing as long as there is data left and sound has not been stopped
					while ( !stopped && (len = audioStream.read(audioBytes) ) != -1 ) {
						//Pause until we are told to continue
						if( paused ) sleepThread();

						audioChannel.write(audioBytes, 0, len);					
					}

					//Finish the rest of the data
					if( !stopped ) audioChannel.drain();		//play rest of the data
					else		   audioChannel.flush();		//discard rest of the data

					//Release resources
					audioChannel.stop();
					audioChannel.close();
					audioChannel = null;
					audioStream.close();
				}
				while( looping && !stopped );

				//Pause until further notice
				JSoundSystem.channelsPlaying--;
				sleepThread();
			} 
		}
		catch (Exception e)  {
			System.err.println("Error playing sound ("+ getName() +"): " + e);
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
		}
	}

	/**
	 * Begins playing the sound or resumes if it was paused
	 */
	public void play(){
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

	public void pause() {
		paused = true;
	}

	public void dispose(){
		stopped = true;
		killThread = true;
	}
	
	/**
	 * JJ> This updates sound mixer effects like volume and sound balance to an active audio channel
	 * @param line Which audio line to update
	 * @param effects A bitmask with the effects to update
	 */
	protected void mixSoundEffects(Line line, int effects ) {
		
		//No need to mix something that isn't playing
		if( audioChannel == null ) return;
					
		//Adjust sound speed
		if( (effects & MIX_SPEED) != 0 && line.isControlSupported(FloatControl.Type.SAMPLE_RATE) ) {
			FloatControl gainControl = (FloatControl)line.getControl(FloatControl.Type.SAMPLE_RATE);
			float sampleRate = DEFAULT_SAMPLE_RATE * speed;
			sampleRate = Math.max(gainControl.getMinimum(), Math.min(sampleRate, gainControl.getMaximum()));
			gainControl.setValue(sampleRate);
			System.out.println("Speed is: " + sampleRate);
			System.out.println("Minimum: " + gainControl.getMinimum() );
			System.out.println("Maximum: " + gainControl.getMaximum() );
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

	public boolean isPlaying() {
		return !paused && !stopped;
	}

	public void stopPlaying(){
		resumeThread();
		stopped = true;
	}


}