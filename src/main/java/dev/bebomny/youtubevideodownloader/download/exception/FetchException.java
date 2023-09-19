package dev.bebomny.youtubevideodownloader.download.exception;

public class FetchException extends RuntimeException{

    public FetchException(String message, Stage stage) {
        super("At " + stage.name() + " | " + message);
    }

    public FetchException(String message, Stage stage, Throwable cause) {
        super("At " + stage.name() + " | " + message, cause);
    }

    public enum Stage {
        CONNECTION,
        EXTRACTION,
        FORMAT_PARSING
    }
}
