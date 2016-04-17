package ratuple.processexecution.functions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ratuple.processexecution.utils.ProcessExecutioner;

import java.util.List;

@Component
public class NetworkConnections {

    @Autowired
    private ProcessExecutioner processExecutioner;

    public List<String> retrieveActiveConnections() {
        List<String> outputLines = processExecutioner.executeCommandAndReadOutputUntilFinished("netstat");
        return outputLines;
    }

    /**
     * Requires elevated rights.
     * @return
     */
    public List<String> retrieveActiveConnectionsWithAssociatedApplication() {
        List<String> outputLines = processExecutioner.executeCommandAndReadOutputUntilFinished("netstat -b");
        return outputLines;
    }
}
