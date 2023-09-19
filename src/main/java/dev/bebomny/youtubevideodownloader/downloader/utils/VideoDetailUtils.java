package dev.bebomny.youtubevideodownloader.downloader.utils;

import dev.bebomny.youtubevideodownloader.DataManager;
import dev.bebomny.youtubevideodownloader.MainController;
import dev.bebomny.youtubevideodownloader.YoutubeVideoDownloaderApplication;
import dev.bebomny.youtubevideodownloader.download.StreamOption;
import dev.bebomny.youtubevideodownloader.downloader.stream.YoutubeVideo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

import java.text.DecimalFormat;
import java.util.List;

public class VideoDetailUtils {

    public static String getFormattedVideoViewCount(long viewCount) {
        if (viewCount < 1000) {
            return String.valueOf(viewCount);
        } else if (viewCount < 1000000) {
            double thousands = viewCount / 1000.0;
            return new DecimalFormat("#.#k").format(thousands);
        } else {
            double millions = viewCount / 1000000.0;
            return new DecimalFormat("#.#m").format(millions);
        }
    }

    public static String getFormattedVideoLength(int videoLength) {
        int hours = videoLength / 3600;
        int minutes = (videoLength % 3600) / 60;
        int seconds = videoLength % 60;

        String formattedTime = hours == 0 ? minutes == 0 ? String.format("%02d", seconds) + "s" : String.format("%02d:%02d", minutes, seconds) + "m" : String.format("%02d:%02d:%02d", hours, minutes, seconds) + "h";
        return formattedTime;
    }

    public static ObservableList<MenuItem> getVideoQualityMenuFromVideo(YoutubeVideo video) {
        YoutubeVideoDownloaderApplication application = YoutubeVideoDownloaderApplication.getInstance();
        MainController controller = application.getMainController();
        DataManager dataManager = application.getDataManager();

        List<StreamOption> streamOptions = video.getSortedVideoStreamOptions();
        //remove if only audio
        streamOptions.removeIf(target -> (!target.getType().hasVideo()) && (target.getType().hasAudio()));

        //System.out.println("Stream Options size: " + streamOptions.size());

        //idk? filter audio and video
        //Length: 85168840

        //Audio and video only
        //streamOptions.removeIf(target -> !(target.getType().hasVideo() && target.getType().hasAudio()));

        //video and not dash
        //streamOptions.removeIf(target -> !(target.getType().hasVideo() && target.getType().getFormatNote() != FormatNote.DASH));

        //

        ObservableList<MenuItem> itemList = FXCollections.observableArrayList();

        for(StreamOption option : streamOptions) {
            MenuItem optionItem = new MenuItem(option.getText());

            optionItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dataManager.setChosenVideoOption(option);
                    controller.videoQualityMenuButton.setText(option.getText());
                    controller.updateDisplayedInfo();
                    controller.downloadButton.setDisable(false);
                    System.out.println("URL: " + option.getUrl().toString());
                }
            });
            itemList.add(optionItem);
        }

        return itemList;
    }

    public static ObservableList<MenuItem> getVideoQualityMenuFromStreamOptions(List<StreamOption> streamOptions) {
        YoutubeVideoDownloaderApplication application = YoutubeVideoDownloaderApplication.getInstance();
        MainController controller = application.getMainController();
        DataManager dataManager = application.getDataManager();

        ObservableList<MenuItem> itemList = FXCollections.observableArrayList();

        for(StreamOption option : streamOptions) {
            MenuItem optionItem = new MenuItem(option.getText());

            optionItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dataManager.setChosenVideoOption(option);
                    controller.videoQualityMenuButton.setText(option.getText());
                    controller.updateDisplayedInfo();
                    controller.downloadButton.setDisable(false);
                    System.out.println("URL: " + option.getUrl().toString());
                }
            });
            itemList.add(optionItem);
        }

        return itemList;
    }

    public static ObservableList<MenuItem> getAudioQualityMenuFromStreamOptions(List<StreamOption> streamOptions) {
        YoutubeVideoDownloaderApplication application = YoutubeVideoDownloaderApplication.getInstance();
        MainController controller = application.getMainController();
        DataManager dataManager = application.getDataManager();

        ObservableList<MenuItem> itemList = FXCollections.observableArrayList();

        for(StreamOption option : streamOptions) {
            MenuItem optionItem = new MenuItem(option.getText());

            optionItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dataManager.setChosenAudioOption(option);
                    controller.audioQualityMenuButton.setText(option.getText());
                    controller.updateDisplayedInfo();
                    controller.downloadButton.setDisable(false);
                    System.out.println("URL: " + option.getUrl().toString());
                }
            });
            itemList.add(optionItem);
        }

        return itemList;
    }


}
