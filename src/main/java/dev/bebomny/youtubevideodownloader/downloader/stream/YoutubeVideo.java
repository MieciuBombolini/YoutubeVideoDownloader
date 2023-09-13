package dev.bebomny.youtubevideodownloader.downloader.stream;

import com.alibaba.fastjson.JSONObject;
import dev.bebomny.youtubevideodownloader.downloader.utils.StreamOptionUtils;
import dev.bebomny.youtubevideodownloader.downloader.utils.Validate;

import java.util.*;

public class YoutubeVideo {

    private final String title;
    private final String author;
    private final int videoLength;
    private final long viewCount;
    private final List<StreamOption> videoStreamOptions;
    private final List<StreamOption> audioStreamOptions;
    private final List<JSONObject> thumbNails;
    private final String shortDescription;

    //time
    private final int expiresInSeconds; //seconds
    private final long creationTime; //millis
    private final long validUntil; //millis

    //


    public YoutubeVideo(String title, String author, int videoLength, long viewCount, String shortDescription, List<JSONObject> thumbNails, int expiresInSeconds) {
        this(title, author, videoLength, viewCount, shortDescription, thumbNails, expiresInSeconds, null);
    }

    public YoutubeVideo(String title, String author, int videoLength, long viewCount, String shortDescription, List<JSONObject> thumbNails, int expiresInSeconds, List<StreamOption> streamOptions) {
        Validate.notNull(title, "Title cannot be null!");
        this.title = title;
        this.author = author;
        this.videoLength = videoLength;
        this.viewCount = viewCount;
        this.shortDescription = shortDescription;
        this.thumbNails = thumbNails;
        this.expiresInSeconds = expiresInSeconds;
        this.creationTime = System.currentTimeMillis();
        this.validUntil = this.creationTime + (this.expiresInSeconds * 1000L);
        this.videoStreamOptions = streamOptions == null ? new LinkedList<>() : StreamOptionUtils.extractVideoOptions(streamOptions);
        this.audioStreamOptions = streamOptions == null ? new LinkedList<>() : StreamOptionUtils.extractAudioOptions(streamOptions);
    }

    public void sortStreamOptions() {
        StreamOptionUtils.sortStreamOptions(videoStreamOptions);
        StreamOptionUtils.sortStreamOptions(audioStreamOptions);
    }

    private int getQualityValue(StreamOption option) {
        if(option == null)
            return 0;
        return option.getQualityValue();
    }

    public String getTitle() {
        return title;
    }

    public Optional<String> getAuthor() {
        return Optional.ofNullable(author);
    }

    public int getVideoLength() {
        return videoLength;
    }

    public long getViewCount() {
        return viewCount;
    }

    public List<StreamOption> getSortedVideoStreamOptions() {
        StreamOptionUtils.sortStreamOptions(videoStreamOptions);
        return videoStreamOptions;
    }

    public List<StreamOption> getSortedAudioStreamOptions() {
        StreamOptionUtils.sortStreamOptions(audioStreamOptions);
        return audioStreamOptions;
    }

    public List<StreamOption> getVideoStreamOptions() {
        return videoStreamOptions;
    }

    public List<StreamOption> getAudioStreamOptions() {
        return audioStreamOptions;
    }

    public List<JSONObject> getThumbNails() {
        return thumbNails;
    }

    public Thumbnail getBestQualityThumbnail() {
        List<Thumbnail> tn = new LinkedList<>();

        for (int i = 0; i < getThumbNails().size(); i++) {
            JSONObject json = getThumbNails().get(i);
            tn.add(Thumbnail.parseThumbnail(json));
        }

        tn.sort(Comparator.comparing(Thumbnail::getQualityValue).reversed());

        return tn.get(0);
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public boolean isStillValid(long currentTimeMillis) {
        return currentTimeMillis < validUntil;
    }

    public long getExpireTime() {
        return validUntil;
    }

    public static class Thumbnail {
        private final String url;
        private final int width;
        private final int height;

        public Thumbnail(JSONObject json) {
            this(json.getString("url"), json.getInteger("width"), json.getInteger("height"));
        }

        public Thumbnail(String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getQualityValue() {
            return width * height;
        }

        public static Thumbnail parseThumbnail(JSONObject json) {
            return new Thumbnail(json);
        }
    }
}
