/*package dev.bebomny.youtubevideodownloader.downloader.notifier;

import dev.bebomny.youtubevideodownloader.YoutubeVideoDownloaderApplication;
import io.github.gaeqs.javayoutubedownloader.stream.download.StreamDownloader;
import io.github.gaeqs.javayoutubedownloader.stream.download.StreamDownloaderNotifier;
////TODO: FIX!
public class DownloadNotifier implements StreamDownloaderNotifier {

    public long startTime;
    public long finishTime;
    public int bytesDownloaded;

    @Override
    public void onStart(StreamDownloader downloader) {
        startTime = System.currentTimeMillis();
        YoutubeVideoDownloaderApplication.getInstance().mainController.downloadAnimation.start();
    }

    @Override
    public void onDownload(StreamDownloader downloader) {
        bytesDownloaded++;
    }

    @Override
    public void onFinish(StreamDownloader downloader) {
        finishTime = System.currentTimeMillis();
        YoutubeVideoDownloaderApplication.getInstance().mainController.downloadAnimation.stop();
    }

    @Override
    public void onError(StreamDownloader downloader, Exception ex) {
        ex.printStackTrace();
        YoutubeVideoDownloaderApplication.getInstance().mainController.statusLabel.setText("Something went wrong while downloading!");
        YoutubeVideoDownloaderApplication.getInstance().mainController.downloadAnimation.stop();
    }

    public double getDownloadSpeedKbps() {
        long currentTime = System.currentTimeMillis();
        long elapsedTimeMillis = currentTime - startTime;

        if(elapsedTimeMillis == 0)
            return 0.0d;

        double downloadSpeedKbps = (bytesDownloaded * 8.0) / (elapsedTimeMillis);
        return downloadSpeedKbps;
    }
} */
