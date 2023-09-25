package dev.bebomny.youtubevideodownloader.downloaders;

import dev.bebomny.youtubevideodownloader.download.exception.DownloadException;
import dev.bebomny.youtubevideodownloader.download.status.DownloadStatus;
import dev.bebomny.youtubevideodownloader.download.stream.StreamOption;
import dev.bebomny.youtubevideodownloader.utils.DownloadUtils;

import java.io.File;
import java.util.List;

public class DefaultDownloader implements Downloader{

    private final static int BUFFER_SIZE = 1024 << 2;
    private final static int CHUNK_SIZE = 10 << 17;
    private static final int UPDATE_INTERVAL_MILLIS = 200; // 0.2 second

    private final StreamOption option;
    private final File destination;

    private long length;
    private long startTimeMillis;
    private long totalBytesRead;

    private DownloadStatus status;


    public DefaultDownloader(StreamOption option, File destination) {
        this.option = option;
        this.destination = destination;
        this.length = 0;
        this.totalBytesRead = 0;
        this.status = DownloadStatus.READY;
    }

    @Override
    public List<File> download() {

        return null;
    }

    @Override
    public DownloadStatus getStatus() {
        return status;
    }

    @Override
    public StreamOption getOption() {
        return option;
    }

    @Override
    public File getDestination() {
        return destination;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    @Override
    public long getBytesRead() {
        return totalBytesRead;
    }

}
