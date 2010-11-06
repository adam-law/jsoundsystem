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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;


/**
 * The main overlay API to create JSounds among other things.
 * Current supported sounds are OGG, WAV, AIFF, AU and MP3
 * @author Johan Jansen
 *
 */
public abstract class JSoundSystem {
	
	//Global settings
	protected static int channelsPlaying = 0;
	private static int maxChannels = 32;
	public final static String VERSION = "1.00";
	
	/**
	 * Gets the number of channels in use
	 */
	public static int getSoundsPlaying(){
		return channelsPlaying;
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
	
	public static JSound createSound( File soundFile ) throws UnsupportedAudioFileException, IOException {
		//Make sure the file is actually a sound
		if( !soundIsSupported(soundFile) ) throw new UnsupportedAudioFileException("Audio file not supported: " + soundFile.getAbsolutePath());
			
		//Create a new thread for this sound to be played within
		JSoundThread thread = new JSoundThread( soundFile.getName(), soundFile, 1.00f, 0, false );

		return new JSound(thread);
	}
	
	/**
	 * Turns a File into a ready formated and decoded AudioInputStream that the JSoundSystem API can play
	 * @param file the File to open
	 * @throws UnsupportedAudioFileException If the specified audio file is not supported by the API.
	 * @throws IOException If the specified file could not be read.
	 */
	public static AudioInputStream getAudioStream(File file) throws UnsupportedAudioFileException, IOException {
		
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
		
        //Get AudioInputStream that will be decoded by underlying SPI using the specified format
        return AudioSystem.getAudioInputStream(decodedFormat, rawstream);
	}
	
	/**
	 * Finds out if the specified File is supported as an AudioInputStream and if it can be used
	 * as a JSound. Returns true if it is supported or False otherwise.
	 * @throws IOException If the File cannot be read
	 */
	public static boolean soundIsSupported( File soundFile ) throws IOException {
		try {
			AudioSystem.getAudioFileFormat( soundFile );
		} catch (UnsupportedAudioFileException e) {
			return false;
		}
		return true;
	}
}
