package dev.bebomny.youtubevideodownloader.clients;

import com.alibaba.fastjson.JSONObject;
import dev.bebomny.youtubevideodownloader.download.YoutubeVideo;

public class WebFetchingClient implements FetchingClient{
    @Override
    public JSONObject fetchVideoData(String url) {
        return null;
    }

    @Override
    public String createRequestBodyForVideoId(String videoId) {
        return null;
    }
}
