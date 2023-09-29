package dev.bebomny.youtubevideodownloader.download;

import java.util.Objects;

public enum SaveFormat {

    FLV("FLV", ".flv"),
    GP3("GP3", ".gp3"),
    M4A("M4A", ".m4a"),
    MP4("MP4", ".mp4"),
    MP3("MP3", ".mp3"),
    WEBM("WEBM", ".webm"),
    UNKNOWN("UNKNOWN", ".unknown");

    private final String name;
    private final String format;

    SaveFormat(String name, String format) {
        this.name = name;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return getName().toLowerCase();
    }

    public String getFormat() {
        return format;
    }

    public static SaveFormat getSaveFormatByName(String name) {
        if(name == null)
            return SaveFormat.MP4; //default to mp4

        if(name.startsWith("."))
            name = name.replaceFirst(".", "");

        for (SaveFormat f : SaveFormat.values()) {
            if(Objects.equals(f.getName().toLowerCase(), name.toLowerCase()))
                return f;
        }

        return SaveFormat.MP4; //default to mp4
    }

    public static SaveFormat getSaveFormatByFormat(String f) {
        if(f == null)
            return SaveFormat.MP4; //default to mp4

        for (SaveFormat f1 : SaveFormat.values()) {
            if(Objects.equals(f1.getFormat(), f))
                return f1;
        }

        return SaveFormat.MP4; //default to mp4
    }
}
