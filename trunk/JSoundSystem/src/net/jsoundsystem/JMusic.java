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
 * An object for playing music. JSound and JSound3D load the entire sound into memory, which might be slow for
 * large audio files like music. The JMusic object streams the sound instead of loading it into memory.
 */
public class JMusic extends Audio {
	
	public JMusic( File file ) throws UnsupportedAudioFileException, IOException{
		super( JSoundSystem.createSoundThread(file, false) );
	}
}
