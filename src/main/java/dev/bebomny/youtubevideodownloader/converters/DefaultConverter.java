package dev.bebomny.youtubevideodownloader.converters;

import dev.bebomny.youtubevideodownloader.download.SaveFormat;
import dev.bebomny.youtubevideodownloader.download.exception.DownloadException;
import dev.bebomny.youtubevideodownloader.download.status.DownloadStatus;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.utils.DownloadUtils;
import dev.bebomny.youtubevideodownloader.utils.FileNameSanitizer;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

import java.io.File;

public class DefaultConverter implements Converter{

    private StreamOption videoOption;
    private File videoFile;
    private StreamOption audioOption;
    private File audioFile;

    private File targetDestination;
    private String targetFileName;
    private SaveFormat targetFormat;

    private File tempFolder;


    public DefaultConverter(StreamOption videoOption, File videoFile,
                            StreamOption audioOption, File audioFile,
                            File targetDestination, String targetFileName, SaveFormat targetFormat, File tempFolder) {
        this.videoOption = videoOption;
        this.videoFile = videoFile;
        this.audioOption = audioOption;
        this.audioFile = audioFile;
        this.targetDestination = targetDestination;
        this.targetFileName = targetFileName;
        this.targetFormat = targetFormat;
    }


    @Override
    public void convert(File videoFile, File audioFile) {
        this.videoFile = videoFile;
        this.audioFile = audioFile;

        this.targetFileName = DownloadUtils.removeFileNameFormat(targetFileName);
        String fileName = targetFileName + targetFormat.getFormat();
        FileNameSanitizer.sanitizeFileName(fileName);
        File targetFile = new File(targetDestination, fileName);
        System.out.println("[Conversion] File instance created");

        try (FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(videoFile);
             FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(audioFile);
             FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(targetFile, videoOption.getWidth(), videoOption.getHeight());) {

            System.out.println("[Conversion] Entered the try statement");
            videoGrabber.start();
            System.out.println("[Conversion] videoGrabber start");
            audioGrabber.start();
            System.out.println("[Conversion] audioGrabber start");
            recorder.setFormat("mp4");
            System.out.println("[Conversion] recorder format set");
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            System.out.println("[Conversion] recorder codec set");
            recorder.setFrameRate(videoGrabber.getFrameRate());
            System.out.println("[Conversion] recorder frameRate set");

            // Set up audio codec and parameters
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            System.out.println("[Conversion] audio codec set");
            recorder.setAudioChannels(audioOption.getAudioChannels()); // You can adjust the number of audio channels as needed.
            System.out.println("[Conversion] audio channels set");
            //recorder.setAudioSampleRate(44100); // You can adjust the sample rate as needed.
            //recorder.setAudioQuality(44100);
            //recorder.setAudioBitrate(128000); // You can adjust the audio bitrate as needed.

            recorder.start();
            System.out.println("[Conversion] recorder started");

            Frame videoFrame;
            Frame audioFrame;
            System.out.println("[Conversion] Frames created entering while");
            while ((videoFrame = videoGrabber.grabFrame()) != null && (audioFrame = audioGrabber.grabFrame()) != null) {
                recorder.record(videoFrame);
                recorder.record(audioFrame);
            }
            System.out.println("[Conversion] While finished");
            recorder.stop();
            recorder.release();
            System.out.println("[Conversion] stopping recorder");

            videoGrabber.stop();
            videoGrabber.release();
            System.out.println("[Conversion] stopping videoGrabber");

            audioGrabber.stop();
            audioGrabber.release();
            System.out.println("[Conversion] Stopping audioGrabber");

            System.out.println("Video and audio combined successfully.");
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            e.printStackTrace();
            throw new DownloadException("Something went wrong during conversion!");
        }
    }

    @Override
    public DownloadStatus getStatus() {
        return DownloadStatus.CONVERTING;
    }
}
