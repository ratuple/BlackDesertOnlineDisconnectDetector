package ratuple.general.threads;

public class ThreadUtils {
    public static void sleep(long sleepMillis) {
        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
        }
    }
}
