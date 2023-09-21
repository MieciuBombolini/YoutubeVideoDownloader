package dev.bebomny.youtubevideodownloader.utils;

public class FileNameSanitizer {

    private static final String invalidChars = "[/\\\\:*?\"<>|]";
    private static final int maxFileNameLength = 100;

    public static String sanitizeFileName(String originalFileName) {
        String sanitizedFileName = originalFileName.replaceAll(invalidChars, "_");
        sanitizedFileName = sanitizedFileName.replaceAll(" ", "_");

        sanitizedFileName = sanitizedFileName.trim();

        if(sanitizedFileName.length() > maxFileNameLength) {
            sanitizedFileName = sanitizedFileName.substring(0, maxFileNameLength);
        }

        return sanitizedFileName;
    }
}
