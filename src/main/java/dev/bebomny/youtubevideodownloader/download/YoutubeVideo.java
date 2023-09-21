package dev.bebomny.youtubevideodownloader.download;

import com.alibaba.fastjson.JSONObject;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.utils.Validate;
import dev.bebomny.youtubevideodownloader.utils.VideoDataUtils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class YoutubeVideo {

    //Video info
    private final String title;
    private final String author;
    private final String videoUrl;
    private final String videoId;
    private final int videoLength; //in seconds
    private final long viewCount;
    private final String shortDescription;
    private final List<JSONObject> thumbnails;
    private final List<StreamOption> videoStreamOptions;
    private final List<StreamOption> audioStreamOptions;

    //Timing info
    private final int expiresInSeconds; //seconds
    private final long creationTime; //millis
    private final long validUntil; //millis

    public YoutubeVideo(String title, String author, String videoUrl, String videoId,
                        int videoLength, int viewCount, String shortDescription,
                        List<JSONObject> thumbnails, int expiresInSeconds) {
        this(title, author, videoUrl, videoId, videoLength, viewCount, shortDescription, thumbnails, expiresInSeconds, null, null);
    }

    public YoutubeVideo(String title, String author, String videoUrl, String videoId,
                        int videoLength, int viewCount, String shortDescription,
                        List<JSONObject> thumbnails, int expiresInSeconds,
                        List<StreamOption> videoStreamOptions, List<StreamOption> audioStreamOptions) {
        Validate.notNull(title, "Title cannot be null!");
        this.title = title;
        this.author = author;
        this.videoUrl = videoUrl;
        this.videoId = videoId;
        this.videoLength = videoLength;
        this.viewCount = viewCount;
        this.shortDescription = shortDescription;
        this.thumbnails = thumbnails;
        this.expiresInSeconds = expiresInSeconds;
        this.creationTime = System.currentTimeMillis();
        this.validUntil = this.creationTime + (this.expiresInSeconds * 1000L);
        this.videoStreamOptions = videoStreamOptions == null ? new LinkedList<>() : videoStreamOptions;
        this.audioStreamOptions = audioStreamOptions == null ? new LinkedList<>() : audioStreamOptions;
    }


    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public int getVideoLength() {
        return videoLength;
    }

    public long getViewCount() {
        return viewCount;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public List<StreamOption> getAudioStreamOptions() {
        return audioStreamOptions;
    }

    public List<StreamOption> getVideoStreamOptions() {
        return videoStreamOptions;
    }

    public List<StreamOption> getSortedAudioStreamOptions() {
        VideoDataUtils.sortStreamOptions(audioStreamOptions);
        return audioStreamOptions;
    }

    public List<StreamOption> getSortedVideoStreamOptions() {
        VideoDataUtils.sortStreamOptions(videoStreamOptions);
        return videoStreamOptions;
    }

    public List<JSONObject> getThumbnails() {
        return thumbnails;
    }

    public Thumbnail getBestQualityThumbnail() {
        List<Thumbnail> thumbnails = new LinkedList<>();

        for (int i = 0; i < getThumbnails().size(); i++) {
            JSONObject json = getThumbnails().get(i);
            thumbnails.add(Thumbnail.parseThumbnail(json));
        }

        thumbnails.sort(Comparator.comparing(Thumbnail::getQualityValue).reversed());

        return thumbnails.get(0);
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

        private static final String BEST_QUALITY_KEY = "maxresdefault.jpg";

        private final String url;
        private final int width;
        private final int height;

        private Thumbnail(String url, int width, int height) {
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
            return new Thumbnail(json.getString("url"), json.getInteger("width"), json.getInteger("height"));
        }
    }
}
