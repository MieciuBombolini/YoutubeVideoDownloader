package dev.bebomny.youtubevideodownloader.downloaders;

import dev.bebomny.youtubevideodownloader.download.SaveFormat;
import dev.bebomny.youtubevideodownloader.download.StreamMedium;
import dev.bebomny.youtubevideodownloader.download.exception.DownloadException;
import dev.bebomny.youtubevideodownloader.download.status.DownloadStatus;
import dev.bebomny.youtubevideodownloader.download.stream.AudioVideoStreamOption;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.utils.ConnectionUtils;
import dev.bebomny.youtubevideodownloader.utils.DownloadUtils;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDownloader implements Downloader{

    private final static int BUFFER_SIZE = 1024 << 2;
    private final static int CHUNK_SIZE = 10 << 17;
    private static final int UPDATE_INTERVAL_MILLIS = 200; // 0.2 second

    private final StreamOption audioOption;
    private final StreamOption videoOption;
    private long totalLength;
    private long currentLength;
    private long startTimeMillis;
    private long totalBytesRead;
    private long currentBytesRead;

    private DownloadStatus status;
    private File tempFolder;


    public DefaultDownloader(StreamOption audio, StreamOption video, File tempFolder) {
        this.audioOption = audio;
        this.videoOption = video;
        this.totalLength = 0;
        this.currentLength = 0;
        this.totalBytesRead = 0;
        this.currentBytesRead = 0;
        this.tempFolder = DownloadUtils.tempFolder; //this.tempFolder = DownloadUtils.realTempFolder;
        this.status = DownloadStatus.READY;
    }

    @Override
    public Map<StreamMedium, File> downloadSingle() {
        if(audioOption == null && videoOption == null) {
            System.out.println(
                    "No Qualities selected! " +
                    "Please select at least one and try again.");
            throw new DownloadException("No options to download!");
        }

        if(audioOption != null && videoOption != null) {
            System.out.println(
                    "Two qualities were selected, " +
                            "but somehow downloadSingle() was used, " +
                            "this shouldn't happen! " +
                    "Attempting to use downloadMulti() instead");
            return downloadMulti();
        }

        //Downloading
        this.status = DownloadStatus.DOWNLOADING;
        System.out.println("Using downloadSingle");

        StreamOption downloadableStream = videoOption != null ? videoOption : audioOption;
        StreamMedium streamMedium = downloadableStream.isVideo() ? StreamMedium.VIDEO : StreamMedium.AUDIO;
        Map<StreamMedium, File> downloadedFiles = new HashMap<>();

        //TODO: For now throw an error if contentLength is missing,
        // cover this case later in development
        if(downloadableStream.getContentLength() <= 0) {
            System.out.println("Downloading using the \"normal\" method");
            File downloadedFile = downloadNormal(downloadableStream);
            downloadedFiles.put(streamMedium, downloadedFile);
        } else {
            System.out.println("Downloading using the \"fragmented\" method");
            File downloadedFile = downloadFragmented(downloadableStream, streamMedium);
            downloadedFiles.put(streamMedium, downloadedFile);
        }

        //Finishing up

        return downloadedFiles;
    }

    @Override
    public Map<StreamMedium, File> downloadMulti() {
        if(audioOption == null || videoOption == null) {
            System.out.println(
                    "Only one quality was selected. " +
                    "Cannot invoke downloadMulti(). " +
                    "Using downloadSingle() instead.");
            return downloadSingle();
        }

        //Downloading
        this.status = DownloadStatus.DOWNLOADING;
        System.out.println("Using downloadMulti");

        Map<StreamMedium, File> downloadedFiles = new HashMap<>();

        //Download video
        File downloadedVideoFile = null;
        if(videoOption.getContentLength() <= 0) {
            System.out.println("Downloading using the \"normal\" method");
            downloadedVideoFile = downloadNormal(videoOption);
        } else {
            System.out.println("Downloading using the \"fragmented\" method");
            downloadedVideoFile = downloadFragmented(videoOption, StreamMedium.VIDEO);

        }
        if(downloadedVideoFile == null) throw new DownloadException("An error occurred while trying to download video file");
        downloadedFiles.put(StreamMedium.VIDEO, downloadedVideoFile);


        //Download audio
        File downloadedAudioFile = null;
        if(audioOption.getContentLength() <= 0) {
            System.out.println("Downloading using the \"normal\" method");
            downloadedAudioFile = downloadNormal(audioOption);
        } else {
            System.out.println("Downloading using the \"fragmented\" method");
            downloadedAudioFile = downloadFragmented(audioOption, StreamMedium.AUDIO);
        }
        if(downloadedAudioFile == null) throw new DownloadException("An error occurred while trying to download video file");
        downloadedFiles.put(StreamMedium.AUDIO, downloadedAudioFile);

        //throw new DownloadException("Unsupported as of now!");
        return downloadedFiles;
    }

    private File downloadNormal(StreamOption downloadableStream) {
        String fileName = downloadableStream.getAudioOrVideoAsString() + "_normal"; //TODO: this may cause problems, some id needs to be added
        File downloadedFile = new File(tempFolder, fileName);

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(downloadedFile, "rw");

            //Connect to url
            URL url = downloadableStream.getUrl();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "hehe");
            connection.setDoInput(true);

            //Create bytes buffer
            byte[] bytes = new byte[BUFFER_SIZE];
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());

            //Check if connection is available
            ConnectionUtils.checkConnection(connection);

            //Get length
            this.currentLength = connection.getContentLengthLong();
            this.totalLength += currentLength;
            System.out.println("Length: " + currentLength);
            this.currentBytesRead = 0;
            if(startTimeMillis == 0)
                startTimeMillis = System.currentTimeMillis();

            //Reads form stream and saves into one of the fragments
            int read;
            while ((read = bufferedInputStream.read(bytes)) > 0) {
                randomAccessFile.write(bytes, 0, read);

                //Download info
                currentBytesRead += read;
                totalBytesRead += read;

                if(Thread.interrupted())
                    throw new DownloadException("Download Interrupted!");
            }

            bufferedInputStream.close();
            randomAccessFile.close();//close() -> doesn't allow reopening
        } catch (IOException e) {
            throw new DownloadException("Something failed at normal lol");
        }

        return downloadedFile;
    }

    private File downloadFragmented(StreamOption downloadableStream, StreamMedium streamMedium) {
        long contentLength = downloadableStream.getContentLength();
        List<String> fragmentedUrls =
                DownloadUtils.createFragmentedUrls(downloadableStream.getUrl().toExternalForm(), contentLength);

        List<File> downloadedFragments = new ArrayList<>();
        //Add multithreading in a new downloader

        //Downloading
        try {
            for (String fragment : fragmentedUrls) {
                //Create new fragment
                String fragmentName = downloadableStream.getAudioOrVideoAsString() + "_fragment_" + downloadedFragments.size();
                File fragmentTarget = new File(tempFolder, fragmentName);
                RandomAccessFile randomAccessFile = new RandomAccessFile(fragmentTarget, "rw");

                //Connect to url
                URL url = new URL(fragment);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "hehe");
                connection.setDoInput(true);

                //Create bytes buffer
                byte[] bytes = new byte[BUFFER_SIZE];
                BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());

                //Check if connection is available
                ConnectionUtils.checkConnection(connection);

                //Get length
                this.currentLength = connection.getContentLengthLong();
                this.totalLength += currentLength;
                System.out.println("Length: " + currentLength);
                this.currentBytesRead = 0;
                if(startTimeMillis == 0)
                    startTimeMillis = System.currentTimeMillis();

                //Reads form stream and saves into one of the fragments
                int read;
                while ((read = bufferedInputStream.read(bytes)) > 0) {
                    randomAccessFile.write(bytes, 0, read);

                    //Download info
                    currentBytesRead += read;
                    totalBytesRead += read;

                    if(Thread.interrupted())
                        throw new DownloadException("Download Interrupted!");
                }

                bufferedInputStream.close();
                randomAccessFile.close();//close() -> doesn't allow reopening
                downloadedFragments.add(fragmentTarget);
            }
        } catch (IOException e) {
            throw new DownloadException("Something failed lol");
        }

        //Downloading end
        //Merging
        this.status = DownloadStatus.MERGING;
        File mergedVideo = null;
        try {
            mergedVideo = DownloadUtils.mergeFragments(
                    downloadedFragments,
                    streamMedium,
                    SaveFormat.getSaveFormatByFormat(
                            downloadableStream
                                    .getType()
                                    .getContainer()
                                    .getFormat()),
                    tempFolder);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DownloadException("Error occurred during video merging!");
        }

        return mergedVideo;
    }

    @Override
    public DownloadStatus getStatus() {
        return status;
    }

    @Override
    public StreamOption getAudioOption() {
        return audioOption;
    }

    @Override
    public StreamOption getVideoOption() {
        return videoOption;
    }

    @Override
    public long getTotalLength() {
        return totalLength;
    }

    @Override
    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    @Override
    public long getTotalBytesRead() {
        return totalBytesRead;
    }

    @Override
    public void setTempFolder(File newTempFolder) {
        this.tempFolder = newTempFolder;
    }

    @Override
    public File getTempFolder() {
        return tempFolder;
    }
}
