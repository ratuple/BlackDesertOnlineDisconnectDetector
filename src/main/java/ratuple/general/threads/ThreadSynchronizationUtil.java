package ratuple.general.threads;

import org.springframework.stereotype.Component;

@Component
public class ThreadSynchronizationUtil {

    private ThreadLocal<Object> threadLocalLock = new ThreadLocal<>();

    public Object getThreadLock() {
        Object lock = threadLocalLock.get();
        if (lock == null) {
            lock = new Object();
            threadLocalLock.set(lock);
        }
        return lock;
    }

    public void clearThreadLock() {
        threadLocalLock.remove();
    }
}
