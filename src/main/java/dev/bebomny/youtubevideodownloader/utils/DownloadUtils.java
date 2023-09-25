package dev.bebomny.youtubevideodownloader.utils;

import com.alibaba.fastjson.JSONObject;
import dev.bebomny.youtubevideodownloader.DataManager;
import dev.bebomny.youtubevideodownloader.MainController;
import dev.bebomny.youtubevideodownloader.YoutubeVideoDownloaderApplication;
import dev.bebomny.youtubevideodownloader.download.SaveFormat;
import dev.bebomny.youtubevideodownloader.download.exception.DownloadException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class DownloadUtils {

    public static final String tempFolderPath = "temp";
    public static final File tempFolder = new File(tempFolderPath);
    public static final String realTempDir = System.getProperty("java.io.tmpdir");
    public static final String realTempFolderName = "youtube_downloader";
    public static final File realTempFolder = new File(realTempDir, realTempFolderName);
    public static final String unknownFormatsFolderPath = tempFolderPath + File.separator + "unknownFormats";
    public static final File unknownFormatsFolder = new File(unknownFormatsFolderPath);


    private static final String KEY_MIMETYPE = "mimeType";


    public static void saveUnknownOptionToFile(JSONObject format, String url, int iTag) {
        if(unknownFormatsFolder.mkdirs())
            System.out.println("Temporary directory created successfully");
        else
            System.out.println("Temp directory already present");


        //{audio/video}_{iTag}.txt
        String mimeType = extractMimeType(format);
        String fileName = (mimeType.contains("audio") ? "audio" : "video") +
                "_" +
                iTag +
                ".txt";
        System.out.println("Saving unknown stream option to file with name: " + fileName + " | At location: " + unknownFormatsFolder.getAbsolutePath());


        File unknownFormatFile = new File(unknownFormatsFolder, fileName);
        if(unknownFormatFile.exists()) {
            System.out.println("Unknown format already saved!");
            return;
        }

        try (FileWriter writer = new FileWriter(unknownFormatFile)){
            writer.write("iTag: " + iTag + '\n');
            writer.write("url: " + url + '\n');
            writer.write("Format as string: " + '\n');
            writer.write(format.toString() + '\n');
            writer.write("Format as json string" + '\n');
            writer.write(format.toJSONString());
        } catch (IOException e) {
            throw new DownloadException("Unable to save UnknownFormat with iTag: " + iTag);
        }
    }

    private static String extractMimeType(JSONObject format) {
        return format.getString(KEY_MIMETYPE);
    }

    public String getFormatedDownloadSpeed(double speedInBps) {
        if (speedInBps >= 1_000_000) {
            double speedInMbps = speedInBps / 1_000_000;
            return formatDownloadSpeed(speedInMbps, "0.00") + " MBps";
        } else if (speedInBps >= 1_000) {
            double speedInKbps = speedInBps / 1_000;
            return formatDownloadSpeed(speedInKbps, "0.00") + " kBps";
        } else {
            return formatDownloadSpeed(speedInBps, "0.00") + " B/s";
        }


    }

    private static String formatDownloadSpeed(double value, String pattern) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return decimalFormat.format(value);
    }

    public static boolean clearDirectory(File dir) {
        if(dir.exists()) {
            System.out.println("Directory doesnt exist!");
            return false;
        }

        File[] files = dir.listFiles();
        if(files == null) {
            System.out.println("No files present in this directory!");
            return false;
        }


        for (File file : files) {
            if(file.isFile()) {
                if(file.delete()) {
                    System.out.println("Deleted file: " + file.getName());
                } else {
                    System.out.println("Failed to delete file: " + file.getName());
                }
            }
        }

        return true;
    }

    public static ObservableList<MenuItem> createSaveAsFormatMenuItems() {

        ObservableList<MenuItem> items = FXCollections.observableArrayList();;

        for(SaveFormat format : SaveFormat.values()) {
            if(format == SaveFormat.UNKNOWN)
                continue;

            MenuItem item = new MenuItem(format.getDisplayName());

            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    YoutubeVideoDownloaderApplication application = YoutubeVideoDownloaderApplication.getInstance();
                    MainController controller = application.getMainController();
                    DataManager dataManager = application.getDataManager();
                    dataManager.setSaveAsFormat(format);
                    controller.updateDisplayedInfo();
                }
            });

            items.add(item);
        }
        return items;
    }
}
