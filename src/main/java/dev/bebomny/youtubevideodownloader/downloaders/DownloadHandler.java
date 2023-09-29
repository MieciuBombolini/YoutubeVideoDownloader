package dev.bebomny.youtubevideodownloader.downloaders;

import dev.bebomny.youtubevideodownloader.converters.Converter;
import dev.bebomny.youtubevideodownloader.download.StreamMedium;
import dev.bebomny.youtubevideodownloader.download.exception.DownloadException;
import dev.bebomny.youtubevideodownloader.download.status.DownloadStatus;
import dev.bebomny.youtubevideodownloader.download.tag.Container;
import dev.bebomny.youtubevideodownloader.download.tag.Encoding;
import dev.bebomny.youtubevideodownloader.download.tag.FormatNote;
import dev.bebomny.youtubevideodownloader.download.tag.StreamType;
import dev.bebomny.youtubevideodownloader.utils.DownloadUtils;
import dev.bebomny.youtubevideodownloader.utils.FileNameSanitizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadHandler implements Runnable {

    //Downloader cant be null, heh
    private Downloader downloader;
    //Converter can be null if the video is already in the right format,
    // requires a check before entering here
    private Converter converter;
    private File targetDestination;
    private String targetFileName;

    //For private use only!
    private DownloadStage stage;
    private File tempFolder;

    //Thread handling
    //TODO: implement multithreading down the line,
    // lets stay on this one thread for now

    private List<File> downloadedVideoFiles;
    private List<File> downloadedAudioFiles;

    public DownloadHandler(File tempFolder) {
        this.downloader = null;
        this.converter = null;
        this.tempFolder = DownloadUtils.tempFolder; //this.tempFolder = tempFolder == null ? DownloadUtils.realTempFolder : tempFolder;
        this.stage = DownloadStage.DATA_MISSING;
    }

    public void setup(Downloader newDownloader, Converter newConverter, File targetDestination, String targetFileName , File newTempFolder) {
        this.downloader = newDownloader;
        this.converter = newConverter;
        this.tempFolder = newTempFolder != null ? newTempFolder : tempFolder;

        if(this.downloader == null || this.tempFolder == null)
            return;

        this.targetDestination = targetDestination;
        this.targetFileName = targetFileName;
        this.downloadedVideoFiles = new ArrayList<>();
        this.downloadedAudioFiles = new ArrayList<>();

        if(tempFolder != downloader.getTempFolder()) {
            System.out.println(
                    "TempFolder mismatch, this is dangerous! " +
                    "Falling back to default tempFolder!");
            //TODO: change later to the real temp folder
            this.tempFolder = DownloadUtils.tempFolder;
            this.downloader.setTempFolder(DownloadUtils.tempFolder);
        }

        this.stage = DownloadStage.READY;
        System.out.println("Downloader ready to start!");
    }

    @Override
    public void run() {
        if(this.stage != DownloadStage.READY) throw new DownloadException(
                "Insufficient data provided " +
                "or another instance is already running!");

        this.stage = DownloadStage.DOWNLOADING;

        //create temp folder
        if(!this.tempFolder.exists())
            if(this.tempFolder.mkdirs())
                System.out.println("Created temp directory at: " + this.tempFolder.getAbsolutePath());
        System.out.println("Temp folder already exists");

        if(DownloadUtils.clearDirectory(tempFolder))
            System.out.println("Directory cleared successfully");
        else
            System.out.println("Failed to clear TempDirectory");

        Map<StreamMedium, File> downloadedFiles = downloader.downloadStream();

        //Files should be already merged so only separating them is left
        downloadedFiles.forEach((streamMedium, file) -> {
            if(streamMedium == StreamMedium.VIDEO)
                downloadedVideoFiles.add(file);
            else if (streamMedium == StreamMedium.AUDIO) {
                downloadedAudioFiles.add(file);
            }
        });

        if(downloadedAudioFiles.isEmpty() && downloadedVideoFiles.isEmpty())
            throw new DownloadException("No downloaded files found, this isn't right!");

        //if the file fills this conditions:
        //container mp4, encoding h264/aac, formatNote none
        // it doesn't require any conversions to be playable
        // so leave it as is
        // -> save the file at the specified destination
        StreamType type = downloader.getVideoOption() == null ?
                downloader.getAudioOption().getType()
                :
                downloader.getVideoOption().getType();
        if(type.getContainer() == Container.MP4
                && type.getFormatNote() == FormatNote.NONE
                && (type.getVideoEncoding() == Encoding.H264 || type.getAudioEncoding() == Encoding.AAC)) {

            saveDownloadedFile(type);
            this.stage = DownloadStage.FINISHED;
            return;
        }

        //Converting
        this.stage = DownloadStage.CONVERTING;
        if(converter == null) {
            System.out.println(
                    "No converter was specified, " +
                    "saving to file and shutting down the thread");
            saveDownloadedFile(type);
            return;
        }

        //saveDownloadedFile(type);
        if(downloadedVideoFiles.isEmpty()
                || downloadedAudioFiles.isEmpty()
                || downloadedVideoFiles.get(0) == null
                || downloadedAudioFiles.get(0) == null)
            throw new DownloadException("One or both files are missing!");
        converter.convert(downloadedVideoFiles.get(0), downloadedAudioFiles.get(0));
        this.stage = DownloadStage.FINISHED;
        return;

        //Currently unhandled, TODO: handle conversion
        //throw new DownloadException("Unhandled Conversion!");
    }

    private void saveDownloadedFile(StreamType targetType) {
        if(!DownloadUtils.checkFileNameFormat(targetFileName)) {
            targetFileName = targetFileName + targetType.getContainer().getFormat();
        }
        FileNameSanitizer.sanitizeFileName(targetFileName);
        File targetFile = new File(targetDestination, targetFileName);

        File downloadedFile = downloadedVideoFiles.isEmpty() ? downloadedAudioFiles.get(0) : downloadedVideoFiles.get(0);
        DownloadUtils.copyFile(downloadedFile, targetFile);
    }

    public boolean isFinished() {
        return stage == DownloadStage.FINISHED;
    }

    public DownloadStatus getStatus() {
        return stage == DownloadStage.CONVERTING ? converter.getStatus() : downloader.getStatus();
    }

    public double getDownloadSpeed() {
        return downloader.getDownloadSpeed();
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public Converter getConverter() {
        return converter;
    }

    private enum DownloadStage {
        DATA_MISSING,
        READY,
        DOWNLOADING,
        CONVERTING,
        FINISHED
    }
}
