package dev.bebomny.youtubevideodownloader.download;

import dev.bebomny.youtubevideodownloader.download.tag.StreamType;
import dev.bebomny.youtubevideodownloader.downloader.utils.Validate;

import java.net.URL;

public class StreamOption {

    private final URL url;
    private final StreamType streamType;
    private final int iTag;
    private final int approxDurationMs;
    private final long contentLength;
    private final int bitrate;
    private final int width, height;

    public StreamOption(URL url, StreamType type, int iTag, int approxDurationMs, int bitrate) {
        Validate.notNull(url, "Url cannot be null!");
        Validate.notNull(type, "Type cannot be null!");
        this.url = url;
        this.streamType = type;
        this.iTag = iTag;
        this.approxDurationMs = approxDurationMs;
        this.contentLength = 0;
        this.bitrate = bitrate;
        this.width = 0;
        this.height = 0;
    }

    public StreamOption(URL url, StreamType type, int iTag, int approxDurationMs, int bitrate, int width, int height, long contentLength) { //, JSONObject initRange, JSONObject indexRange
        Validate.notNull(url, "Url cannot be null!");
        Validate.notNull(type, "Type cannot be null!");
        this.url = url;
        this.streamType = type;
        this.iTag = iTag;
        this.approxDurationMs = approxDurationMs;
        this.contentLength = contentLength;
        this.bitrate = bitrate;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the {@link URL} of the stream.
     *
     * @return the url.
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Returns the {@link StreamType} of the stream. This object can give us information about the stream,
     * such as the video and audio quality, the format, or the container.
     * *
     *
     * @return the {@link StreamType}
     */
    public StreamType getType() {
        return streamType;
    }

    public int getITag() {
        return iTag;
    }

    public int getDurationMs() {
        return approxDurationMs;
    }

    public long getContentLength() {
        return contentLength;
    }

    public int getBitrate() {
        return bitrate;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getText() {
        StreamType type = getType();
        return type.hasVideo() ?
                type.getVideoQuality().getDisplayName() + type.getFps().getAsInt() + type.getContainer().getFormat()
                :
                type.hasAudio() ?
                        type.getAudioQuality().getDisplayName()
                        :
                        "Unknown";
    }

    public int getQualityValue() {
        int qualityValue = 0;

        if(streamType.hasVideo()) {
            String videoText = streamType.getVideoQuality().getDisplayName();
            int videoValue = 4 * Integer.parseInt(videoText.substring(0, videoText.indexOf('p')));
            int fpsValue = 2 * streamType.getFps().getAsInt();
            int formatValue = streamType.getContainer().getFormat().contains(".mp4") ? 50 : 0;

            //adding a 1000 so video qualities always appear above audio
            qualityValue += 1000 + videoValue + fpsValue + formatValue;
        }

        if(streamType.hasAudio()) {
            String audioText = streamType.getAudioQuality().getName();
            int audioValue = Integer.parseInt(audioText.substring(1));

            qualityValue += audioValue;
        }

        return qualityValue;
    }
}
