package dev.bebomny.youtubevideodownloader.downloader.tag;

/**
 * Represents the encoding of a video or audio channel.
 */
public enum Encoding {

    H263,
    H264,
    VP8,
    VP9,
    MP4,
    MP3,
    AAC,
    VORBIS,
    OPUS,
    DTSE,
    EC_3;

    public String getName() {
        return name();
    }

    public String getDisplayName() {
        return name().toLowerCase();
    }

}
