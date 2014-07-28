/******************************************************************************
JSoundSystem is a simple and easy sound API to use sound in your Java applications.
Copyright (c) 2014, Johan Jansen
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list 
of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this 
list of conditions and the following disclaimer in the documentation and/or other materials 
provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may be used 
to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
