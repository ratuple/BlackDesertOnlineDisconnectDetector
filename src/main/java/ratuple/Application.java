package ratuple;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import ratuple.scripts.BlackDesertNetworkConnectionPoller;

import java.io.IOException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws IOException {


        ApplicationContext ctx = new SpringApplicationBuilder(Application.class)
                .headless(false)
                .run(args);

        BlackDesertNetworkConnectionPoller networkConnectionsPoller = ctx.getBean(BlackDesertNetworkConnectionPoller.class);
        networkConnectionsPoller.playSoundWhenBlackDesertHasDisconnected();
    }

}
