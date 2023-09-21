package dev.bebomny.youtubevideodownloader.download.status;

public enum FetchingStatus implements Status{
    READY,
    PREPARING,
    FETCHING,
    PARSING,
    COMPLETED,
    FAILURE
}
