package dev.bebomny.youtubevideodownloader.download.stream;

import com.alibaba.fastjson.JSONObject;
import dev.bebomny.youtubevideodownloader.download.exception.FetchException;

import java.util.regex.Pattern;

public class StreamOptionFactory {

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

    public static StreamOption createStreamOption(JSONObject format) {
        if (format.isEmpty()) {
            throw new FetchException("Format is empty!", FetchException.Stage.FORMAT_PARSING);
            return null;
        }

        //Extract core information
        String url;
        int iTag;

        try {
            url = format.getString(KEY_URL);
            iTag = format.getInteger(KEY_ITAG);
        } catch (Exception ignored) {
            throw new FetchException("Core format information is missing, cannot proceed!", FetchException.Stage.FORMAT_PARSING);
            return null;
        }

        //use mimeType property for creating the correct object


    }
}
