package dev.bebomny.youtubevideodownloader.download.stream;

import com.alibaba.fastjson.JSONObject;
import dev.bebomny.youtubevideodownloader.download.exception.FetchException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamOptionFactory {

    private static final String KEY_URL = "url";
    private static final String KEY_ITAG = "itag";
    private static final String KEY_MIMETYPE = "mimeType";
    private static final Pattern PATTERN_MIMETYPE = Pattern.compile("([^;]+); codecs=\"([^\"]+)\"");
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

        StreamOption streamOption;

        //Extract core information
        String url;
        int iTag;
        List<String> mimeType = new ArrayList<>();

        try {
            url = format.getString(KEY_URL);
            iTag = format.getInteger(KEY_ITAG);

            String stringMimeType = format.getString(KEY_MIMETYPE);
            Matcher matcher = PATTERN_MIMETYPE.matcher(stringMimeType);
            if(!matcher.find())
                throw new FetchException("MimeType extraction failed!", FetchException.Stage.FORMAT_PARSING);

            //Type
            mimeType.add(matcher.group(1));
            //Codecs
            String[] codecArray = matcher.group(2).split(",\\s*");
            mimeType.addAll(Arrays.asList(codecArray));
        } catch (Exception ignored) {
            throw new FetchException("Core format information is missing, cannot proceed!", FetchException.Stage.FORMAT_PARSING);
            return null;
        }

        //use mimeType property for creating the correct object
        //Only audio
        if (mimeType.get(0).contains("audio")) {

        }

        //Audio AND video
        if (mimeType.size() > 2) {

        }

        //only Video



        return streamOption;
    }
}
