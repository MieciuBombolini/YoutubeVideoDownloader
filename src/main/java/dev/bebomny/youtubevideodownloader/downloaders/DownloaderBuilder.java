package dev.bebomny.youtubevideodownloader.downloaders;

import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.utils.DownloadUtils;

import java.io.File;

public class DownloaderBuilder {
    private StreamOption streamOption; //contentLength can be -1! check for it!
    private File destination;
    private boolean fragmented;
    private boolean isVideo;
    private boolean isAudio;

    public DownloaderBuilder() {
        this.streamOption = null;
        this.destination = DownloadUtils.realTempFolder;
        this.fragmented = false;
        this.isAudio = false;
        this.isVideo = false;
    }

    public DownloaderBuilder option(StreamOption option) {
        this.streamOption = option;
        return this;
    }

    public DownloaderBuilder toDestination(File destination) {
        this.destination = destination;
        return this;
    }

    public DownloaderBuilder type(boolean audio, boolean video) {
        audio(audio);
        video(video);
        return this;
    }

    public DownloaderBuilder audio(boolean isAudio) {
        this.isAudio = isAudio;
        return this;
    }

    public DownloaderBuilder video(boolean isVideo) {
        this.isVideo = isVideo;
        return this;
    }

    public DownloaderBuilder inFragments(boolean fragmented) {
        this.fragmented = fragmented;
        return this;
    }

    public Downloader build() {
        return new DefaultDownloader(streamOption, destination); //Placeholder!!!
    }
}
