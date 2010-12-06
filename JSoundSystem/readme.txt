JSoundSystem quick install guide
-----------------------------------

Simply add JSoundSystem-1.1.2.jar (or any later version) to your project, right click it 
and add it to build path. Now you can use JSound objects to play sounds.

	JSound mySound = new JSound( "mysound.wav" );
	mySound.play();

That's it!

If you want support for MP3 sounds you will also need to copy mp3spi1.9.5.jar and add it to your
build path. This is because the MP3 decoder is not under a free license and you might not want to
include it with your project



Using JSoundSystem audio effects
-----------------------------------
The JSoundSystem supports various simple effects you can add to your sound. This includes simulated
3D sounds or playback speed or something as basic as volume or panning.

	JSound mySound = new JSound( "mysound.ogg" );
	mySound.setVolume( 0.75f );
	mySound.setSpeed( 2.00f );
	mySound.setLoop( true );
	mySound.play();

The sound will now play at double speed with 25% less volume in an infinite loop until you call
mySound.stop() or mySound.pause().


3D sound emulation works is done through the JSound3D object. Note that changing volume or panning
for JSound3D objects won't do anything useful because this handled automatically by the JSoundSystem.
To use a JSound3D you need first to set positions of the listener, position of the source and the maximum
hearing distance. Setting the position of the listener usually only needs to be done once unless you change
who is the listener.

	//Setup positions of the listener and source
	Point2D.Float listenerPosition = new Point2D.Float( 400, 300 );
	Point2D.Float sourcePosition = new Point2D.Float( 200, 150 );

	//Initialize the listener, this only needs to be done once
	JSoundSystem.setMaxDistance( 800 );
	JSoundSystem.setListenerPosition( listenerPosition );
	
	//Now load and play the audio file
	File myAudioFile = new File( "mysound.mp3" );
	JSound3D my3dSound = new JSound3D( myAudioFile );
	my3dSound.setSourcePosition( sourcePosition );
	my3dSound.setLoop(true);
	my3dSound.play();

The 3D emulation in the JSoundSystem currently uses Point2D.Float as the position for the source and listener.
For 3D sounds to be automatically updated when either your listener or source moves, you need to make sure that
any movement that is done is also done to listenerPosition and sourcePosition variables. You do not need to
reset the positions each time you move if you have referenced the Point2D variable correctly..




Other
-----------------------------------

JSoundSystem currently supports the following audio formats:
- WAVE
- MP3 (note you have to copy mp3spi1.9.5.jar for MP3 support)
- OGG
- FLAC
- AIFF
- AU
- AIFC
- SND

JSoundSystem is licensed under the GPL v3.0 and implements some SPI libraries 
from JavaZoom to decode OGG and MP3 sound files.

Get the latest version at: http://code.google.com/p/jsoundsystem/
Read the javadoc included for more detailed documentation.

JSoundSystem was written by Johan Jansen - 2010
Contact me at: zefzsoftwares@hotmail.com for suggestions or questions about JSoundSystem




Changelog
-------------------------------------

1.2.0 (06.12.10)
- NEW FEATURE: Added new JMusic object that streams audio instead of loading them into memory. This is useful
  for large sound files like music that take time to load into memory.
- NEW FEATURE: Added new clone() and getAudioFormat() methods for JSound objects. The clone() method can
   be used to duplicate and play the same sound in different threads while only loading the sound into 
   memory once.
- UPDATE: JSoundSystem now properly uses actual 3d vectors for positions instead of 2d.
- UPDATE: Renamed the package to accommodate with the Java standard convention.
- UPDATE: Large optimizations by loading whole sounds into memory instead of keeping open data streams.
- UPDATE: Various other optimizations.
- BUGFIX: Fixed possible null pointer exception when using JSound3D

1.1.0 (06.11.10)
- NEW FEATURE: Added basic support for 3D sound simulation using the JSound3D object
- NEW FEATURE: Added support for changing sound volume, speed and panning. This works even while 
  the sound is playing
- UPDATE: Made it even simpler to create JSound and JSound3D objects
- UPDATE: The MP3 decoder is now shipped seperately from JSoundSystem so that you can use JSoundSystem
  without requiring the MP3 decoder which is not under a free license
- UPDATE: Added more documentation
- BUGFIX: Fixed delay when pausing or stopping sounds
- BUGFIX: Fixed various minor bugs

1.0.0 (04.11.10)
- Initial release