package dev.bebomny.youtubevideodownloader.clients;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import dev.bebomny.youtubevideodownloader.download.YoutubeVideo;
import dev.bebomny.youtubevideodownloader.utils.ConnectionUtils;
import dev.bebomny.youtubevideodownloader.utils.VideoDataUtils;

public class IOSFetchingClient implements FetchingClient{
    @Override
    public JSONObject fetchVideoData(String url) {
        //VideoId extraction
        String videoId = VideoDataUtils.matchAndGet(FetchingClient.VIDEO_ID_PATTERN, url);

        //Youtube Json response parsing
        String requestBody = createRequestBodyForVideoId(videoId);
        String rawJsonResponse = ConnectionUtils.getJsonResponse(requestBody);
        System.out.println("IOS response: " + rawJsonResponse);
        //JSONObject jsonResponse = JSON.parseObject(rawJsonResponse);

        //Video details
        //JSONObject videoDetails = jsonResponse.getJSONObject(FetchingClient.KEY_VIDEO_DETAILS);

        //return jsonResponse;
        return null;
    }

    @Override
    public String createRequestBodyForVideoId(String videoId) {
        return "{\"context\": {\"client\": {\"clientName\": \"IOS\", \"clientVersion\": \"17.33.2\" }}, \"videoId\": \""+ videoId +"\", \"params\": \"CgIQBg==\", \"playbackContext\": {\"contentPlaybackContext\": {\"html5Preference\": \"HTML5_PREF_WANTS\"}}, \"contentCheckOk\": true, \"racyCheckOk\": true}";
    }
}
