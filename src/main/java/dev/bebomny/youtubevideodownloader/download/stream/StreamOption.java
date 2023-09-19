package dev.bebomny.youtubevideodownloader.download.stream;

import dev.bebomny.youtubevideodownloader.download.tag.Encoding;
import dev.bebomny.youtubevideodownloader.download.tag.StreamType;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface StreamOption {

    /**
     * Returns the {@link URL} of the stream.
     *
     * @return the url.
     */
    URL getUrl();

    /**
     * Returns the {@link StreamType} of the stream. This object can give us information about the stream,
     * such as the video and audio quality, the format, or the container.
     * *
     *
     * @return the {@link StreamType}
     */
    StreamType getType();

    String getQualityText();
    String getQualityKey();

    int getITag();

    int getDurationMs();

    long getContentLength();

    int getBitrate();

    Quality getQuality();

    default int getWidth() {
        return -1;
    }

    default int getHeight() {
        return -1;
    }

    default String getResolution() {
        return getWidth() >= 0 || getHeight() >= 0 ? getWidth() + "x" + getHeight() : "Unknown Resolution";
    }

    default String getText() {
        StreamType type = getType();
        return type.hasVideo() ?
                type.getVideoQuality().getDisplayName() + type.getFps().getAsInt() + type.getContainer().getFormat()
                :
                type.hasAudio() ?
                        type.getAudioQuality().getDisplayName()
                        :
                        "Unknown";
    }

    default int getQualityValue() {
        StreamType streamType = getType();
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

    interface Quality {
        String getKey();
        String getText();
    }
}
