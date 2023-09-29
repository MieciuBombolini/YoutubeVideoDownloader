package dev.bebomny.youtubevideodownloader.animation;

import dev.bebomny.youtubevideodownloader.DataManager;
import dev.bebomny.youtubevideodownloader.DownloadManager;
import dev.bebomny.youtubevideodownloader.MainController;
import dev.bebomny.youtubevideodownloader.YoutubeVideoDownloaderApplication;
import dev.bebomny.youtubevideodownloader.downloaders.DownloadHandler;
import dev.bebomny.youtubevideodownloader.utils.DownloadUtils;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;


public class DownloadAnimation extends AnimationTimer {

    private final YoutubeVideoDownloaderApplication application;
    //private final DownloadManager downloadManager;
    private final DownloadHandler downloadHandler;
    //private final DataManager dataManager;
    private final MainController controller;
    private final Label statusLabel;
    private final Label downloadSpeedLabel;

    private final int maxDotAmount = 6;
    private int dotAmount = 0;
    private final int delay = 100;
    private long lastUpdatedTime = 0;

    public DownloadAnimation(YoutubeVideoDownloaderApplication application, DownloadHandler downloadHandler) {
        this.application = application;
        //this.downloadManager = application.getDownloadManager();
        this.downloadHandler = downloadHandler;
        //this.dataManager = application.getDataManager();
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
            StringBuilder stringBuilder = new StringBuilder(downloadHandler.getStatus().name().toLowerCase());
            if(dotAmount > maxDotAmount)
                dotAmount = 0;
            stringBuilder.append(".".repeat(dotAmount++));
            String fetchStatusString = stringBuilder.toString();
            statusLabel.setText(fetchStatusString);
            String downloadSpeed = DownloadUtils.getFormatedDownloadSpeed(downloadHandler.getDownloadSpeed());
            downloadSpeedLabel.setText(downloadSpeed);

            lastUpdatedTime = System.currentTimeMillis();
        }
        /////////////////////

        if(!downloadHandler.isFinished())
            return;


        statusLabel.setText("Video Downloaded Successfully");
        this.stop();
    }
}
