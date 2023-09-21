package dev.bebomny.youtubevideodownloader.utils;

import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoDataUtils {

    public static String matchAndGet(Pattern pattern, String data) {
        Matcher matcher = pattern.matcher(data);
        if (!matcher.find()) {
            throw new NoSuchElementException("Match not found!");
        }
        return matcher.group(1);
    }

    public static List<StreamOption> sortStreamOptions(List<StreamOption> options) {
        options.removeIf(Objects::isNull);
        options.sort(Comparator.comparing(StreamOption::getQualityValue).reversed());
        return options;
    }

    public static String getFormattedVideoViewCount(long viewCount) {
        if (viewCount < 1000) {
            return String.valueOf(viewCount);
        } else if (viewCount < 1000000) {
            double thousands = viewCount / 1000.0;
            return new DecimalFormat("#.#k").format(thousands);
        } else {
            double millions = viewCount / 1000000.0;
            return new DecimalFormat("#.#m").format(millions);
        }
    }

    public static String getFormattedVideoLength(int videoLength) {
        int hours = videoLength / 3600;
        int minutes = (videoLength % 3600) / 60;
        int seconds = videoLength % 60;

        String formattedTime = hours == 0 ? minutes == 0 ? String.format("%02d", seconds) + "s" : String.format("%02d:%02d", minutes, seconds) + "m" : String.format("%02d:%02d:%02d", hours, minutes, seconds) + "h";
        return formattedTime;
    }
}
