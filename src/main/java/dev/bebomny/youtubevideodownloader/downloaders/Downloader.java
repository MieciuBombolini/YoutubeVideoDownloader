package dev.bebomny.youtubevideodownloader.downloaders;

import dev.bebomny.youtubevideodownloader.download.StreamMedium;
import dev.bebomny.youtubevideodownloader.download.status.DownloadStatus;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;

import java.io.File;
import java.util.Map;

public interface Downloader{ //extends Runnable

    Map<StreamMedium, File> downloadSingle();

    Map<StreamMedium, File> downloadMulti();

    DownloadStatus getStatus();

    StreamOption getAudioOption();

    StreamOption getVideoOption();

    long getTotalLength();

    long getStartTimeMillis();

    long getTotalBytesRead();

    void setTempFolder(File newTempFolder);

    File getTempFolder();

    default Map<StreamMedium, File> downloadStream() {
        if(isMulti())
            return downloadMulti();
        else
            return downloadSingle();
    }

    default boolean isMulti() {
        return getVideoOption() != null && getAudioOption() != null;
    }

    default boolean isFinished() {
        return getStatus() == DownloadStatus.DONE || getStatus() == DownloadStatus.FAILURE;
    }

    default double getDownloadSpeed() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - getStartTimeMillis();

        double downloadSpeed = (double) getTotalBytesRead() / (elapsedTime / 1000.0); // bytes per second
//        double downloadSpeedKilobytes = downloadSpeed / 1024;
//        double downloadSpeedMegabytes = downloadSpeedKilobytes / 1024;
        return downloadSpeed;
    }
}
