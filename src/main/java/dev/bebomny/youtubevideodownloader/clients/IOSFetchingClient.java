package dev.bebomny.youtubevideodownloader.clients;

import dev.bebomny.youtubevideodownloader.downloader.stream.YoutubeVideo;

public class IOSFetchingClient implements FetchingClient{
    @Override
    public YoutubeVideo fetchVideoData(String url) {
        return null;
    }

    @Override
    public String createRequestBodyForVideoId(String videoId) {
        return null;
    }
}
