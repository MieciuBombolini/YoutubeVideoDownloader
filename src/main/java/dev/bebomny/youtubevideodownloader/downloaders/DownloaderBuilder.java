package dev.bebomny.youtubevideodownloader.downloaders;

import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.utils.DownloadUtils;

import java.io.File;

public class DownloaderBuilder {
    private StreamOption audioOption; //contentLength can be -1! check for it!
    private StreamOption videoOption;
    private File destination;
    private File tempFolder;
    //private boolean fragmented;

    public DownloaderBuilder() {
        this.audioOption = null;
        this.videoOption = null;
        this.destination = DownloadUtils.tempFolder;
        this.tempFolder = DownloadUtils.tempFolder;
        //this.fragmented = false;
    }

    public static DownloaderBuilder builder() {
        return new DownloaderBuilder();
    }

    public DownloaderBuilder withAudio(StreamOption audio) {
        this.audioOption = audio;
        return this;
    }

    public DownloaderBuilder withVideo(StreamOption video) {
        this.videoOption = video;
        return this;
    }

    public DownloaderBuilder toDestination(File destination) {
        this.destination = destination;
        return this;
    }

    public DownloaderBuilder tempStorage(File tempLocation) {
        this.tempFolder = tempLocation;
        return this;
    }

//    public DownloaderBuilder inFragments(boolean fragmented) {
//        this.fragmented = fragmented;
//        return this;
//    }

    public Downloader build() {

        return new DefaultDownloader(audioOption, videoOption, tempFolder); //Placeholder!!!
    }
}
