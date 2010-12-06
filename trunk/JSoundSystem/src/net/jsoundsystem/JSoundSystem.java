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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.jsoundsystem.utils.Vector3f;



/**
 * The main overlay API to create JSounds among other things.
 * Current supported sounds are OGG, WAV, AIFF, SND, AIFC, FLAC, AU and MP3
 * @author Johan Jansen
 *
 */
public abstract class JSoundSystem {
	public final static String VERSION = "1.00";

	//Sound channels
	protected static int channelsPlaying = 0;
	private static int maxChannels = 32;

	//3D sound effects
	private static Vector3f listenerPosition = new Vector3f();
	protected static float maxDistance = 800;

	/**
	 * Gets the number of channels in use
	 */
	public static int getSoundsPlaying(){
		return channelsPlaying;
	}

	/**
	 * This is a very important method in the JSoundSystem. With this method you can create JSound objects
	 * from any specified File. This function will fail if the specified sound is not supported or if
	 * the file does not exist.
	 * @param soundFile A File object pointing to the audio file you want to use
	 * @return A JSound object ready to be played
	 * @throws UnsupportedAudioFileException If the audio format is not supported by the JSoundSystem
	 * @throws IOException If the audio file could not be read
	 * @see JSound
	 */
	public static JSound createSound( File soundFile ) throws UnsupportedAudioFileException, IOException {
		return new JSound(soundFile);
	}

	/**
	 * This is a very important method in the JSoundSystem. With this method you can create JSound objects
	 * from any specified String. This function will fail if the specified sound is not supported or if
	 * the file does not exist.
	 * @param soundFile A String with the file path of the audio file you want to use
	 * @return A JSound object ready to be played
	 * @throws UnsupportedAudioFileException If the audio format is not supported by the JSoundSystem
	 * @throws IOException If the audio file could not be read
	 * @see JSound
	 */
	public static JSound createSound( String soundFile ) throws UnsupportedAudioFileException, IOException{
		return new JSound(soundFile);
	}

	/**
	 * This function sets the amount of sound channels that can be used at the same time.
	 * Sound channels define the number of sounds that can be played at the same time.
	 * The maximum limit depends on the audio drivers of the computer. The default amount is 32.
	 * @param amount The maximum amount of channels that can be allocated
	 * @throws IllegalArgumentException if amount is negative or less than the number of channels in use
	 */
	public static void setMaxChannels( int amount ){

		//No negative numbers
		if( amount < 0 ) 
			throw new IllegalArgumentException("Cannot set number of channels to negative.");

		//Dont close channels that are in use
		if( amount < channelsPlaying ) 
			throw new IllegalArgumentException("Cannot set channels to " + amount + " because " + channelsPlaying + " is already in use.");

		//All ok
		maxChannels = amount;
	}

	/**
	 * Returns true if there is at least one free channel
	 */
	public static boolean hasFreeChannels(){
		return channelsPlaying <= maxChannels;
	}


