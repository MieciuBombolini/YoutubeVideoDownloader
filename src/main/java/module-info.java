module dev.bebomny.youtubevideodownloader {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    //requires JavaYoutubeDownloader;
    requires fastjson;
    //requires org.openjdk.nashorn;
    requires org.bytedeco.javacv;
    requires org.bytedeco.ffmpeg;
    requires org.bytedeco.ffmpeg.platform;
    requires org.bytedeco.ffmpeg.windows.x86_64;
    //requires org.bytedeco.javacv.platform;
    //requires org.bytedeco.ffmpeg.platform;
    //requires org.bytedeco.ffmpeg;
    //requires org.bytedeco.javacv;

    opens dev.bebomny.youtubevideodownloader to javafx.fxml;
    exports dev.bebomny.youtubevideodownloader;
    exports dev.bebomny.youtubevideodownloader.clients;
    exports dev.bebomny.youtubevideodownloader.download;
    exports dev.bebomny.youtubevideodownloader.download.stream;
    exports dev.bebomny.youtubevideodownloader.download.status;
    exports dev.bebomny.youtubevideodownloader.download.tag;
    exports dev.bebomny.youtubevideodownloader.download.exception;
    exports dev.bebomny.youtubevideodownloader.downloaders;
    exports dev.bebomny.youtubevideodownloader.animation;
    exports dev.bebomny.youtubevideodownloader.utils;
    exports dev.bebomny.youtubevideodownloader.converters;
    opens dev.bebomny.youtubevideodownloader.clients to javafx.fxml;
}