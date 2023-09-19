package dev.bebomny.youtubevideodownloader;

import dev.bebomny.youtubevideodownloader.download.StreamOption;
import dev.bebomny.youtubevideodownloader.downloader.stream.YoutubeVideo;
import javafx.scene.image.Image;

public class DataManager {

    private final YoutubeVideoDownloaderApplication videoDownloaderApplication;
    private final MainController controller;
    private YoutubeVideo currentVideo;
    private StreamOption chosenVideoOption;
    private StreamOption chosenAudioOption;
    private YoutubeVideo.Thumbnail currentThumbnail;
    private Image thumbnailImage;

    public DataManager(YoutubeVideoDownloaderApplication application) {
        this.videoDownloaderApplication = application;
        this.controller = application.getMainController();
    }

    public void setCurrentVideo(YoutubeVideo currentVideo) {
        this.currentVideo = currentVideo;
        this.currentThumbnail = currentVideo.getBestQualityThumbnail();
        System.out.println("Thumbnail Resolution:" + currentThumbnail.getWidth() + "x" + currentThumbnail.getHeight());
        this.thumbnailImage = new Image(currentThumbnail.getUrl(), true);
    }

    public void clearCurrentVideo() {
        currentVideo = null;
        chosenVideoOption = null;
        chosenAudioOption = null;
        currentThumbnail = null;
        thumbnailImage = null;
        System.out.println("Data cleared");
    }

    public void setChosenVideoOption(StreamOption chosenOption) {
        this.chosenVideoOption = chosenOption;
    }

    public void setChosenAudioOption(StreamOption chosenOption) {
        this.chosenAudioOption = chosenOption;
    }

    public YoutubeVideo getCurrentVideo() {
        return currentVideo;
    }

    public StreamOption getChosenVideoOption() {
        return chosenVideoOption;
    }

    public StreamOption getChosenAudioOption() {
        return chosenAudioOption;
    }

    public Image getThumbnailImage() {
        return thumbnailImage;
    }

    public YoutubeVideo.Thumbnail getCurrentThumbnail() {
        return currentThumbnail;
    }
}
