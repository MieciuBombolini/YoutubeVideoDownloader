package dev.bebomny.youtubevideodownloader.downloader.stream.download;

public interface StreamDownloadNotifier {

    void onStart(StreamDownloader downloader);

    void onDownload(StreamDownloader downloader);

    void onFinish(StreamDownloader downloader);

    void onError(StreamDownloader downloader, Exception ex);

}
