package ratuple.general.exceptions;

public class GeneralRuntimeException extends RuntimeException {
    public GeneralRuntimeException() {
    }

    public GeneralRuntimeException(String message) {
        super(message);
    }

    public GeneralRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneralRuntimeException(Throwable cause) {
        super(cause);
    }

    public GeneralRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
