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

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;


/**
 * A sound container class to make using sound effects very easy. To construct a 
 * new JSound simply call createSound() from the static SoundSystem class.
 * @author Johan Jansen
 *
 */
public class JSound extends Audio {

	/**
	 * A constructor for the JSound object. This is same as calling JSoundSystem.createSound( File soundFile )
	 * @param soundFile The file you want to play as an audio file.
	 * @throws UnsupportedAudioFileException If the API cannot convert the file into an audio stream
	 * @throws IOException If the file could not be read
	 */
	public JSound( File soundFile ) throws UnsupportedAudioFileException, IOException {
		super( JSoundSystem.createSoundThread(soundFile, true) );
	}
	
	private JSound( AudioThread thread ){
		super(thread);
	}
	
	/**
	 * A constructor for the JSound object. This is same as calling JSoundSystem.createSound( String soundFile )
	 * @param soundFile The path to the file you want to use as an audio file.
	 * @throws UnsupportedAudioFileException If the API cannot convert the file into an audio stream
	 * @throws IOException If the file could not be read
	 */
	public JSound( String soundFile ) throws UnsupportedAudioFileException, IOException{
		this( new File( soundFile) );
	}
	
	/**
	 * Makes an exact copy of this JSound object, also cloning any sound effect modifiers such as volume, speed
	 * looping, etc. 3D sound properties will also be cloned. This sound can be played independently from the
	 * original JSound from which this one is cloned from.
	 */
	public JSound clone() {
		return new JSound( soundThread.clone() );
	}
	
	/**
	 * This inverts the sound stream, playing the sound backwards. Calling this method twice will revert it to
	 * it's original stream, so no actual data is modified (only reversed).
	 */
	//This function has been uncommented. Reversing data on encoded audio doesn't work very well
	//public void reverse(){
	//	soundThread.invertSoundData();
	//}
}