package dev.bebomny.youtubevideodownloader;

import dev.bebomny.youtubevideodownloader.animation.DownloadAnimation;
import dev.bebomny.youtubevideodownloader.animation.FetchingAnimation;
import dev.bebomny.youtubevideodownloader.clients.ClientManager;
import dev.bebomny.youtubevideodownloader.clients.VideoDataFetcher;
import dev.bebomny.youtubevideodownloader.converters.Converter;
import dev.bebomny.youtubevideodownloader.converters.ConverterBuilder;
import dev.bebomny.youtubevideodownloader.download.SaveFormat;
import dev.bebomny.youtubevideodownloader.download.YoutubeVideo;
import dev.bebomny.youtubevideodownloader.download.status.FetchingStatus;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.downloaders.DownloadHandler;
import dev.bebomny.youtubevideodownloader.downloaders.Downloader;
import dev.bebomny.youtubevideodownloader.downloaders.DownloaderBuilder;
import dev.bebomny.youtubevideodownloader.utils.DownloadUtils;

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
    public DownloadHandler downloadHandler;
    public Thread downloadThread;
    public DownloadAnimation downloadAnimation;

    public DownloadManager(YoutubeVideoDownloaderApplication application) {
        this.videoDownloaderApplication = application;
        this.dataManager = application.getDataManager();
        this.mainController = videoDownloaderApplication.getMainController();

        //Fetching
        this.videoDataFetcher = new VideoDataFetcher(clientManager);

        //Downloading
        this.downloadHandler = new DownloadHandler(DownloadUtils.tempFolder);
    }

    public void fetchVideoData(String url) {
        //Start the fetching Animation
        videoDataFetcher.setStatus(FetchingStatus.PREPARING);
        fetchingAnimation = new FetchingAnimation(videoDownloaderApplication, videoDataFetcher);
        fetchingAnimation.start();

        fetchVideoDataTask = CompletableFuture.supplyAsync(
                () -> videoDataFetcher.fetchVideoData(url, "android") //"android", "ios", "web" //in priority order!
        );
    }

    public void downloadVideo(StreamOption videoOption, StreamOption audioOption, File targetDestination, String targetFileName, SaveFormat finalFormat) {
        //Dispatch to another thread?- yeah
        //TODO: create the correct streamdownloader here and run it in a new thread try to merge the conversion and download into one
        // reconstruct the streamdownloader class
        //Download/
        DownloaderBuilder streamDownloaderBuilder = DownloaderBuilder.builder();

        //Builder configuration - TODO: simplify later
        if(videoOption != null)
            streamDownloaderBuilder.withVideo(videoOption);

        if(audioOption != null)
            streamDownloaderBuilder.withAudio(audioOption);

        if(targetDestination != null)
            streamDownloaderBuilder.toDestination(targetDestination);

        Downloader streamDownloader = streamDownloaderBuilder
                .tempStorage(DownloadUtils.tempFolder)
                .build();

        //Converter
        ConverterBuilder converterBuilder = ConverterBuilder.builder(videoOption, null, audioOption, null, targetDestination, targetFileName, SaveFormat.MP4, DownloadUtils.tempFolder);
        Converter converter = converterBuilder.build();

        downloadHandler.setup(streamDownloader, converter, targetDestination, targetFileName, DownloadUtils.tempFolder);

        //Animation
        downloadAnimation = new DownloadAnimation(videoDownloaderApplication, downloadHandler);

        //Start downloading
        downloadThread = new Thread(downloadHandler);
        downloadAnimation.start();
        downloadThread.start();
        //TODO: Perform cleanup

        //Convert - dispatching a new thread in the download animation
        // - nope, that's not a good idea - transferring all control to DownloadHandler
    }
}
