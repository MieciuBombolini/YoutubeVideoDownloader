package dev.bebomny.youtubevideodownloader;

import dev.bebomny.youtubevideodownloader.animation.DownloadAnimation;
import dev.bebomny.youtubevideodownloader.animation.FetchingAnimation;
import dev.bebomny.youtubevideodownloader.clients.ClientManager;
import dev.bebomny.youtubevideodownloader.clients.VideoDataFetcher;
import dev.bebomny.youtubevideodownloader.download.SaveFormat;
import dev.bebomny.youtubevideodownloader.download.YoutubeVideo;
import dev.bebomny.youtubevideodownloader.download.status.FetchingStatus;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.downloaders.StreamDownloader;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DownloadManager {

    private static final ClientManager clientManager = new ClientManager();
    private final YoutubeVideoDownloaderApplication videoDownloaderApplication;
    private final DataManager dataManager;
    private final MainController mainController;


    //Fetching
    public CompletableFuture<YoutubeVideo> fetchVideoDataTask;
    public FetchingAnimation fetchingAnimation;
    public VideoDataFetcher videoDataFetcher;

    //Downloading
    public StreamDownloader streamDownloader;
    public Thread downloadThread;
    public DownloadAnimation downloadAnimation;

    public DownloadManager(YoutubeVideoDownloaderApplication application) {
        this.videoDownloaderApplication = application;
        this.dataManager = application.getDataManager();
        this.mainController = videoDownloaderApplication.getMainController();

        //Fetching
        videoDataFetcher = new VideoDataFetcher(clientManager);

        //Downloading
        streamDownloader = new StreamDownloader();
    }

    public void fetchVideoData(String url) {
        //Start the fetching Animation
        videoDataFetcher.setStatus(FetchingStatus.PREPARING);
        fetchingAnimation = new FetchingAnimation(videoDownloaderApplication);
        fetchingAnimation.start();

        fetchVideoDataTask = CompletableFuture.supplyAsync(
                () -> videoDataFetcher.fetchVideoData(url, "android") //"android", "ios", "web" //in priority order!
        );
    }

    public void downloadVideo(StreamOption videoOption, StreamOption audioOption, File destination, SaveFormat finalFormat) {
        //Dispatch to another thread?- yeah
        //TODO: create the correct streamdownloader here and run it in a new thread try to merge the conversion and download into one
        // reconstruct the streamdownloader class
        //Download

        //Convert - dispatching a new thread in the download animation
    }
}
