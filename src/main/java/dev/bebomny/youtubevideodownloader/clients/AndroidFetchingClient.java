package dev.bebomny.youtubevideodownloader.clients;

import dev.bebomny.youtubevideodownloader.downloader.stream.YoutubeVideo;

public class AndroidFetchingClient implements FetchingClient{
    @Override
    public YoutubeVideo fetchVideoData(String url) {
        return null;
    }

    @Override
    public String createRequestBodyForVideoId(String videoId) {
        return "{\"context\": {\"client\": {\"clientName\": \"ANDROID\", \"clientVersion\": \"17.31.35\", \"androidSdkVersion\": 30 }}, \"videoId\": \"" + videoId +"\", \"params\": \"CgIQBg==\", \"playbackContext\": {\"contentPlaybackContext\": {\"html5Preference\": \"HTML5_PREF_WANTS\"}}, \"contentCheckOk\": true, \"racyCheckOk\": true}";
    }
}
