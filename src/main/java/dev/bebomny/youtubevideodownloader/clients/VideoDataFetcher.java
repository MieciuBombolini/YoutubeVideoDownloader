package dev.bebomny.youtubevideodownloader.clients;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dev.bebomny.youtubevideodownloader.download.YoutubeVideo;
import dev.bebomny.youtubevideodownloader.download.exception.FetchException;
import dev.bebomny.youtubevideodownloader.download.status.FetchingStatus;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOptionFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class VideoDataFetcher {

    //videoDetails
    private static final String KEY_VIDEO_DETAILS = "videoDetails";
    private static final String KEY_VIDEO_ID = "videoId";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_VIDEO_LENGTH = "lengthSeconds";
    private static final String KEY_SHORT_DESCRIPTION = "shortDescription";
    private static final String KEY_VIEW_COUNT = "viewCount";
    private static final String KEY_THUMBNAIL = "thumbnail";
    private static final String KEY_THUMBNAILS = "thumbnails";

    //streamingData
    private static final String KEY_STREAMING_DATA = "streamingData";
    private static final String KEY_EXPIRES_IN_SECONDS = "expiresInSeconds";
    private static final String KEY_FORMATS = "formats";
    private static final String KEY_ADAPTIVE_FORMATS = "adaptiveFormats";

    private final ClientManager clientManager;

    private FetchingStatus status;

    public VideoDataFetcher(ClientManager clientManager) {
        this.clientManager = clientManager;
        this.status = FetchingStatus.READY;
    }

    public YoutubeVideo fetchVideoData(String url, String... clients) {
        status = FetchingStatus.FETCHING;
        if(clientManager.getFetchingClients().isEmpty())
            throw new FetchException("No Fetching Clients Present, THIS SHOULDN'T HAPPEN!", FetchException.Stage.PREPARING);

        //Fetching data
        status = FetchingStatus.FETCHING;
        JSONObject jsonVideoData = null;

        Optional<FetchingClient> client;
        for (String string : clients) {
            client = clientManager.getFetchingClient(string);
            if(client.isEmpty()) continue;
            try {
                jsonVideoData = client.get().fetchVideoData(url);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if(jsonVideoData != null)
                break;
        }

        if(jsonVideoData == null)
            throw new FetchException("Data json fetching failed!", FetchException.Stage.CONNECTION);

        //Parsing data
        status = FetchingStatus.PARSING_DETAILS;

        //Video details
        JSONObject videoDetails = jsonVideoData.getJSONObject(KEY_VIDEO_DETAILS);

        String videoId = videoDetails.getString(KEY_VIDEO_ID);
        String title = videoDetails.getString(KEY_TITLE);
        String author = videoDetails.getString(KEY_AUTHOR);
        int videoLength = videoDetails.getIntValue(KEY_VIDEO_LENGTH);
        int viewCount = videoDetails.getIntValue(KEY_VIEW_COUNT);
        String shortDescription = videoDetails.getString(KEY_SHORT_DESCRIPTION);

        //thumbnails
        JSONArray thumbnailArray = extractThumbnailJSONArray(videoDetails);
        List<JSONObject> thumbnails = new ArrayList<>();
        thumbnailArray.forEach(o -> thumbnails.add((JSONObject) o));

        //Streaming data
        status = FetchingStatus.PARSING_FORMATS;
        JSONObject streamingData = jsonVideoData.getJSONObject(KEY_STREAMING_DATA);

        int expiresInSeconds = streamingData.getIntValue(KEY_EXPIRES_IN_SECONDS);

        YoutubeVideo video = new YoutubeVideo(
                title, author, url, videoId,
                videoLength, viewCount, shortDescription,
                thumbnails, expiresInSeconds
        );

        List<StreamOption> parsedStreamOptions = new LinkedList<>();

        JSONArray formats = streamingData.getJSONArray(KEY_FORMATS);
        JSONArray adaptiveFormats = streamingData.getJSONArray(KEY_ADAPTIVE_FORMATS);

        formats.forEach(o -> parsedStreamOptions.add(StreamOptionFactory.createStreamOption((JSONObject) o)));
        adaptiveFormats.forEach(o -> parsedStreamOptions.add(StreamOptionFactory.createStreamOption((JSONObject) o)));

        parsedStreamOptions.forEach(streamOption -> {
            if(streamOption.isAudio() && !streamOption.isVideo())
                video.getAudioStreamOptions().add(streamOption);
            else
                video.getVideoStreamOptions().add(streamOption);

        });

        return video;
    }

    public void setStatus(FetchingStatus status) {
        this.status = status;
    }

    public FetchingStatus getStatus() {
        return status;
    }

    private static JSONArray extractThumbnailJSONArray(JSONObject videoDetails) {
        return videoDetails.getJSONObject(KEY_THUMBNAIL).getJSONArray(KEY_THUMBNAILS);
    }
}
