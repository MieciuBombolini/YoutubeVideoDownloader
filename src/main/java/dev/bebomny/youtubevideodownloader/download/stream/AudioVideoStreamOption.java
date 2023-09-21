package dev.bebomny.youtubevideodownloader.download.stream;

import dev.bebomny.youtubevideodownloader.download.tag.StreamType;

import java.net.URL;

public class AudioVideoStreamOption implements StreamOption{

    private final URL url;
    private final StreamType streamType;
    private final Quality videoQuality;
    private final Quality audioQuality;
    private final String qualityLabel;
    private final int iTag;
    private final int approxDurationMs;
    private final long contentLength;
    private final int bitrate;

    //Video specific
    private final int width, height;
    private final int fps;

    //Audio specific
    private final int audioSampleRate;
    private final int audioChannels;

    public AudioVideoStreamOption(URL url, StreamType streamType, String videoQualityKey, String audioQualityKey, String qualityLabel,
                                  int iTag, int approxDurationMs, int contentLength, int bitrate,
                                  int width, int height, int fps,
                                  int audioSampleRate, int audioChannels) {
        this.url = url;
        this.streamType = streamType;
        this.videoQuality = VideoStreamOption.VideoOptionQuality.getByKey(videoQualityKey);
        this.audioQuality = AudioStreamOption.AudioOptionQuality.getByKey(audioQualityKey);
        this.qualityLabel = qualityLabel;
        this.iTag = iTag;
        this.approxDurationMs = approxDurationMs;
        this.contentLength = contentLength;
        this.bitrate = bitrate;

        //Video specific
        this.width = width;
        this.height = height;
        this.fps = fps;

        //Audio specific
        this.audioSampleRate = audioSampleRate;
        this.audioChannels = audioChannels;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public StreamType getType() {
        return streamType;
    }

    public String getAudioQualityText() {
        return audioQuality.getText();
    }

    public String getAudioQualityKey() {
        return audioQuality.getKey();
    }

    public String getVideoQualityText() {
        return videoQuality.getText();
    }

    public String getVideoQualityKey() {
        return videoQuality.getKey();
    }

    @Override
    public String getQualityLabel() {
        return qualityLabel;
    }

    @Override
    public int getITag() {
        return iTag;
    }

    @Override
    public int getDurationMs() {
        return approxDurationMs;
    }

    @Override
    public long getContentLength() {
        return contentLength;
    }

    @Override
    public int getBitrate() {
        return bitrate;
    }

    @Override
    public Quality getVideoQuality() {
        return videoQuality;
    }

    @Override
    public Quality getAudioQuality() {
        return audioQuality;
    }

    @Override
    public boolean isVideo() {
        return true;
    }

    @Override
    public boolean isAudio() {
        return true;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getFps() {
        return fps;
    }

    @Override
    public int getAudioSampleRate() {
        return audioSampleRate;
    }

    @Override
    public int getAudioChannels() {
        return audioChannels;
    }
}
