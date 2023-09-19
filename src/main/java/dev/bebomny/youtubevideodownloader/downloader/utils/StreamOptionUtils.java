package dev.bebomny.youtubevideodownloader.downloader.utils;

import dev.bebomny.youtubevideodownloader.download.StreamOption;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class StreamOptionUtils {

    public static List<StreamOption> extractVideoOptions(List<StreamOption> allOptions) {
        allOptions.removeIf(Objects::isNull);
        allOptions.removeIf(option -> !option.getType().hasVideo());
        return allOptions;
    }

    public static List<StreamOption> extractAudioOptions(List<StreamOption> allOptions) {
        allOptions.removeIf(Objects::isNull);
        allOptions.removeIf(option -> !option.getType().hasAudio() || option.getType().hasVideo());
        return allOptions;
    }

    public static List<StreamOption> sortStreamOptions(List<StreamOption> options) {
        options.removeIf(Objects::isNull);
        options.sort(Comparator.comparing(StreamOptionUtils::getOptionQualityValue).reversed());
        return options;
    }

    private static int getOptionQualityValue(StreamOption option) {
        if(option == null)
            return 0;
        return option.getQualityValue();
    }
}
