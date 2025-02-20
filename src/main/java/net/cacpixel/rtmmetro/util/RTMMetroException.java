package net.cacpixel.rtmmetro.util;

public class RTMMetroException extends RuntimeException
{
    public RTMMetroException() {
        super();
    }

    public RTMMetroException(String message) {
        super(message);
    }

    public RTMMetroException(String message, Throwable cause) {
        super(message, cause);
    }

    public RTMMetroException(Throwable cause) {
        super(cause);
    }

    protected RTMMetroException(String message, Throwable cause,
                                boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
