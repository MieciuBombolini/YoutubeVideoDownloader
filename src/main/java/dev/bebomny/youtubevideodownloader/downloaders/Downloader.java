package dev.bebomny.youtubevideodownloader.downloaders;

import dev.bebomny.youtubevideodownloader.download.status.DownloadStatus;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;

import java.io.File;
import java.util.List;

public interface Downloader {

    List<File> download();

    DownloadStatus getStatus();

    StreamOption getOption();

    File getDestination();

    long getLength();

    long getStartTimeMillis();

    long getBytesRead();

    default boolean isFinished() {
        return getStatus() == DownloadStatus.DONE || getStatus() == DownloadStatus.FAILURE;
    }

    default double getDownloadSpeed() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - getStartTimeMillis();

        double downloadSpeed = (double) getBytesRead() / (elapsedTime / 1000.0); // bytes per second
        double downloadSpeedKilobytes = downloadSpeed / 1024;
        double downloadSpeedMegabytes = downloadSpeedKilobytes / 1024;
        return downloadSpeedMegabytes;
    }
}
