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

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;


class JSoundThread extends Thread {
	private boolean looping, paused, stopped;
	private boolean killThread;
	private File audioFile;
	private float volume;
	private float panning;

	/**
	 * Constructs a new SoundThread, should only be called by the SoundSystem
	 */
	JSoundThread( String name, File file, float volume, float panning, boolean looping ) {
		super(name);
		setPriority( Thread.MIN_PRIORITY );		//Sounds have low priority
		setDaemon(true);						//And run independently

		this.audioFile = file;
		this.volume = volume;
		this.panning = panning;
		this.looping = looping;
	}

	public void setLooped( boolean looping ){
		this.looping = looping;
	}

	public void setPanning( float panning ){
		this.panning = panning;
	}

	public void setVolume( float volume ){
		this.volume = volume;
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
				SourceDataLine audioChannel = (SourceDataLine) AudioSystem.getLine(info);
				audioChannel.open( audioStream.getFormat() );

				//This might be do once or in infinity, depending on the loop variable
				do{
					JSoundSystem.channelsPlaying++;
					audioChannel.start();		//begin playing

					//Apply various sound effects
					mixSoundEffects( audioChannel, volume, panning );

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
	 * JJ> This adds sound mixer effects like volume and sound balance to a audio line
	 * @param line Which audio line to adjust
	 */
	protected void mixSoundEffects(Line line, float volume, float panning) {

		//Clip them to some valid values
		panning = Math.max(-1, Math.min(1, panning));
		volume = Math.max(0, Math.min(1, volume));

		//Adjust sound balance
		if( panning != 0 && line.isControlSupported(FloatControl.Type.PAN) ) {
			FloatControl gainControl = (FloatControl)line.getControl(FloatControl.Type.PAN);
			gainControl.setValue(panning);
		}

		//Set sound volume
		if(line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
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