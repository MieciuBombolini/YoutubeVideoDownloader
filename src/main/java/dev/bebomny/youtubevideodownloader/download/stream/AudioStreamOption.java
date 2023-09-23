package dev.bebomny.youtubevideodownloader.download.stream;

import dev.bebomny.youtubevideodownloader.download.tag.StreamType;

import java.net.URL;
import java.util.Objects;

public class AudioStreamOption implements StreamOption{

    private final URL url;
    private final StreamType streamType;
    private final Quality quality;
    private final int iTag;
    private final int approxDurationMs;
    private final long contentLength;
    private final int bitrate;
    private final int audioSampleRate;
    private final int audioChannels;

    public AudioStreamOption(URL url, StreamType streamType, String audioQualityKey,
                             int iTag, int approxDurationMs, int contentLength, int bitrate,
                             int audioSampleRate, int audioChannels) {
        this.url = url;
        this.streamType = streamType;
        this.quality = AudioOptionQuality.getByKey(audioQualityKey);
        this.iTag = iTag;
        this.approxDurationMs = approxDurationMs;
        this.contentLength = contentLength;
        this.bitrate = bitrate;

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

    @Override
    public String getAudioQualityText() {
        return quality.getText();
    }

    @Override
    public String getAudioQualityKey() {
        return quality.getKey();
    }

    @Override
    public String getQualityLabel() {
        return "tiny-" + streamType.getAudioQuality();
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
    public Quality getAudioQuality() {
        return quality;
    }

    @Override
    public int getAudioSampleRate() {
        return audioSampleRate;
    }

    @Override
    public int getAudioChannels() {
        return audioChannels;
    }

    @Override
    public boolean isAudio() {
        return true;
    }

    public enum AudioOptionQuality implements Quality {
        ULTRALOW("AUDIO_QUALITY_ULTRALOW", "Ultra low"),
        LOW("AUDIO_QUALITY_LOW", "Low"),
        MEDIUM("AUDIO_QUALITY_MEDIUM", "Medium"),
        HIGH("AUDIO_QUALITY_HIGH", "High"),
        UNKNOWN("unknown", "Unknown");

        private final String key;
        private final String text;

        AudioOptionQuality(String key, String text) {
            this.key = key;
            this.text = text;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getText() {
            if(this == UNKNOWN) {
                return "AudioQuality Unknown, Report if you can, ty:)";
            }
            return text;
        }

        static AudioOptionQuality getByKey(String key) {
            if (key == null)
                return AudioOptionQuality.LOW;

            for (AudioOptionQuality quality : AudioOptionQuality.values()) {
                if (Objects.equals(quality.getKey(), key))
                    return quality;
            }

            return AudioOptionQuality.UNKNOWN;
        }
    }
}
