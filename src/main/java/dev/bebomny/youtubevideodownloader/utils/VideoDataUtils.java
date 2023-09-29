package dev.bebomny.youtubevideodownloader.utils;

import dev.bebomny.youtubevideodownloader.DataManager;
import dev.bebomny.youtubevideodownloader.MainController;
import dev.bebomny.youtubevideodownloader.YoutubeVideoDownloaderApplication;
import dev.bebomny.youtubevideodownloader.download.stream.EmptyStreamOption;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.download.tag.Encoding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoDataUtils {

    public static String matchAndGet(Pattern pattern, String data) {
        Matcher matcher = pattern.matcher(data);
        if (!matcher.find()) {
            throw new NoSuchElementException("Match not found!");
        }
        return matcher.group(1);
    }

    public static List<StreamOption> sortStreamOptions(List<StreamOption> options) {
        options.removeIf(Objects::isNull);
        options.removeIf(streamOption -> streamOption instanceof EmptyStreamOption);
        options.sort(Comparator.comparing(StreamOption::getQualityValue).reversed());
        return options;
    }

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

    public static ObservableList<MenuItem> getAudioQualityMenuFromStreamOptions(List<StreamOption> streamOptions) {
        YoutubeVideoDownloaderApplication application = YoutubeVideoDownloaderApplication.getInstance();
        MainController controller = application.getMainController();
        DataManager dataManager = application.getDataManager();

        //For testing purposes TODO: remove later
        //streamOptions.removeIf(option -> !option.getType().getAudioEncoding().equals(Encoding.AAC));
        //streamOptions.removeIf(option -> !option.isVideoAndAudio());
        //

        ObservableList<MenuItem> itemList = FXCollections.observableArrayList();
        for (StreamOption option : streamOptions) {
            MenuItem optionItem = new MenuItem(option.getText());

            optionItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dataManager.setChosenAudioOption(option);
                    controller.audioQualityMenuButton.setText(option.getText());
                    controller.updateDisplayedInfo();
                    controller.downloadButton.setDisable(false);
                    System.out.println("URL: " + option.getUrl().toExternalForm());
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

        //For testing purposes TODO: remove later
        //streamOptions.removeIf(option -> !option.getType().getVideoEncoding().equals(Encoding.H264));
        //streamOptions.removeIf(option -> !option.isVideoAndAudio());
        //

        ObservableList<MenuItem> itemList = FXCollections.observableArrayList();
        for (StreamOption option : streamOptions) {
            MenuItem optionItem = new MenuItem(option.getText());

            optionItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dataManager.setChosenVideoOption(option);
                    if(option.isVideoAndAudio())
                        dataManager.setChosenAudioOption(null);
                    controller.videoQualityMenuButton.setText(option.getText());
                    controller.updateDisplayedInfo();
                    controller.downloadButton.setDisable(false);
                    System.out.println("URL: " + option.getUrl().toExternalForm());
                }
            });
            itemList.add(optionItem);
        }

        return itemList;
    }
}
