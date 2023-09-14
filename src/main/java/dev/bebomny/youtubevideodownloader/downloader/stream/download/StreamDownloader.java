package dev.bebomny.youtubevideodownloader.downloader.stream.download;

import dev.bebomny.youtubevideodownloader.downloader.exception.DownloadException;
import dev.bebomny.youtubevideodownloader.downloader.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.downloader.tag.FormatNote;
import dev.bebomny.youtubevideodownloader.downloader.utils.ConnectionUtils;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamDownloader implements Runnable{

    private final static int BUFFER_SIZE = 1024 << 2;
    private final static int CHUNK_SIZE = 10 << 17;
    private static final int UPDATE_INTERVAL_MILLIS = 200; // 0.2 second

    private final StreamOption option;
    private final File target;
    private final StreamDownloadNotifier notifier;

    private long length;
    private long startTimeMillis;
    private long totalBytesRead;

    private DownloadStatus status;

    private File tempFolder;

    public StreamDownloader(StreamOption option, File target, StreamDownloadNotifier notifier) {
        this.option = option;
        this.target = target;
        this.length = 0;
        this.totalBytesRead = 0;
        this.notifier = notifier;
        this.status = DownloadStatus.READY;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public StreamDownloadNotifier getNotifier() {
        return notifier;
    }

    public File getTarget() {
        return target;
    }

    public StreamOption getOption() {
        return option;
    }

    public long getLength() {
        return length;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getTotalBytesRead() {
        return totalBytesRead;
    }

    public double getDownloadSpeed() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTimeMillis;

        double downloadSpeed = (double) totalBytesRead / (elapsedTime / 1000.0); // bytes per second
        double downloadSpeedKilobytes = downloadSpeed / 1024;
        double downloadSpeedMegabytes = downloadSpeedKilobytes / 1024;
        return downloadSpeedMegabytes;
    }

    public boolean isDownloadFinished() {
        return status == DownloadStatus.DONE;
    }

    @Override
    public void run() {
        if (status == DownloadStatus.DOWNLOADING) throw new RuntimeException("This downloader is already running!");

        //Create temp folder
        tempFolder = new File("temp");
        if(tempFolder.mkdir())
            System.out.println("Temporary directory created successfully");
        else
            System.out.println("Failed to create TempDirectory");

        if(clearDirectory(tempFolder))
            System.out.println("Directory cleared successfully");
        else
            System.out.println("Failed to clear TempDirectory");


        if(option.getType().getFormatNote() == FormatNote.DASH) {
            System.out.println("Downloading Dashy format");
            downloadDash();
        } else {
            System.out.println("Downloading Normally");
            downloadNormal();
        }
    }

    private void downloadDash() {

        //download //TODO: support the http 'range' header -> progress
        status = DownloadStatus.DOWNLOADING;
        if(notifier != null)
            notifier.onStart(this);

        long contentLength = option.getContentLength();
        List<Map<String, String>> fragmentedUrls = buildFragmentedUrls(option.getUrl().toExternalForm(), contentLength);

        fragmentedUrls.forEach(target -> System.out.println("URL" + target.get("url")));

        List<File> downloadedFragments = new ArrayList<>();

        try {
            for(Map<String, String> fragment : fragmentedUrls) {
                //Create new Fragment
                String fragmentName = "fragment" + downloadedFragments.size();
                File fragmentTarget = new File(tempFolder, fragmentName);
                RandomAccessFile randomAccessFile = new RandomAccessFile(fragmentTarget, "rw");

                //Connect to the url
                URL url = new URL(fragment.get("url"));
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Yo mama");
                connection.setDoInput(true);

                //Create bytes buffer
                byte[] bytes = new byte[BUFFER_SIZE];
                BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
                ConnectionUtils.checkConnection(connection);

                //Get length and Check connection
                length = connection.getContentLengthLong();
                System.out.println("Length: " + length);
                totalBytesRead = 0;
                startTimeMillis = System.currentTimeMillis();

                //Reads form stream and saves into one of the fragments
                int read;
                while((read = bufferedInputStream.read(bytes)) > 0) {
                    randomAccessFile.write(bytes, 0 ,read);

                    //Download Speed
                    totalBytesRead += read;

                    if(notifier != null)
                        notifier.onDownload(this);

                    if(Thread.interrupted())
                        throw new DownloadException("Download Interrupted!");
                }


                bufferedInputStream.close();
                randomAccessFile.close(); //cant close -> doesn't allow reopening
                downloadedFragments.add(fragmentTarget);
            }
        } catch (IOException e) {
            if(notifier != null)
                notifier.onError(this, e);
        }
        //Download end

        //Merging
        status = DownloadStatus.MERGING;
        File mergedVideo = null;
        try {
            mergedVideo = mergeFragments(downloadedFragments);
        } catch (IOException e) {
            e.printStackTrace();
            if(notifier != null)
                notifier.onError(this, e);
            throw new DownloadException("Error occurred during video merging!");
        }

        //Conversion
        status = DownloadStatus.CONVERTING;

        //save to target//javaCV approach
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(mergedVideo);
             FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(target, option.getWidth(), option.getHeight());) {

            grabber.start();
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            Map<String, String> videoOptions = new HashMap<>();
            videoOptions.put("preset", "ultrafast");
            videoOptions.put("crf", "22");
            recorder.setVideoOptions(videoOptions);
            recorder.setFormat("mp4");
            recorder.start();

            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                recorder.record(frame);
            }

            recorder.stop();
            recorder.release();
            grabber.stop();
            grabber.release();

            if (target.exists()) {
                status = DownloadStatus.DONE;
                if (notifier != null) notifier.onFinish(this);
            } else {
                throw new DownloadException("JavaCV conversion failed.");
            }

        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: error during video conversion!");
            if (notifier != null)
                notifier.onError(this, e);
            return;
        }


        //Conversion End

        status = DownloadStatus.DONE;
        if (notifier != null) notifier.onFinish(this);
    }

    private void downloadNormal() {
        status = DownloadStatus.DOWNLOADING;
        if(notifier != null)
            notifier.onStart(this);
        RandomAccessFile randomAccessFile = null;

        //fix
        String newURL = option.getContentLength() != 0 ? option.getUrl().toExternalForm() + "&range=" + 0 + "-" + (option.getContentLength() / 2) : option.getUrl().toExternalForm();

        try {
            HttpsURLConnection connection = (HttpsURLConnection) option.getUrl().openConnection();
            //HttpsURLConnection connection = (HttpsURLConnection) new URL(newURL).openConnection();
            connection.setRequestProperty("User-Agent", "Yo mama");
            connection.setDoInput(true);
            //TODO: Add a file check
            if (!target.createNewFile()) throw new DownloadException("File couldn't be created");
            randomAccessFile = new RandomAccessFile(target, "rw");
            byte[] bytes = new byte[BUFFER_SIZE];
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            ConnectionUtils.checkConnection(connection);

            length = connection.getContentLengthLong();
            System.out.println("Length: " + length);
            totalBytesRead = 0;
            startTimeMillis = System.currentTimeMillis();

            int read;
            while((read = bufferedInputStream.read(bytes)) > 0) {
                randomAccessFile.write(bytes, 0 ,read);

                //Download Speed
                totalBytesRead += read;

                if(notifier != null)
                    notifier.onDownload(this);

                if(Thread.interrupted())
                    throw new DownloadException("Download Interrupted!");
            }

            status = DownloadStatus.DONE;
            bufferedInputStream.close();
            if (notifier != null) notifier.onFinish(this);
        } catch (IOException e) {
            if(notifier != null)
                notifier.onError(this, e);
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<Map<String, String>> buildFragmentedUrls(String url, long contentLength) {
        List<Map<String, String>> fragments = new ArrayList<>();

        for(long rangeStart = 0; rangeStart < contentLength; rangeStart += CHUNK_SIZE) {
            long rangeEnd = Math.min(rangeStart + CHUNK_SIZE - 1, contentLength);
            String range = rangeStart + "-" + rangeEnd;

            Map<String, String> fragment = new HashMap<>();
            fragment.put("url", ConnectionUtils.updateUrlQuery(url, "range", range));

            fragments.add(fragment);
        }

        return fragments;
    }

    private File mergeFragments(List<File> fragments) throws IOException {
        File mergedVideo = new File(tempFolder, "mergedVideo" + option.getType().getContainer().getFormat());

        try (FileOutputStream fileOutputStream = new FileOutputStream(mergedVideo)) {
            for (File fragment : fragments) {
                try (FileInputStream fileInputStream = new FileInputStream(fragment)) {
                    byte[] bytes = new byte[BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, bytesRead);
                    }
                }
            }
        }
        return mergedVideo;
    }

    private boolean clearDirectory(File dir) {
        if(dir.exists()) {
            System.out.println("Directory doesnt exist!");
            return false;
        }

        File[] files = dir.listFiles();
        if(files == null) {
            System.out.println("No files present in this directory!");
            return false;
        }


        for (File file : files) {
            if(file.isFile()) {
                if(file.delete()) {
                    System.out.println("Deleted file: " + file.getName());
                } else {
                    System.out.println("Failed to delete file: " + file.getName());
                }
            }
        }

        return true;
    }

    public static enum DownloadStatus {
        READY,
        DOWNLOADING,
        MERGING,
        CONVERTING,
        DONE
    }
}
