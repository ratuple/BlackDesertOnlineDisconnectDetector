package ratuple.scripts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import ratuple.general.sound.SoundPlayer;
import ratuple.general.threads.ThreadSynchronizationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ratuple.processexecution.functions.NetworkConnections;
import ratuple.processexecution.utils.ProcessExecutioner;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.logging.Logger;

import static ratuple.general.threads.ThreadUtils.sleep;

@Component
@PropertySource(value = "file:config.properties")
public class BlackDesertNetworkConnectionPoller {

    private static final Logger LOGGER = Logger.getLogger(BlackDesertNetworkConnectionPoller.class.getName());

    @Value("${soundFile}")
    private String soundFile;

    @Value("${soundLoopCount}")
    private Integer soundLoopCount;

    @Value("${connectionSearchTerm}")
    private String connectionSearchTerm;

    @Value("${pauseBetweenScansInSeconds}")
    private long pauseBetweenScansInSeconds;

    @Value("${pollingCommand}")
    private String pollingCommand;

    @Value("${showGUI}")
    private boolean showGUI;

    @Autowired
    private NetworkConnections networkConnections;

    @Autowired
    private SoundPlayer soundPlayer;

    @Autowired
    private ThreadSynchronizationUtil threadSynchronizationUtil;

    @Autowired
    private ProcessExecutioner processExecutioner;

    public void playSoundWhenBlackDesertHasDisconnected() {
        JFrame statusGui = null;
        if (showGUI) {
            statusGui = showGui();
        }

        waitForBlackDesertToDisconnect();

        Clip clip = soundPlayer.playSoundInLoopForFile(soundFile, soundLoopCount);

        Object threadLock = threadSynchronizationUtil.getThreadLock();
        addListenerToSoundPlayerToStopProgramWhenTheLoopHasFinished(clip, threadLock);
        showNotificationAsynchronously(threadLock);
        waitForNotificationOfStoppedSoundOrUserInteraction(threadLock);

        clip.stop();

        LOGGER.info("black desert has disconnected and then either the sound loop has ended " +
                "or the notification has been closed -> closing program");
        if (statusGui != null) {
            LOGGER.info("closing GUI");
            statusGui.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            statusGui.dispose();
        }
    }

    private JFrame showGui() {
        JFrame statusWindow = new JFrame();
        statusWindow.setTitle("Black Desert Online Disconnect Detector");
        statusWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                LOGGER.info("======== killing application because i am too lazy to make a normal shutdown =======");
                System.exit(0);
            }
        });
        statusWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        statusWindow.getContentPane().setLayout(new GridLayout(1, 1));
        statusWindow.getContentPane().add(new JLabel("<html>" +
                "Close this window to close the detector.<br />" +
                "This application will run until a Black Desert disconnect.<br />" +
                "The detection of the disconnect can have a delay of multiple minutes (normally ~1 minute.)<br />" +
                "You can use a different sound file by replacing the one in the \"sounds\" directory.<br />" +
                "Some settings can be configured in the file config.properties.<br />" +
                "Execute this application using the command line for log output.<br />" +
                "</html>"));

        statusWindow.pack();
        statusWindow.setSize(540, statusWindow.getHeight() + 50);
        statusWindow.setLocationByPlatform(true);
        statusWindow.setVisible(true);

        return statusWindow;
    }

    private void showNotificationAsynchronously(Object threadLock) {
        new Thread(() -> {
            JOptionPane.showMessageDialog(null, "Black Desert has disconnected!");
            notifyMainThreadToStop(threadLock);
        }).start();
    }

    private void addListenerToSoundPlayerToStopProgramWhenTheLoopHasFinished(Clip clip, Object threadLock) {
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.CLOSE) {
                notifyMainThreadToStop(threadLock);
            }
        });
    }

    private void notifyMainThreadToStop(Object threadLock) {
        synchronized (threadLock) {
            threadLock.notifyAll();
        }
    }

    private void waitForNotificationOfStoppedSoundOrUserInteraction(Object threadLock) {
        synchronized (threadLock) {
            try {
                threadLock.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    private void waitForBlackDesertToDisconnect() {
        while (doesBlackDesertHaveAnActiveConnection()) {
            if (pauseBetweenScansInSeconds > 0) {
                sleep(pauseBetweenScansInSeconds * 1000);
            }
        }
    }

    private boolean doesBlackDesertHaveAnActiveConnection() {
//        if (true) return false;
        LOGGER.info("======== start command \"" + pollingCommand + "\" =======");
        List<String> output = processExecutioner.executeCommandAndReadOutputUntilFinished(pollingCommand);
        LOGGER.info("======== command \"" + pollingCommand + "\" output =======");
        output.stream().forEach(s1 -> LOGGER.info(s1));
        LOGGER.info("======== command end \"" + pollingCommand + "\" output =======");
        return output.stream()
                .filter(s -> s.toLowerCase().contains(connectionSearchTerm.toLowerCase()))
                .findFirst().isPresent();
    }
}
