package dev.bebomny.youtubevideodownloader.animation;

import dev.bebomny.youtubevideodownloader.DataManager;
import dev.bebomny.youtubevideodownloader.DownloadManager;
import dev.bebomny.youtubevideodownloader.MainController;
import dev.bebomny.youtubevideodownloader.YoutubeVideoDownloaderApplication;
import dev.bebomny.youtubevideodownloader.clients.VideoDataFetcher;
import dev.bebomny.youtubevideodownloader.download.YoutubeVideo;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;

import java.util.concurrent.ExecutionException;

public class FetchingAnimation extends AnimationTimer {

    private final YoutubeVideoDownloaderApplication application;
    private final DownloadManager downloadManager;
    private final VideoDataFetcher videoDataFetcher;
    private final DataManager dataManager;
    private final MainController controller;
    private final Label statusLabel;

    private final int maxDotAmount = 6;
    private int dotAmount = 0;
    private final int delay = 100;
    private long lastUpdatedTime = 0;

    public FetchingAnimation(YoutubeVideoDownloaderApplication application, VideoDataFetcher videoDataFetcher) {
        this.application = application;
        this.downloadManager = application.getDownloadManager();
        this.videoDataFetcher = videoDataFetcher;
        this.dataManager = application.getDataManager();
        this.controller = application.getMainController();
        this.statusLabel = controller.statusLabel;
    }

    @Override
    public void handle(long now) {

        //Fetching String Builder
        long currentTime = System.currentTimeMillis();

        if(currentTime - lastUpdatedTime > delay) {
            StringBuilder stringBuilder = new StringBuilder(videoDataFetcher.getStatus().name().toLowerCase());
            if(dotAmount > maxDotAmount)
                dotAmount = 0;
            stringBuilder.append(".".repeat(dotAmount++));
            String fetchStatusString = stringBuilder.toString();
            //controller.statusLabel.setText(fetchStatusString);
            statusLabel.setText(fetchStatusString);

            lastUpdatedTime = System.currentTimeMillis();
        }
        /////////////////////

        if(downloadManager.fetchVideoDataTask.isDone())
            return;

        YoutubeVideo video;

        try {
            video = downloadManager.fetchVideoDataTask.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return;
        }

        if(video == null)
            return;

        //load into "cache"
        dataManager.setCurrentVideo(video);

        controller.updateDisplayedInfo();

        //controller.statusLabel.setText("Video Data Acquired");
        statusLabel.setText("Video Data Acquired");
        this.stop();
    }
}
