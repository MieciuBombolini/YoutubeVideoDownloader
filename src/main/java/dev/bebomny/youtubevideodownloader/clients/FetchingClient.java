package dev.bebomny.youtubevideodownloader.clients;

import com.alibaba.fastjson.JSONObject;
import dev.bebomny.youtubevideodownloader.download.YoutubeVideo;

import java.util.regex.Pattern;

public interface FetchingClient {

    Pattern VIDEO_ID_PATTERN = Pattern.compile("(?<=v=|v\\/|vi=|vi\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|&v=|\\?v=)([^#\\&\\?\\n]*).*");
    String KEY_VIDEO_DETAILS = "videoDetails";
    String KEY_STREAMING_DATA = "streamingData";


    JSONObject fetchVideoData(String url);

    String createRequestBodyForVideoId(String videoId);
}
