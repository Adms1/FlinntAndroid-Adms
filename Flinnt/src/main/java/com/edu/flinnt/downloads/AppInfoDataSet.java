package com.edu.flinnt.downloads;

import java.io.Serializable;


public class AppInfoDataSet implements Serializable {
    public static final int STATUS_NOT_DOWNLOAD = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECT_ERROR = 2;
    public static final int STATUS_DOWNLOADING = 3;
    public static final int STATUS_PAUSED = 4;
    public static final int STATUS_DOWNLOAD_ERROR = 5;
    public static final int STATUS_COMPLETE = 6;
    public static final int STATUS_INSTALLED = 7;
    public static final int STATUS_CANCEL = 8;

    private String name;
    private String packageName;
    private String id;
    private String image;
    private String url;
    private int progress;
    private String downloadPerSize;
    private String downloadFilePath;
    private String attachmentDoEncode;
    private int status;

    public AppInfoDataSet() {
    }

    public AppInfoDataSet(String id, String name, String image, String url , String encode) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.url = url;
        this.attachmentDoEncode = encode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDownloadPerSize() {
        return downloadPerSize;
    }

    public void setDownloadPerSize(String downloadPerSize) {
        this.downloadPerSize = downloadPerSize;
    }

    public String getDownloadFilePath() {
        return downloadFilePath;
    }

    public void setDownloadFilePath(String downloadFilePath) {
        this.downloadFilePath = downloadFilePath;
    }

    public String getAttachmentDoEncode() {
        return attachmentDoEncode;
    }

    public void setAttachmentDoEncode(String attachmentDoEncode) {
        this.attachmentDoEncode = attachmentDoEncode;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusText() {
        switch (status) {
            case STATUS_NOT_DOWNLOAD:
                return "Not Download";
            case STATUS_CONNECTING:
                return "Connecting";
            case STATUS_CONNECT_ERROR:
                return "Connect Error";
            case STATUS_DOWNLOADING:
                return "Downloading";
            case STATUS_PAUSED:
                return "Pause";
            case STATUS_DOWNLOAD_ERROR:
                return "Download Error";
            case STATUS_COMPLETE:
                return "Complete";
            case STATUS_INSTALLED:
                return "Installed";
            case STATUS_CANCEL:
                return "Cancel";
            default:
                return "Not Download";
        }
    }


    public void setStatus(int status) {
        this.status = status;
    }


}
