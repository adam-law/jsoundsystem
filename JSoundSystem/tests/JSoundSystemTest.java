import net.jsoundsystem.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

class JSoundSystemTest {

    private static void testSound(String sound) {
        System.out.println("Testing " + sound + " sound effect.");

        try {
            JSound wav = new JSound("Test Audio/" + sound);
            wav.play();
            Thread.sleep(3000);
        }
        catch(Exception ex)
        {
            System.err.println("Failed! " + ex);
        }
    }

    public static void main(String[] args) {
        testSound("testSound.ogg");
        testSound("testSound.flac");
        testSound("testSound.mp3");
        testSound("testSound.aiff");
    }

}