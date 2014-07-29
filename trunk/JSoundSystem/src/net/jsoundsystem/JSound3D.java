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
     * A JSound3D constructor accepting a string instead of a File object
     * @param fileName the path to the file to load
     * @throws UnsupportedAudioFileException If the API cannot convert the file into an audio stream
     * @throws IOException If the file could not be read
     */
    public JSound3D(String fileName) throws UnsupportedAudioFileException, IOException {
        this(new File(fileName));
    }
	
	/**
	 * This sets the position for this sound
	 * @param source A Vector3f position
	 */
	public void setSourcePosition( Vector3f source ) {
		soundThread.setSourcePosition( source );
	}
}
