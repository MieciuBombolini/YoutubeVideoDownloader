package dev.bebomny.youtubevideodownloader.converters;

import dev.bebomny.youtubevideodownloader.download.SaveFormat;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;

import java.io.File;

public class ConverterBuilder {

    private StreamOption videoOption;
    private File videoFile;
    private StreamOption audioOption;
    private File audioFile;

    private File targetDestination;
    private String targetFileName;
    private SaveFormat targetFormat;

    private File tempFolder;

    //This is only a temporal solution!
    private ConverterBuilder(StreamOption firstOption, File firstFile) {
        //Do something normally
    }

    private ConverterBuilder(StreamOption videoOption, File videoFile,
                             StreamOption audioOption, File audioFile,
                             File targetDestination, String targetFileName, SaveFormat targetFormat,
                             File tempFolder) {
        this.videoOption = videoOption;
        this.videoFile = videoFile;
        this.audioOption = audioOption;
        this.audioFile = audioFile;
        this.targetDestination = targetDestination;
        this.targetFileName = targetFileName;
        this.targetFormat = targetFormat;
        this.tempFolder = tempFolder;
    }

    public static ConverterBuilder builder(StreamOption firstOption, File firstFile) {
        //TODO: add ID to Encoding options to incorporate it with ffmpeg
        return new ConverterBuilder(firstOption, firstFile);
    }

    public static ConverterBuilder builder(StreamOption videoOption, File videoFile,
                                           StreamOption audioOption, File audioFile,
                                           File targetDestination, String targetFileName, SaveFormat targetFormat, File tempFolder) {
        return new ConverterBuilder(videoOption, videoFile, audioOption, audioFile, targetDestination, targetFileName, targetFormat, tempFolder);
    }

    public Converter build() {
        return new DefaultConverter(videoOption, videoFile, audioOption, audioFile, targetDestination, targetFileName, targetFormat, tempFolder);
    }
}
