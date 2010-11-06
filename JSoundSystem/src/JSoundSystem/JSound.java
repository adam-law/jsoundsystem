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

/**
 * A sound container class to make using sound effects very easy. To construct a 
 * new JSound simply call createSound() from the static SoundSystem class.
 * @author Johan Jansen
 *
 */
public class JSound {
	protected JSoundThread soundThread;
	protected boolean oggFile = false;

	/**
	 * Hidden constructor only used by the SoundSystem
	 */
	JSound( JSoundThread thread ){
		soundThread = thread;
	}
	
	/**
	 * This method starts playing a sound that is either stopped or paused.
	 * A sound that hasn't begun playing is stopped. This function will do
	 * nothing if there are no free channels.
	 */
	public void play() {

		//No free channels
		if( !JSoundSystem.hasFreeChannels() ) return;

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
}