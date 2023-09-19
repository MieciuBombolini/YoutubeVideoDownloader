package dev.bebomny.youtubevideodownloader.download.stream;

import dev.bebomny.youtubevideodownloader.download.tag.StreamType;

import java.net.URL;
import java.util.Objects;

public class AudioStreamOption implements StreamOption{

    private final URL url;
    private final  StreamType streamType;
    private final Quality quality;
    private final int iTag;
    private final int approxDurationMs;
    private final long contentLength;
    private final int bitrate;
    private final int audioSampleRate;
    private final int audioChannels;

    public AudioStreamOption(URL url, StreamType streamType, String audioQualityKey, int iTag, int approxDurationMs, int contentLength, int bitrate, int audioSampleRate, int audioChannels) {
        this.url = url;
        this.streamType = streamType;
        this.quality = AudioQuality.getByKey(audioQualityKey);
        this.iTag = iTag;
        this.approxDurationMs = approxDurationMs;
        this.contentLength = contentLength;
        this.bitrate = bitrate;
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

    @Override
    public String getQualityText() {
        return quality.getText();
    }

    @Override
    public String getQualityKey() {
        return null;
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
    public Quality getQuality() {
        return quality;
    }

    public int getAudioSampleRate() {
        return audioSampleRate;
    }

    public int getAudioChannels() {
        return audioChannels;
    }

    private enum AudioQuality implements Quality {
        LOW("AUDIO_QUALITY_LOW", "Low"),
        MEDIUM("AUDIO_QUALITY_MEDIUM", "Medium"),
        HIGH("AUDIO_QUALITY_HIGH", "High");

        private final String key;
        private final String text;

        AudioQuality(String key, String text) {
            this.key = key;
            this.text = text;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getText() {
            return text;
        }

        static AudioQuality getByKey(String key) {
            if (key == null)
                return AudioQuality.LOW;

            for (AudioQuality quality : AudioQuality.values()) {
                if (Objects.equals(quality.key, key))
                    return quality;
            }

            return AudioQuality.LOW;
        }
    }
}
