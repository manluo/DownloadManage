package com.xman.downloadmanagedemo.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by nieyunlong on 17/6/15.
 * 下载信息
 */
@Entity
public class DownloadInfo {
    /*设置Id,为Long类型,并将其设置为自增*/
    @Id(autoincrement = true)
    private Long id;
    /*下载id*/
    private String downloadId;

    /**
     * 0 是 准备下载 1下载中 2 下载失败 3下载成功
     */
    private int downloadStatus;
    /*文件大小*/
    private long mFileSize;
    /*下载文件的进度*/
    private long currentSize;
    /*保存的文件名*/
    private String saveName;
    /*保存的文件夹*/
    private String saveDir;
    /*下载的url*/
    private String downloadUrl;


    @Generated(hash = 707771084)
    public DownloadInfo(Long id, String downloadId, int downloadStatus,
                        long mFileSize, long currentSize, String saveName, String saveDir,
                        String downloadUrl) {
        this.id = id;
        this.downloadId = downloadId;
        this.downloadStatus = downloadStatus;
        this.mFileSize = mFileSize;
        this.currentSize = currentSize;
        this.saveName = saveName;
        this.saveDir = saveDir;
        this.downloadUrl = downloadUrl;
    }


    @Generated(hash = 327086747)
    public DownloadInfo() {
    }


    @Override
    public String toString() {
        return "DownloadInfo{" +
                "id=" + id +
                ", downloadId='" + downloadId + '\'' +
                ", downloadStatus=" + downloadStatus +
                ", mFileSize=" + mFileSize +
                ", currentSize=" + currentSize +
                ", saveName='" + saveName + '\'' +
                ", saveDir='" + saveDir + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getDownloadId() {
        return this.downloadId;
    }


    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }


    public int getDownloadStatus() {
        return this.downloadStatus;
    }


    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }


    public long getMFileSize() {
        return this.mFileSize;
    }


    public void setMFileSize(long mFileSize) {
        this.mFileSize = mFileSize;
    }


    public long getCurrentSize() {
        return this.currentSize;
    }


    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }


    public String getSaveName() {
        return this.saveName;
    }


    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }


    public String getSaveDir() {
        return this.saveDir;
    }


    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }


    public String getDownloadUrl() {
        return this.downloadUrl;
    }


    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public enum DownloadStatus {
        /**
         * 正在下载 暂停下载 下载失败 下载成功
         */
        DOWNLOADING, DOWNLOAD_PAUSE, DOWNLOAD_ERROR, DOWNLOAD_SUCCESS;
    }
}
