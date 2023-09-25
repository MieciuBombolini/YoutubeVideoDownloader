package dev.bebomny.youtubevideodownloader.animation;

import dev.bebomny.youtubevideodownloader.DataManager;
import dev.bebomny.youtubevideodownloader.DownloadManager;
import dev.bebomny.youtubevideodownloader.MainController;
import dev.bebomny.youtubevideodownloader.YoutubeVideoDownloaderApplication;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;


public class DownloadAnimation extends AnimationTimer {

    private final YoutubeVideoDownloaderApplication application;
    private final DownloadManager downloadManager;
    private final DataManager dataManager;
    private final MainController controller;
    private final Label statusLabel;
    private final Label downloadSpeedLabel;

    private final int maxDotAmount = 6;
    private int dotAmount = 0;
    private final int delay = 100;
    private long lastUpdatedTime = 0;

    public DownloadAnimation(YoutubeVideoDownloaderApplication application) {
        this.application = application;
        this.downloadManager = application.getDownloadManager();
        this.dataManager = application.getDataManager();
        this.controller = application.getMainController();
        this.statusLabel = controller.statusLabel;
        this.downloadSpeedLabel = controller.downloadSpeedLabel;
    }

    @Override
    public void handle(long now) {

        //Downloading String Builder
        //TODO: Add merging and conversion!
        long currentTime = System.currentTimeMillis();

        if(currentTime - lastUpdatedTime > delay) {
            StringBuilder stringBuilder = new StringBuilder("Downloading");
            if(dotAmount > maxDotAmount)
                dotAmount = 0;
            stringBuilder.append(".".repeat(dotAmount++));
            String fetchStatusString = stringBuilder.toString();
            statusLabel.setText(fetchStatusString);
            String downloadSpeed = String.format("%.3g", downloadManager.streamDownloader.getDownloadSpeed()) + "MBps";
            downloadSpeedLabel.setText(downloadSpeed);

            lastUpdatedTime = System.currentTimeMillis();
        }
        /////////////////////

        if(!downloadManager.streamDownloader.isDownloadAndConversionFinished())
            return;

        //controller.statusLabel.setText("Video Data Acquired");
        statusLabel.setText("Video Downloaded Successfully");
        this.stop();


        //TODO: FIX!
        //String downloadSpeedString = String.format("%.3g", controller.downloadNotifier.getDownloadSpeedKbps()) + "Kbps";
        //controller.downloadSpeedLabel.setText(downloadSpeedString);

    }
}
