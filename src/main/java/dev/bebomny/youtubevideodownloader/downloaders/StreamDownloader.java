package dev.bebomny.youtubevideodownloader.downloaders;

import dev.bebomny.youtubevideodownloader.download.SaveFormat;
import dev.bebomny.youtubevideodownloader.download.exception.DownloadException;
import dev.bebomny.youtubevideodownloader.download.status.DownloadStatus;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.utils.DownloadUtils;

import java.io.File;

public class StreamDownloader implements Runnable{

    private final StreamOption audioOption;
    private final StreamOption videoOption;
    private final File destination;
    private final SaveFormat finalFormat;
    private DownloadStatus status;

    private final File tempFolder;

    //Actual Download

    //Conversion -- to be done

    public StreamDownloader(StreamOption audio, StreamOption video, File destination, SaveFormat finalFormat) {
        this.audioOption = audio;
        this.videoOption = video;
        this.destination = destination;
        this.finalFormat = finalFormat;
        this.tempFolder = DownloadUtils.tempFolder; //this.tempFolder = DownloadUtils.realTempFolder;
        this.status = DownloadStatus.READY;
    }

    @Override
    public void run() {
        if(status != DownloadStatus.READY) throw new DownloadException("Instance already running!");
        this.status = DownloadStatus.PREPARING;

        //create temp folder
        if(!this.tempFolder.exists())
            if(this.tempFolder.mkdirs())
                System.out.println("Created temp directory at: " + this.tempFolder.getAbsolutePath());
        System.out.println("Temp folder already exists");

        if(DownloadUtils.clearDirectory(tempFolder))
            System.out.println("Directory cleared successfully");
        else
            System.out.println("Failed to clear TempDirectory");
    }

    private File downloadStreamOption(StreamOption option, File destination) {
        return;
    }

    public boolean isDownloadAndConversionFinished() {
        return status == DownloadStatus.DONE || status == DownloadStatus.FAILURE;
    }


}
