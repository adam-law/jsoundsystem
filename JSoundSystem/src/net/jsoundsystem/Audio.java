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

import javax.sound.sampled.AudioFormat;

/**
 * An abstract sound object. Could be a streaming sound or a spatialized sound.
 * @see JSound, JSound3D and JMusic
 */
abstract class Audio {
	/**
	 * This points to the actual audio where it is played in a different thread
	 */
	protected final AudioThread soundThread;
	
	protected Audio( AudioThread thread ){
		soundThread = thread;
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
	 * Disposes of this sound and frees all resources is uses. The audio object cannot be used anymore 
	 * after this is done.
	 */
	public void dispose(){
		soundThread.dispose();
	}
	
	/**
	 * Returns information about the format of this specific audio such as frequency, mono or stereo, etc.
	 * @return And AudioFormat object containing various information about this sound
	 */
	public AudioFormat getSoundFormat(){
		return soundThread.getAudioFormat();
	}
}
