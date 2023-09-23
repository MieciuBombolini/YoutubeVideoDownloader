package dev.bebomny.youtubevideodownloader.download.stream;

import dev.bebomny.youtubevideodownloader.download.tag.StreamType;

import java.net.URL;

public class EmptyStreamOption implements StreamOption{

    private final URL url;
    private final int iTag;

    public EmptyStreamOption(URL url, int iTag) {
        this.url = url;
        this.iTag = iTag;
        System.out.println("Creating and empty stream option with iTag:" + iTag + ", sus?");
    }


    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public StreamType getType() {
        return null;
    }

    @Override
    public String getAudioQualityText() {
        return "Empty";
    }

    @Override
    public String getVideoQualityText() {
        return "Empty";
    }

    @Override
    public String getQualityLabel() {
        return "Empty";
    }

    @Override
    public int getITag() {
        return iTag;
    }

    @Override
    public int getDurationMs() {
        return -1;
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public int getBitrate() {
        return -1;
    }

    @Override
    public String getText() {
        return "Empty Option";
    }

    @Override
    public int getQualityValue() {
        return -69;
    }
}
