package net.jsoundsystem;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

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
	
	JSound3D(JSoundThread thread ) {
		super(thread);
		thread.setSourcePosition( new Point2D.Float() );
	}
	
	/**
	 * A constructor for the JSound3D object. This is same as calling JSoundSystem.create3DSound( File soundFile )
	 * @param soundFile The file you want to play as an audio file.
	 * @throws UnsupportedAudioFileException If the API cannot convert the file into an audio stream
	 * @throws IOException If the file could not be read
	 */
	public JSound3D( File soundFile ) throws UnsupportedAudioFileException, IOException{
		super( JSoundSystem.createSoundThread(soundFile, true) );
		soundThread.setSourcePosition( new Point2D.Float() );
		
		//Make sure the file is actually a sound
		if( !JSoundSystem.soundIsSupported(soundFile) ) 
			throw new UnsupportedAudioFileException("Audio file not supported: " + soundFile.getAbsolutePath());
	}
	
	/**
	 * This sets the position for this sound
	 * @param source A Point2D.Float position
	 */
	public void setSourcePosition( Point2D.Float source ) {
		soundThread.setSourcePosition( source );
	}
}
