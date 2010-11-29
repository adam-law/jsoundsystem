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

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A sound container class to make using sound effects very easy. To construct a 
 * new JSound simply call createSound() from the static SoundSystem class.
 * @author Johan Jansen
 *
 */
public class JSound {
	/**
	 * This points to the actual sound where the sound is played in a different thread
	 */
	protected final JSoundThread soundThread;

	/**
	 * Hidden constructor only used by the SoundSystem
	 */
	JSound( JSoundThread thread ){
		soundThread = thread;
	}
	
	/**
	 * A constructor for the JSound object. This is same as calling JSoundSystem.createSound( File soundFile )
	 * @param soundFile The file you want to play as an audio file.
	 * @throws UnsupportedAudioFileException If the API cannot convert the file into an audio stream
	 * @throws IOException If the file could not be read
	 */
	public JSound( File soundFile ) throws UnsupportedAudioFileException, IOException{
		//Make sure the file is actually a sound
		if( !JSoundSystem.soundIsSupported(soundFile) ) 
			throw new UnsupportedAudioFileException("Audio file not supported: " + soundFile.getAbsolutePath());

		soundThread = JSoundSystem.createSoundThread(soundFile, false);
	}
	
	/**
	 * A constructor for the JSound object. This is same as calling JSoundSystem.createSound( String soundFile )
	 * @param soundFile The path to the file you want to use as an audio file.
	 * @throws UnsupportedAudioFileException If the API cannot convert the file into an audio stream
	 * @throws IOException If the file could not be read
	 */
	public JSound( String soundFile ) throws UnsupportedAudioFileException, IOException{
		soundThread = JSoundSystem.createSoundThread( new File(soundFile), false);
	}
		
	/**
	 * This method starts playing a sound that is either stopped or paused.
	 * A sound that hasn't begun playing is stopped. This function will do
	 * nothing if there are no free channels.
	 */
	public void play() {

		//No free channels to start a new sound
		if( !JSoundSystem.hasFreeChannels() && !soundThread.isPaused() ) return;

		soundThread.play();
	}
	
	/**
	 * Stops playing a sound and resets its position
	 */
	public void stop(){
		soundThread.stopPlaying();
	}
	
	/**
	 * Stops playing a sound, but will resume at the same position once
	 * play() is called again.
	 */
	public void pause(){
		soundThread.pause();
	}
	
	/**
	 * Sets if this sound is supposed to be looping or not. A looping sound
	 * will play in infinity until it is stopped looping or a stop() is called.
	 * @param looping
	 */
	public void setLoop( boolean looping ){
		soundThread.setLooped( looping );
	}
	
	/**
	 * Returns True if this sound is currently playing
	 */
	public boolean isPlaying(){
		return soundThread.isPlaying();
	}
	
	public String toString(){
		return soundThread.getName();
	}
	
	/**
	 * Changes the volume of this sound. The number indicates how loud the sound
	 * will be played (For example 0.10f is 10%, while 1.00f means 100% and 2.25f means 225%)
	 * @param volume A number between 0.00f and 5.00f where 1.00f is default 
	 */
	public void setVolume( float volume ){
		soundThread.setVolume( volume );
	}
	
	/**
	 * Changes how fast this sound is played by changing it's sample playback rate.
	 * @param speed A non-negative float that describes how fast to play. 
	 * 1.00f means 100% speed (default)
	 */
	public void setSpeed( float speed ){
		soundThread.setSpeed( speed );
	}
	
	/**
	 * Sets if this sound is to be played on the left or right speaker.
	 * @param panning A number between -1.00f (left) and 1.00f (right). 0.00f is the default (center)
	 */
	public void setPanning( float panning ) {
		soundThread.setPanning( panning );
	}
	
	/**
	 * Disposes of this sound and frees all resources is uses. The JSound object cannot be used anymore 
	 * after this is done.
	 */
	public void dispose(){
		soundThread.dispose();
	}
}