package dev.bebomny.youtubevideodownloader;

import dev.bebomny.youtubevideodownloader.animation.DownloadAnimation;
import dev.bebomny.youtubevideodownloader.animation.FetchingAnimation;
import dev.bebomny.youtubevideodownloader.downloader.YoutubeDownloader;
import dev.bebomny.youtubevideodownloader.downloader.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.downloader.stream.YoutubeVideo;
import dev.bebomny.youtubevideodownloader.downloader.stream.download.StreamDownloader;
import javafx.scene.image.Image;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class DownloadManager {

    private final YoutubeVideoDownloaderApplication videoDownloaderApplication;
    private final DataManager dataManager;
    private final MainController mainController;

    //Fetching
    public CompletableFuture<YoutubeVideo> fetchVideoDataTask;
    public FetchingAnimation fetchingAnimation;

    //Downloading
    public StreamDownloader streamDownloader;
    public Thread downloadThread;
    public DownloadAnimation downloadAnimation;

    public DownloadManager(YoutubeVideoDownloaderApplication application) {
        this.videoDownloaderApplication = application;
        this.dataManager = application.getDataManager();
        this.mainController = videoDownloaderApplication.getMainController();
    }

    public void fetchVideoData(String url) {
        //Start the fetching Animation
        fetchingAnimation = new FetchingAnimation(videoDownloaderApplication);
        fetchingAnimation.start();

        fetchVideoDataTask = CompletableFuture.supplyAsync(() -> YoutubeDownloader.fetchVideoData(url));
    }

    public void downloadVideo(StreamOption option, File target) {
        downloadAnimation = new DownloadAnimation(videoDownloaderApplication);

        streamDownloader = new StreamDownloader(option, target, null);
        downloadThread = new Thread(streamDownloader);

        downloadAnimation.start();
        downloadThread.start();
    }
}
