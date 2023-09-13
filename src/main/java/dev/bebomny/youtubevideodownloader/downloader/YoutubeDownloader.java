package dev.bebomny.youtubevideodownloader.downloader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dev.bebomny.youtubevideodownloader.downloader.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.downloader.stream.YoutubeVideo;
import dev.bebomny.youtubevideodownloader.downloader.tag.ITagMap;
import dev.bebomny.youtubevideodownloader.downloader.utils.ConnectionUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class YoutubeDownloader {

    private final static Pattern VIDEO_ID_PATTERN = Pattern.compile("(?<=v=|v\\/|vi=|vi\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|&v=|\\?v=)([^#\\&\\?\\n]*).*");

    //videoDetails
    private static final String KEY_VIDEO_DETAILS = "videoDetails";
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
    private static final String KEY_URL = "url";
    private static final String KEY_ITAG = "itag";
    private static final String KEY_APPROX_DURATION_MS = "approxDurationMs";
    private static final String KEY_CONTENT_LENGTH = "contentLength";
    private static final String KEY_BITRATE = "bitrate";
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";


    public static YoutubeVideo fetchVideoData(String url) {
        //https://www.youtube.com/watch?v=9SkbZKFpELo
        String videoId = matchAndGet(VIDEO_ID_PATTERN, url);

        String htmlResponse = ConnectionUtils.getJsonResponse(videoId);
        JSONObject jsonResponse = JSON.parseObject(htmlResponse);

        //key = videoDetails
        JSONObject videoDetails = jsonResponse.getJSONObject(KEY_VIDEO_DETAILS);

        String title = extractVideoTitle(videoDetails);
        String author = extractVideoAuthor(videoDetails);
        int videoLength = extractVideoLength(videoDetails);
        int viewCount = extractViewCount(videoDetails);
        String shortDescription = extractShortDescription(videoDetails);

        //thumbnails
        JSONArray thumbnailArray = extractThumbnailJSONArray(videoDetails);
        List<JSONObject> thumbnails = new ArrayList<>();
        thumbnailArray.forEach(o -> thumbnails.add((JSONObject) o));

        //key = streamingData
        JSONObject streamingData = jsonResponse.getJSONObject(KEY_STREAMING_DATA);

        int expiresInSeconds = streamingData.getInteger(KEY_EXPIRES_IN_SECONDS);

        YoutubeVideo video;

        JSONArray formats = streamingData.getJSONArray(KEY_FORMATS);
        JSONArray adaptiveFormats = streamingData.getJSONArray(KEY_ADAPTIVE_FORMATS);

        List<StreamOption> streamOptions = new LinkedList<>();
        formats.forEach(o -> streamOptions.add(parseFormat((JSONObject) o)));
        adaptiveFormats.forEach(o -> streamOptions.add(parseFormat((JSONObject) o)));

        video = new YoutubeVideo(title, author, videoLength, viewCount, shortDescription, thumbnails, expiresInSeconds, streamOptions);
        video.sortStreamOptions();

        return video;
    }

    private static String extractVideoTitle(JSONObject videoDetails) {
        return videoDetails.getString(KEY_TITLE);
    }

    private static String extractVideoAuthor(JSONObject videoDetails) {
        return videoDetails.getString(KEY_AUTHOR);
    }

    private static int extractVideoLength(JSONObject videoDetails) {
        Integer videoLength = videoDetails.getInteger(KEY_VIDEO_LENGTH);
        return videoLength != null ? videoLength : -1;
    }

    private static String extractShortDescription(JSONObject videoDetails) {
        return videoDetails.getString(KEY_SHORT_DESCRIPTION);
    }

    private static int extractViewCount(JSONObject videoDetails) {
        Integer viewCount = videoDetails.getInteger(KEY_VIEW_COUNT);
        return viewCount != null ? viewCount : -1;
    }

    private static JSONArray extractThumbnailJSONArray(JSONObject videoDetails) {
        return videoDetails.getJSONObject(KEY_THUMBNAIL).getJSONArray(KEY_THUMBNAILS);
    }

    private static StreamOption parseFormat(JSONObject format) {
        StreamOption option;
        String url = format.getString(KEY_URL);
        int iTag = format.getInteger(KEY_ITAG);
        int approxDurationMs = format.getInteger(KEY_APPROX_DURATION_MS);

        //not always there
        long contentLength = -1;
        int width = -1;
        int height = -1;
        try {
            contentLength = format.getLong(KEY_CONTENT_LENGTH);
            width = format.getInteger(KEY_WIDTH);
            height = format.getInteger(KEY_HEIGHT);
        } catch (Exception e) {
            System.out.println("ContentLength, width, height are missing! Probably a lower quality format or audio only. For now I can't handle DASH formats. sorry :/");
        }

        int bitrate = format.getInteger(KEY_BITRATE);

        try {
            if(contentLength > 0)
                option = new StreamOption(new URL(url), ITagMap.MAP.get(iTag), iTag, approxDurationMs, bitrate, width, height, contentLength);
            else
                option = new StreamOption(new URL(url), ITagMap.MAP.get(iTag), iTag, approxDurationMs, bitrate);
        } catch (IllegalArgumentException e) {
            if (ITagMap.MAP.get(iTag) == null) {
                if (iTag > 393 && iTag <= 399) return null; //Unknown streams.
                System.err.println("Couldn't find the StreamType for iTag " + iTag);
            } else e.printStackTrace();

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return option;
    }

    private static String matchAndGet(Pattern pattern, String data) {
        Matcher matcher = pattern.matcher(data);
        if (!matcher.find()) {
            throw new NoSuchElementException("Match not found!");
        }
        return matcher.group(1);
    }

}
