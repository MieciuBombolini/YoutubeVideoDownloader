package dev.bebomny.youtubevideodownloader;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class YoutubeVideoDownloaderApplication extends Application {

    public static YoutubeVideoDownloaderApplication instance;
    public FXMLLoader fxmlLoader;
    public MainController mainController;
    public DataManager dataManager;
    public DownloadManager downloadManager;

    @Override
    public void start(Stage stage) throws IOException {
        if(instance == null) instance = this;
        this.dataManager = new DataManager(this);
        this.downloadManager = new DownloadManager(this);

        fxmlLoader = new FXMLLoader(YoutubeVideoDownloaderApplication.class.getResource("main-view.fxml"));
        Scene mainScene = new Scene(fxmlLoader.load(), 1200, 500);
        mainController = fxmlLoader.getController();

        stage.setTitle("Youtube Video Downloader | by Bebomny:)");
        stage.getIcons().add(new Image(YoutubeVideoDownloaderApplication.class.getResourceAsStream("YoutubeDownloaderIcon.png")));

        stage.setScene(mainScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static YoutubeVideoDownloaderApplication getInstance() {
        return instance;
    }

    public MainController getMainController() {
        return mainController;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }
}
