package dev.bebomny.youtubevideodownloader.downloader.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ConnectionUtils {

    private static final String YOUTUBE_URL = "https://www.youtube.com/";
    private static final String YOUTUBE_PLAYER = "youtubei/v1/player?key=AIzaSyA8eiZmM1FaDVjRy-df2KTyQ_vz_yYM39w";
    private static final String QUERY_SPACER = "&";
    private static final String QUERY_EQUALS = "=";

    public static String getJsonResponse(String videoId) {
        try {
            URL url = new URL(YOUTUBE_URL + YOUTUBE_PLAYER);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setDoOutput(true);

            String requestBody = getRequestBodyForVideoId(videoId);

            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input);
            }

            int responseCode = connection.getResponseCode();
            if(responseCode == HttpsURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = bufferedReader.readLine()) != null) {
                        response.append(inputLine);
                    }
                } finally {
                    connection.disconnect();
                }
                return response.toString();
            } else {
                System.err.println("HTTP Request failed with response code: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "An error occurred while getting a JSON response!";
        }
        return null;
    }

    public static void checkConnection(HttpsURLConnection connection) throws IOException {
        int code = connection.getResponseCode();
        String message = connection.getResponseMessage();

        switch (code) {
            case HttpURLConnection.HTTP_PARTIAL -> {
                return;
            }
            case HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_PERM ->
                // rfc2616: the user agent MUST NOT automatically redirect the
                // request unless it can be confirmed by the user
                    throw new RuntimeException("Download moved" + " (" + message + ")");
            case HttpURLConnection.HTTP_PROXY_AUTH -> throw new RuntimeException("Proxy auth" + " (" + message + ")");
            case HttpURLConnection.HTTP_FORBIDDEN ->
                    throw new RuntimeException("Http forbidden: " + code + " (" + message + ")");
            case 416 -> throw new RuntimeException("Requested range nt satisfiable" + " (" + message + ")");
        }
    }

    public static String updateUrlQuery(String url, String key, String value) {
        String newParameter = QUERY_SPACER + key + QUERY_EQUALS + value;
        return url + newParameter;
    }

    public static String getRequestBodyForVideoId(String videoId) {
        return "{\"context\": {\"client\": {\"clientName\": \"ANDROID\", \"clientVersion\": \"17.31.35\", \"androidSdkVersion\": 30 }}, \"videoId\": \"" + videoId +"\", \"params\": \"CgIQBg==\", \"playbackContext\": {\"contentPlaybackContext\": {\"html5Preference\": \"HTML5_PREF_WANTS\"}}, \"contentCheckOk\": true, \"racyCheckOk\": true}";
    }
}
