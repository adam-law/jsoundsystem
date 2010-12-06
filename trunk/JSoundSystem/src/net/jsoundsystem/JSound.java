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