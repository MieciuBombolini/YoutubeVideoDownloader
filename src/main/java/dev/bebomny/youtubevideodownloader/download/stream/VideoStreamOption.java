package dev.bebomny.youtubevideodownloader.download.stream;

import dev.bebomny.youtubevideodownloader.download.tag.StreamType;

import java.net.URL;
import java.util.Objects;

public class VideoStreamOption implements StreamOption{

    private final URL url;
    private final StreamType streamType;
    private final Quality quality;
    private final String qualityLabel;
    private final int iTag;
    private final int approxDurationMs;
    private final long contentLength;
    private final int bitrate;
    private final int width, height;
    private final int fps;

    public VideoStreamOption(URL url, StreamType streamType, String videoQualityKey, String qualityLabel,
                             int iTag, int approxDurationMs, int contentLength, int bitrate,
                             int width, int height, int fps) {
        this.url = url;
        this.streamType = streamType;
        this.quality = VideoOptionQuality.getByKey(videoQualityKey);
        this.qualityLabel = qualityLabel;
        this.iTag = iTag;
        this.approxDurationMs = approxDurationMs;
        this.contentLength = contentLength;
        this.bitrate = bitrate;

        //Video specific
        this.width = width;
        this.height = height;
        this.fps = fps;
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
    public String getVideoQualityText() {
        return quality.getText();
    }

    @Override
    public String getVideoQualityKey() {
        return quality.getKey();
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
        return quality;
    }

    @Override
    public boolean isVideo() {
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

    enum VideoOptionQuality implements Quality{
        TINY("tiny", "Tiny"),
        SMALL("small", "Small"),
        MEDIUM("medium", "Medium"),
        LARGE("large", "Large"),
        HD720("hd720", "HD720"),
        HD1080("hd1080", "HD1080"),
        UNKNOWN("unknown", "Unknown");

        private final String key;
        private final String text;

        VideoOptionQuality(String key, String text) {
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
                return "VideoQuality Unknown, Report if you can, ty:)";
            }
            return text;
        }

        static VideoOptionQuality getByKey(String key) {
            if(key == null)
                return VideoOptionQuality.TINY;

            for(VideoOptionQuality quality : VideoOptionQuality.values()) {
                if(Objects.equals(quality.getKey(), key))
                    return quality;
            }

            return VideoOptionQuality.UNKNOWN;
        }
    }
}
