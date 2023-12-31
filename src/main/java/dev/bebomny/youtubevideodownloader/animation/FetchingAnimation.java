package dev.bebomny.youtubevideodownloader.animation;

import dev.bebomny.youtubevideodownloader.DataManager;
import dev.bebomny.youtubevideodownloader.DownloadManager;
import dev.bebomny.youtubevideodownloader.MainController;
import dev.bebomny.youtubevideodownloader.YoutubeVideoDownloaderApplication;
import dev.bebomny.youtubevideodownloader.downloader.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.downloader.stream.YoutubeVideo;
import dev.bebomny.youtubevideodownloader.downloader.utils.FileNameSanitizer;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FetchingAnimation extends AnimationTimer {

    private final YoutubeVideoDownloaderApplication application;
    private final DownloadManager downloadManager;
    private final DataManager dataManager;
    private final MainController controller;
    private final Label statusLabel;

    private final int maxDotAmount = 6;
    private int dotAmount = 0;
    private final int delay = 100;
    private long lastUpdatedTime = 0;

    public FetchingAnimation(YoutubeVideoDownloaderApplication application) {
        this.application = application;
        this.downloadManager = application.getDownloadManager();
        this.dataManager = application.getDataManager();
        this.controller = application.getMainController();
        this.statusLabel = controller.statusLabel;
    }

    @Override
    public void handle(long now) {

        //Fetching String Builder
        long currentTime = System.currentTimeMillis();

        if(currentTime - lastUpdatedTime > delay) {
            StringBuilder stringBuilder = new StringBuilder("Fetching");
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


    private Integer extractQuality(MenuItem menuItem) {
        String text = menuItem.getText();

        if(text.startsWith("p")) {
            //video and fps values
            int videoValue = 4 * Integer.parseInt(text.substring(1, text.indexOf('f')));
            int fpsValue = 2 * Integer.parseInt(text.substring(text.indexOf('f') + 1, text.indexOf('f') + 3));
            int formatValue = text.contains("mp4") ? 50 : 0;

            //adding a 1000 so video qualities always appear above audio
            int qualityValue = 1000 + videoValue + fpsValue + formatValue;
            return -qualityValue;
        } else if(text.startsWith("k")) {
            int audioValue = Integer.parseInt(text.substring(1, text.indexOf('.')));
            return -audioValue;
        }
        return Integer.MAX_VALUE;
    }
}
