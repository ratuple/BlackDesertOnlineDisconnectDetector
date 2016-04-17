package ratuple.processexecution.utils;

import com.vnetpublishing.java.suapp.SuperUserApplication;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import ratuple.general.exceptions.ExceptionWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import com.vnetpublishing.java.suapp.SU;

import javax.swing.*;

import static ratuple.general.threads.ThreadUtils.sleep;

@Component
public class ProcessExecutioner {

    private static final Logger LOGGER = Logger.getLogger(ProcessExecutioner.class.getName());

    public List<String> executeCommandAndReadOutputUntilFinished(String command) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = null;
            pr = rt.exec(command);
            InputStream inputStream = pr.getInputStream();
            List<String> lines = IOUtils.readLines(inputStream);
            return lines;
        } catch (IOException e) {
            throw ExceptionWrapper.wrap(e);
        }
    }

    public List<String> executeCommandAsSuperuserAndReadOutputUntilFinished(String command) {
        executeAsSuperuser();
        return null;
    }

    private void executeAsSuperuser() {
        SU.run(new SuperUserApplication() {
            @Override
            public int run(String[] strings) {
                JOptionPane.showMessageDialog(null, "lel");
                LOGGER.info("lel");
                return 0;
            }
        }, new String[]{});
        sleep(10000);
    }


}
