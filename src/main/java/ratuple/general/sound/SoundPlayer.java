package ratuple.general.sound;

import org.springframework.stereotype.Component;
import ratuple.general.exceptions.ExceptionWrapper;

import javax.sound.sampled.*;
import java.io.*;

@Component
public class SoundPlayer {

    public Clip playSound(String soundFileResource) {
        Clip clip = createClip(getResourceAsStream(soundFileResource));
        clip.start();
        return clip;
    }

    public Clip playSoundInLoopForClasspathResource(String soundFileResource, int times) {
        return playSoundInLoopForFile(getResourceAsStream(soundFileResource), times);
    }

    public Clip playSoundInLoopForFile(String file, int times) {
        return playSoundInLoopForFile(readFile(file), times);
    }

    private Clip playSoundInLoopForFile(InputStream soundFileData, int times) {
        Clip clip = createClip(soundFileData);
        clip.setLoopPoints(0, -1);
        clip.loop(times);
        clip.start();
        return clip;
    }

    private Clip createClip(InputStream soundFileData) {
        try {
            Clip clip = AudioSystem.getClip();
            BufferedInputStream resetableSoundInputStream = new BufferedInputStream(soundFileData);
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(resetableSoundInputStream);
            clip.open(inputStream);
            return clip;
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            throw ExceptionWrapper.wrap(e);
        }
    }

    private FileInputStream readFile(String file) {
        return readFile(new File(file));
    }

    private FileInputStream readFile(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw ExceptionWrapper.wrap(e);
        }
    }

    private InputStream getResourceAsStream(String resource) {
        InputStream soundFile = SoundPlayer.class.getResourceAsStream(resource);
        if (soundFile == null) {
            throw new RuntimeException("resource " + resource + " not found in classpath");
        }
        return soundFile;
    }

}
