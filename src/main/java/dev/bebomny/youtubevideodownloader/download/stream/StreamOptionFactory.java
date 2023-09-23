package dev.bebomny.youtubevideodownloader.download.stream;

import com.alibaba.fastjson.JSONObject;
import dev.bebomny.youtubevideodownloader.download.exception.FetchException;
import dev.bebomny.youtubevideodownloader.download.tag.ITagMap;
import dev.bebomny.youtubevideodownloader.download.tag.StreamType;

import java.net.MalformedURLException;
import java.net.URL;
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

    //Video specific
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_FPS = "fps";
    private static final String KEY_QUALITY = "quality";
    private static final String KEY_QUALITY_LABEL = "qualityLabel";

    //Audio specific
    private static final String KEY_AUDIO_QUALITY = "audioQuality";
    private static final String KEY_AUDIO_SAMPLE_RATE = "audioSampleRate";
    private static final String KEY_AUDIO_CHANNELS = "audioChannels";

    public static StreamOption createStreamOption(JSONObject format) {
        if (format.isEmpty()) {
            throw new FetchException("Format is empty!", FetchException.Stage.FORMAT_PARSING);
        }

        StreamOption streamOption;

        System.out.println("format = " + format.toJSONString());

        //Extract core information
        String stringUrl;
        URL url;
        int iTag;
        StreamType streamType;
        List<String> mimeType = new ArrayList<>();

        //
        int approxDurationMs = -1;
        int contentLength = -1;
        int bitrate = -1;

        try {
            stringUrl = format.getString(KEY_URL);
            System.out.println("StringUrl: " + stringUrl);
            url = new URL(stringUrl);
            System.out.println("url = " + url.toExternalForm());

            iTag = format.getInteger(KEY_ITAG);
            System.out.println("iTag = " + iTag);

            streamType = ITagMap.MAP.get(iTag);
            if(streamType == null) {
                System.out.println("Unknown stream, iTag: " + iTag);
                if (iTag > 393 && iTag <= 399) System.out.println("Known unknown streams");; //Unknown streams.
                System.err.println("Couldn't find the StreamType for the iTag " + iTag);
                return new EmptyStreamOption(url, iTag);
            }
            System.out.println("streamType = " + streamType.toString());

            String stringMimeType = format.getString(KEY_MIMETYPE);
            System.out.println("stringMimeType = " + stringMimeType);
            Matcher matcher = PATTERN_MIMETYPE.matcher(stringMimeType);
            if(!matcher.find()) {
                System.out.println("Matcher failed");
                throw new FetchException("MimeType extraction failed!", FetchException.Stage.FORMAT_PARSING);
            }

            //Type
            mimeType.add(matcher.group(1));
            System.out.println("Type = " + mimeType.get(0));
            //Codecs
            String[] codecArray = matcher.group(2).split(",\\s*");
            System.out.println("codecArray = " + Arrays.toString(codecArray));
            mimeType.addAll(Arrays.asList(codecArray));

            if(format.containsKey(KEY_APPROX_DURATION_MS))
                approxDurationMs = format.getInteger(KEY_APPROX_DURATION_MS);
            System.out.println("approxDurationMs = " + approxDurationMs);

            if(format.containsKey(KEY_CONTENT_LENGTH))
                contentLength = format.getInteger(KEY_CONTENT_LENGTH);
            System.out.println("contentLength = " + contentLength);

            if(format.containsKey(KEY_BITRATE))
                bitrate = format.getInteger(KEY_BITRATE);
            System.out.println("bitrate = " + bitrate);

        } catch (MalformedURLException e) {
            throw new FetchException(
                    "URL is somehow incorrect, this shouldn't happen!",
                    FetchException.Stage.FORMAT_PARSING);
        } catch (Exception ignored) {
            throw new FetchException(
                    "Core format information is missing, cannot proceed!",
                    FetchException.Stage.FORMAT_PARSING);
        }

        //use mimeType property for creating the correct object
        //Only audio
        if (mimeType.get(0).contains("audio")) {

            String audioQuality = "Unknown";
            int audioSampleRate = -1;
            int audioChannels = -1;

            try {
               audioQuality = format.getString(KEY_AUDIO_QUALITY);
               audioSampleRate = format.getInteger(KEY_AUDIO_SAMPLE_RATE);
               audioChannels = format.getInteger(KEY_AUDIO_CHANNELS);
            } catch (Exception ignored) {
                throw new FetchException(
                        "Audio format information is missing, cannot proceed!",
                        FetchException.Stage.FORMAT_PARSING);
            }

            streamOption = new AudioStreamOption(
                    url, streamType, audioQuality,
                    iTag, approxDurationMs, contentLength, bitrate,
                    audioSampleRate, audioChannels
            );

            return streamOption;
        }

        //Audio AND video
        if (mimeType.size() > 2) {

            //Audio specific
            String audioQuality = "Unknown";
            int audioSampleRate = -1;
            int audioChannels = -1;

            //Video specific
            String videoQuality = "Unknown";
            String qualityLabel = "None";
            int width = -1;
            int height = -1;
            int fps = -1;

            try {
                audioQuality = format.getString(KEY_AUDIO_QUALITY);
                audioSampleRate = format.getInteger(KEY_AUDIO_SAMPLE_RATE);
                audioChannels = format.getInteger(KEY_AUDIO_CHANNELS);

                videoQuality = format.getString(KEY_QUALITY);
                qualityLabel = format.getString(KEY_QUALITY_LABEL);
                width = format.getInteger(KEY_WIDTH);
                height = format.getInteger(KEY_HEIGHT);
                fps = format.getInteger(KEY_FPS);
            } catch (Exception ignored) {
                throw new FetchException(
                        "Audio format information is missing, cannot proceed!",
                        FetchException.Stage.FORMAT_PARSING);
            }

            streamOption = new AudioVideoStreamOption(
                    url, streamType, videoQuality, audioQuality, qualityLabel,
                    iTag, approxDurationMs, contentLength, bitrate,
                    width, height, fps,
                    audioSampleRate, audioChannels
            );

            return streamOption;
        }

        //only Video

        if (mimeType.get(0).contains("video") && mimeType.size() <= 2) {

            //Video specific
            String videoQuality = "Unknown";
            String qualityLabel = "None";
            int width = -1;
            int height = -1;
            int fps = -1;

            try {
                videoQuality = format.getString(KEY_QUALITY);
                qualityLabel = format.getString(KEY_QUALITY_LABEL);
                width = format.getInteger(KEY_WIDTH);
                height = format.getInteger(KEY_HEIGHT);
                fps = format.getInteger(KEY_FPS);
            } catch (Exception ignored) {
                throw new FetchException(
                        "Audio format information is missing, cannot proceed!",
                        FetchException.Stage.FORMAT_PARSING);
            }

            streamOption = new VideoStreamOption(
                    url, streamType, videoQuality, qualityLabel,
                    iTag, approxDurationMs, contentLength, bitrate,
                    width, height, fps
            );

            return streamOption;
        }

        //use empty for setting unknown
        streamOption = new EmptyStreamOption(url, iTag);


        return streamOption;
    }
}
