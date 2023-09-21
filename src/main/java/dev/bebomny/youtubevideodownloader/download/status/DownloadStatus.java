package dev.bebomny.youtubevideodownloader.download.status;

public enum DownloadStatus implements Status{
    READY,
    PREPARING,
    DOWNLOADING,
    MERGING,
    CONVERTING,
    DONE,
    FAILURE
}
