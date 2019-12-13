package at.apf.easycli.exception;

public class MalformedMethodException extends RuntimeException {
    public MalformedMethodException() {
    }

    public MalformedMethodException(String message) {
        super(message);
    }

    public MalformedMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedMethodException(Throwable cause) {
        super(cause);
    }

    public MalformedMethodException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
