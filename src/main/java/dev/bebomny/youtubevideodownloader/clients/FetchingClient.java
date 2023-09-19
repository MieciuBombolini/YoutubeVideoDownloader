package dev.bebomny.youtubevideodownloader.clients;

import dev.bebomny.youtubevideodownloader.downloader.stream.YoutubeVideo;

public interface FetchingClient {

    YoutubeVideo fetchVideoData(String url);

    String createRequestBodyForVideoId(String videoId);
}
