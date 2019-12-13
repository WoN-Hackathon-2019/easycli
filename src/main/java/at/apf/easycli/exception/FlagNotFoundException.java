package at.apf.easycli.exception;

public class FlagNotFoundException extends RuntimeException {

    public FlagNotFoundException() {
    }

    public FlagNotFoundException(String message) {
        super(message);
    }

    public FlagNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlagNotFoundException(Throwable cause) {
        super(cause);
    }

    public FlagNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
