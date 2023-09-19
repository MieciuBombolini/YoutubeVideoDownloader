package dev.bebomny.youtubevideodownloader.download;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class YoutubeVideo {

    private final String videoUrl;
    private final String videoId;
    private final String title;
    private final String author;
    private final int length; //in seconds
    private final long views;
    private final String shortDescription;
    private final List<JSONObject> thumbnails;
    private final List<StreamOption> videoStreamOptions;
    private final List<StreamOption> audioStreamOptions;

}
