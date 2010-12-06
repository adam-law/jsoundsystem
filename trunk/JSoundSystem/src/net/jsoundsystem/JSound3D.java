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

import net.jsoundsystem.utils.Vector3f;

/**
 * An extension of the JSound class that simulates a 3D sound effect. 3D sounds have a
 * listener position (the user) and a source position (the origin of the sound). 
 * A JSound3D will automatically update the loudness and the position of where the sound
 * is played depending on the positions. The max distance from what sounds can be heard
 * can be set by JSoundSystem.setMaxDistance() and the position of the listener can be
 * set by JSoundSystem.setListenerPosition().
 * <p>
 * Note that currently this simulates a very shallow 3D effect and works more like sounds in
 * two dimensional space. Also note that you cannot change the volume or panning of 3D sounds
 * since this is automatically done by the JSoundSystem API
 * @author Johan Jansen
 *
 */
public class JSound3D extends JSound {
	
	/**
	 * A constructor for the JSound3D object. This is same as calling JSoundSystem.create3DSound( File soundFile )
	 * @param soundFile The file you want to play as an audio file.
	 * @throws UnsupportedAudioFileException If the API cannot convert the file into an audio stream
	 * @throws IOException If the file could not be read
	 */
	public JSound3D( File soundFile ) throws UnsupportedAudioFileException, IOException{
		super( soundFile );
		soundThread.enableSpatializedSound();
	}
	
	/**
	 * This sets the position for this sound
	 * @param source A Vector3f position
	 */
	public void setSourcePosition( Vector3f source ) {
		soundThread.setSourcePosition( source );
	}
}
