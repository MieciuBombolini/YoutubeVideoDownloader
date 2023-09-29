package dev.bebomny.youtubevideodownloader.download.stream;

import dev.bebomny.youtubevideodownloader.download.tag.StreamType;
import javafx.scene.control.MenuItem;

import java.net.URL;

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

    default String getAudioQualityText() {
        return "Unknown";
    }
    default String getAudioQualityKey() {
        return "Unknown";
    }

    default String getVideoQualityText() {
        return "Unknown";
    }
    default String getVideoQualityKey() {
        return "Unknown";
    }

    default String getQualityLabel() {
        return "tiny";
    }

    int getITag();

    int getDurationMs();

    default long getContentLength() {
        return -1;
    };

    int getBitrate();

    default Quality getAudioQuality() {
        return null;
    }

    default Quality getVideoQuality() {
        return null;
    }

    default boolean isVideo() {
        return false;
    }

    default boolean isAudio() {
        return false;
    }

    default boolean isVideoAndAudio() {
        return isVideo() && isAudio();
    }

    default String getAudioOrVideoAsString() {
        return isVideoAndAudio() ? "dual" : isAudio() ? "audio" : isVideo() ? "video" : "Unknown";
    }

    default int getWidth() {
        return -1;
    }

    default int getHeight() {
        return -1;
    }

    default String getResolution() {
        return getWidth() >= 0 || getHeight() >= 0 ? getWidth() + "x" + getHeight() : "Unknown Resolution";
    }

    default int getFps() {
        return -1;
    }

    default int getAudioSampleRate() {
        return -1;
    }

    default int getAudioChannels() {
        return -1;
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