	/**
	 * This function decodes and loads sound data into memory stored in a JSoundThread ready to be played
	 * @param file
	 * @param simulate3DSound
	 * @return
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	static AudioThread createSoundThread( File file, boolean loadToMemory ) throws UnsupportedAudioFileException, IOException {
		AudioInputStream audioStream = JSoundSystem.getAudioInputStream( file );
		byte[] memoryData = null;

		if( loadToMemory ){
			// copy the AudioInputStream to a byte array which we load into memory
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int tempBytesRead = 0;
			while ((tempBytesRead = audioStream.read(buffer, 0, buffer.length)) != -1) {
				bos.write(buffer, 0, tempBytesRead);
			}
			memoryData = bos.toByteArray();
		}

		//All done!
		return new AudioThread( file, memoryData, audioStream.getFormat() );
	}

	/**
	 * Opens a file turning it into a decoded AudioInputStream
	 * @param file Which file to open
	 * @return The AudioInputStream ready to be used
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	static AudioInputStream getAudioInputStream( File file ) throws UnsupportedAudioFileException, IOException{
		//Make sure the file is actually a sound first
		if( !JSoundSystem.soundIsSupported(file) ) 
			throw new UnsupportedAudioFileException("Audio file not supported: " + file.getAbsolutePath());

		//Try to open a stream to it, we use BufferedInputStream which works with JAR files
		BufferedInputStream in = new BufferedInputStream( new FileInputStream(file) );
		AudioInputStream rawstream = AudioSystem.getAudioInputStream(in);
		AudioFormat decodedFormat = rawstream.getFormat();

		String fileName = file.getName().toLowerCase();

		//Decode it if it is in OGG Vorbis format
		if( fileName.endsWith(".ogg") ) {
			decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					decodedFormat.getSampleRate(),
					16,
					2,
					decodedFormat.getChannels() * 2,
					decodedFormat.getSampleRate(),
					false);
		}

		//Decode it if it is in MP3 format
		else if( fileName.endsWith(".mp3") ) {
			decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					decodedFormat.getSampleRate(),
					16,
					decodedFormat.getChannels(),
					decodedFormat.getChannels() * 2,
					decodedFormat.getSampleRate(),
					false);
		}

		//Convert sound from Mono to Stereo so that we can adjust panning
		/*else if(decodedFormat.getChannels() == 1 )
		{
	        decodedFormat = new AudioFormat(
                decodedFormat.getEncoding(),
                decodedFormat.getSampleRate(),
                decodedFormat.getSampleSizeInBits(),
                2,
                decodedFormat.getFrameSize(),
                decodedFormat.getFrameRate(),
                decodedFormat.isBigEndian());			
		}*/

		//Decode the sound by using the underlying SPI with the specified format
		return AudioSystem.getAudioInputStream( decodedFormat, rawstream );
	}

	/**
	 * Finds out if the specified File is supported as an AudioInputStream and if it can be used
	 * as a JSound. Returns true if it is supported or False otherwise.
	 * @throws IOException If the File cannot be read
	 */
	public static boolean soundIsSupported( File soundFile ) throws IOException {
		if( soundFile == null ) return false;
		
		try {
			AudioSystem.getAudioFileFormat( soundFile );
		} catch (UnsupportedAudioFileException e) {
			return false;
		}
		return true;
	}

	/**********************************************************************************************
	 * 3D sound simulation code beyond here
	 *********************************************************************************************/

	/**
	 * Works like the createSound( File soundFile ) except that it will return a JSound3D object. A JSound3D
	 * will automatically simulate 3D positional audio for you. You need to set the sound source, listener source
	 * and maximum sound distance for this to properly work.
	 * @param soundFile
	 * @return A JSound3D object with default source at position (0, 0)
	 * @throws UnsupportedAudioFileException If the audio format is not supported by the JSoundSystem
	 * @throws IOException If the audio file could not be read
	 * @see JSound3D
	 */
	public static JSound create3DSound( File soundFile ) throws UnsupportedAudioFileException, IOException {
		//Make sure the file is actually a sound
		if( !soundIsSupported(soundFile) ) throw new UnsupportedAudioFileException("Audio file not supported: " + soundFile.getAbsolutePath());

		return new JSound3D(soundFile);
	}

	/**
	 * This sets or changes the position of the listener. This is only used by JSound3D
	 * who use this to simulate 3D positional sounds. The default position is (0, 0, 0)
	 * @param listenerPosition A 3 dimensional x y z floating point coordinate
	 * @see JSound3D
	 */
	public static void setListenerPosition ( Vector3f listenerPosition ) {
		JSoundSystem.listenerPosition = listenerPosition;
	}

	/**
	 * This sets the maximum distance from where sounds can be heard using 3D sound simulation
	 * The default value is 800. The distance cannot be set below 1 or an IllegalArgumentException
	 * will be thrown.
	 * @param distance
	 * @see JSound3D
	 * @exception IllegalArgumentException If the distance is set to 1 or less.
	 */
	public static void setMaxDistance( float distance ){
		if( distance <= 1 ) throw new IllegalArgumentException("Distance cannot be less than 1");
		maxDistance = distance;
	}

	/**
	 * This returns the current position of the listener. The default position is (0, 0)
	 * @return listenerPosition
	 * @see JSound3D
	 */
	public static Vector3f getListenerPosition(){
		return listenerPosition;
	}

	/**
	 * Returns the current max hearing distance. Default is 800.
	 * @return
	 */
	public static float getMaxDistance(){
		return maxDistance;
	}

}
