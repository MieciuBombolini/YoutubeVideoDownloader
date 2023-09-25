package dev.bebomny.youtubevideodownloader;

import dev.bebomny.youtubevideodownloader.download.SaveFormat;
import dev.bebomny.youtubevideodownloader.download.YoutubeVideo;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;
import javafx.scene.image.Image;

public class DataManager {

    private final YoutubeVideoDownloaderApplication videoDownloaderApplication;
    private final MainController controller;
    private SaveFormat saveAsFormat = SaveFormat.MP4;
    private YoutubeVideo currentVideo;
    private StreamOption chosenVideoOption;
    private StreamOption chosenAudioOption;
    private YoutubeVideo.Thumbnail currentThumbnail;
    private Image thumbnailImage;

    public DataManager(YoutubeVideoDownloaderApplication application) {
        this.videoDownloaderApplication = application;
        this.controller = application.getMainController();
    }

    public void setSaveAsFormat(SaveFormat saveAsFormat) {
        this.saveAsFormat = saveAsFormat;
    }

    public void setCurrentVideo(YoutubeVideo currentVideo) {
        this.currentVideo = currentVideo;
        this.currentThumbnail = currentVideo.getBestQualityThumbnail();
        System.out.println("Thumbnail Resolution:" + currentThumbnail.getWidth() + "x" + currentThumbnail.getHeight());
        System.out.println("Thumbnail URL: " + currentThumbnail.getUrl());
        System.out.println("Thumbnail best quality URL: " + currentThumbnail.getBestQualityUrl());
        this.thumbnailImage = new Image(currentThumbnail.getBestQualityUrl(), true);
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

    public SaveFormat getSaveAsFormat() {
        return saveAsFormat;
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
