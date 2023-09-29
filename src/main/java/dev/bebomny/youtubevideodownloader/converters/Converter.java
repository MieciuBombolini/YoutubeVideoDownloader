package dev.bebomny.youtubevideodownloader.converters;

import dev.bebomny.youtubevideodownloader.download.status.DownloadStatus;

import java.io.File;

public interface Converter {

    void convert(File videoFile, File audioFile);

    DownloadStatus getStatus();
}
