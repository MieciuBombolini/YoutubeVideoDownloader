package dev.bebomny.youtubevideodownloader;

import dev.bebomny.youtubevideodownloader.downloader.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.downloader.stream.YoutubeVideo;
import dev.bebomny.youtubevideodownloader.downloader.tag.ITagMap;
import dev.bebomny.youtubevideodownloader.downloader.tag.StreamType;
import dev.bebomny.youtubevideodownloader.downloader.utils.FileNameSanitizer;
import dev.bebomny.youtubevideodownloader.downloader.utils.VideoDetailUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class MainController {


    public TextField URLTextField;
    public TextField fileNameTextField;
    public TextField directoryTextField;
    public Label statusLabel;

    //videoInfo
    public Label tittleLabel;
    public Label authorLabel;
    public ImageView thumbnailImageView;
    public Label videoDescriptionLabel;
    public Label videoLengthLabel;
    public Label viewCountLabel;
    public MenuButton videoQualityMenuButton;
    public MenuButton audioQualityMenuButton;

    //Video details
    public Label videoQualityLabel;
    public Label formatLabel;
    public Label audioQualityLabel;
    public Label fpsLabel;
    public Label formatNoteLabel;
    public Label iTagLabel;
    public Label videoEncodingLabel;
    public Label audioEncodingLabel;
    public Label informationLabel;

    //download specific
    public Button downloadButton;
    public CheckBox titleAsNameCheckBox;
    public Label downloadSpeedLabel;

    //sample
    public Button sampleButton;



    @FXML
    public void initialize() {
        statusLabel.setText("");
        fileNameTextField.setText("downloadedVideo");
        directoryTextField.setText(System.getProperty("user.home") + File.separator +  "Downloads");
        tittleLabel.setText("Unknown");
        authorLabel.setText("Unknown");
        viewCountLabel.setText("Unknown");
        videoLengthLabel.setText("Unknown");
        videoDescriptionLabel.setText("Unknown");

        //Quality Tabs
        videoQualityMenuButton.setText("Empty");
        videoQualityLabel.setText("Unknown");
        formatLabel.setText("Unknown");
        audioQualityLabel.setText("Unknown");
        audioQualityLabel.setText("Unknown");
        fpsLabel.setText("Unknown");
        formatNoteLabel.setText("Unknown");
        iTagLabel.setText("Unknown");
        videoEncodingLabel.setText("Unknown");
        audioEncodingLabel.setText("Unknown");
        informationLabel.setDisable(true);
        informationLabel.setText("");

        //download
        downloadButton.setDisable(true); //Uncomment later!!!!!!!!!!
        titleAsNameCheckBox.setSelected(false);
        downloadSpeedLabel.setText("");

        //sample
        sampleButton.setDisable(true);
        sampleButton.setVisible(false);
    }

    @FXML
    protected void onFetchVideoDataButtonClick() {
        String url = URLTextField.getText();
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            statusLabel.setText("Incorrect or missing URL!");
            return;
        }

        DownloadManager downloadManager = YoutubeVideoDownloaderApplication.getInstance().getDownloadManager();
        downloadManager.fetchVideoData(url);

        //Clear the previous video
        DataManager dataManager = YoutubeVideoDownloaderApplication.getInstance().getDataManager();
        if(dataManager.getCurrentVideo() != null)
            dataManager.clearCurrentVideo();

        updateDisplayedInfo();

        statusLabel.setText("Fetching");
    }

    public void onTitleAsNameCheckBoxAction(ActionEvent actionEvent) {
        fileNameTextField.setDisable(titleAsNameCheckBox.isSelected());
        if(!Objects.equals(tittleLabel.getText(), "")) {
            fileNameTextField.setText(FileNameSanitizer.sanitizeFileName(tittleLabel.getText()));
            updateDisplayedInfo();
        }
    }

    @FXML
    protected void onSelectButtonClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator +  "Downloads"));

        File selectedDirectory = directoryChooser.showDialog(directoryTextField.getScene().getWindow());
        if(selectedDirectory == null)
            return;

        directoryTextField.setText(selectedDirectory.getAbsolutePath());
    }

    public void onSampleButtonClick(ActionEvent actionEvent) {

        YoutubeVideo video = YoutubeVideoDownloaderApplication.getInstance().dataManager.getCurrentVideo();

        StreamOption option = video.getVideoStreamOptions().stream()
                .filter(target -> target.getType().hasVideo() && target.getType().hasAudio())
                .min(Comparator.comparingInt(o -> o.getType().getVideoQuality().ordinal())).orElse(null);

        if(option == null) {
            System.out.println("Le null");
            return;
        }

        System.out.println(option.getType().toString());
    }

    @FXML
    protected void onDownloadButtonClick() {
        YoutubeVideoDownloaderApplication application = YoutubeVideoDownloaderApplication.getInstance();
        DownloadManager downloadManager = application.getDownloadManager();
        DataManager dataManager = application.getDataManager();

        StreamOption option = dataManager.getChosenVideoOption();

        File destination = new File(directoryTextField.getText());
        String fileName = FileNameSanitizer.sanitizeFileName(fileNameTextField.getText() + option.getType().getContainer().getFormat());

        //File//start  here//
        File target = new File(destination, fileName);
        if(target.exists()) {
            int count = 1;
            do {
                String newFileName = FileNameSanitizer.sanitizeFileName(fileNameTextField.getText() + "("+ count +")" + option.getType().getContainer().getFormat());
                target = new File(destination, newFileName);
                count++;
            } while(target.exists());
        }


        System.out.println("File Path: " + target.getAbsolutePath());

        downloadManager.downloadVideo(option, target);

        statusLabel.setText("Downloading");
    }

    public void updateDisplayedInfo() {
        DataManager dataManager = YoutubeVideoDownloaderApplication.getInstance().getDataManager();
        YoutubeVideo currentVideo = dataManager.getCurrentVideo();
        StreamOption currentVideoStreamOption = dataManager.getChosenVideoOption();
        StreamOption currentAudioStreamOption = dataManager.getChosenAudioOption();

        //Set Defaults

        //disable button
        downloadButton.setDisable(true);

        //Video info
        tittleLabel.setText("Unknown");
        authorLabel.setText("Unknown");
        videoLengthLabel.setText("Unknown");

        //Video details
        //Video quality menu
        videoQualityMenuButton.setText("Empty");
        videoQualityMenuButton.getItems().clear();
        //Audio Quality Menu
        audioQualityMenuButton.setText("Empty");
        audioQualityMenuButton.getItems().clear();

        //Video information label
        informationLabel.setText("");

        //Video details
        videoQualityLabel.setText("Unknown");
        videoEncodingLabel.setText("Unknown");
        fpsLabel.setText("Unknown");
        formatLabel.setText("Unknown");
        audioQualityLabel.setText("Unknown");
        audioEncodingLabel.setText("Unknown");
        iTagLabel.setText("Unknown");
        formatNoteLabel.setText("Unknown");

        //////////////////////

        if(currentVideo != null) {
            tittleLabel.setText(currentVideo.getTitle());
            authorLabel.setText(currentVideo.getAuthor().orElse("Author unfetchable"));
            viewCountLabel.setText(VideoDetailUtils.getFormattedVideoViewCount(currentVideo.getViewCount()));
            videoLengthLabel.setText(VideoDetailUtils.getFormattedVideoLength(currentVideo.getVideoLength()));
            videoDescriptionLabel.setText(currentVideo.getShortDescription());
            //thumbnailImageView.setFitWidth(dataManager.getCurrentThumbnail().getWidth());
            //thumbnailImageView.setFitWidth(dataManager.getCurrentThumbnail().getHeight());
            thumbnailImageView.setImage(dataManager.getThumbnailImage());

            if(titleAsNameCheckBox.isSelected()) {
                fileNameTextField.setText(FileNameSanitizer.sanitizeFileName(currentVideo.getTitle()));
            }

            videoQualityMenuButton.getItems().addAll(VideoDetailUtils.getVideoQualityMenuFromStreamOptions(currentVideo.getSortedVideoStreamOptions()));
            audioQualityMenuButton.getItems().addAll(VideoDetailUtils.getAudioQualityMenuFromStreamOptions(currentVideo.getSortedAudioStreamOptions()));
        }

        if(currentVideoStreamOption != null) {
            StreamType streamType = currentVideoStreamOption.getType();

            //unlock download button
            downloadButton.setDisable(false);

            //Video Quality Menu
            videoQualityMenuButton.setText(currentVideoStreamOption.getText());

            //video

            videoQualityLabel.setText(streamType.getVideoQuality().name());
            fpsLabel.setText(streamType.getFps().name());
            videoEncodingLabel.setText(streamType.getVideoEncoding().name());
            informationLabel.setDisable(false);
            informationLabel.setText("Contains ONLY video, you need to select an audio channel too!");


            //if this video option contains audio and video!
            if(streamType.hasAudio()) {
                audioQualityLabel.setText(streamType.getAudioQuality().name());
                audioEncodingLabel.setText(streamType.getAudioEncoding().name());
                informationLabel.setDisable(false);
                informationLabel.setText("Contains Audio and Video!");
                audioQualityMenuButton.setDisable(true);
            } else {
                audioQualityLabel.setText("Unknown");
                audioEncodingLabel.setText("Unknown");
            }

            //format
            formatLabel.setText(streamType.getContainer().name());
            formatNoteLabel.setText(streamType.getFormatNote().name());

            //iTag
            int iTag = ITagMap.MAP.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), streamType)).map(Map.Entry::getKey).toList().get(0); //collect(Collectors.toSet()
            iTagLabel.setText(String.valueOf(iTag));
        }

        if(currentAudioStreamOption != null) {
            StreamType streamType = currentAudioStreamOption.getType();

            //unlock download button
            downloadButton.setDisable(false);

            audioQualityMenuButton.setText(currentAudioStreamOption.getText());

            audioQualityLabel.setText(streamType.getAudioQuality().name());
            audioEncodingLabel.setText(streamType.getAudioEncoding().name());

            informationLabel.setDisable(false);
            informationLabel.setText("Contains Audio and Video!");

            int iTag = ITagMap.MAP.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), streamType)).map(Map.Entry::getKey).toList().get(0); //collect(Collectors.toSet()
            iTagLabel.setText(iTagLabel.getText() + " & " + iTag);

            //If video isn't chosen we set the details as audio type
            if(currentVideoStreamOption == null) {

                informationLabel.setText("Contains only audio!");

                formatLabel.setText(streamType.getContainer().name());
                formatNoteLabel.setText(streamType.getFormatNote().name());
                iTagLabel.setText(String.valueOf(iTag));
            }
        }
    }
}
