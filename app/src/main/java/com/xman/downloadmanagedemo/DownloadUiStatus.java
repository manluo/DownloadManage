package com.xman.downloadmanagedemo;

/**
 * Created by nieyunlong on 17/6/12.
 */

public enum DownloadUiStatus {

    DOWNLOAD_WAIT("等待下载"), DOWNLOAD_READ("准备下载"),

    DOWNLOADING("正在下载"), DOWNLOAD_PAUSE("暂停下载"),

    DOWNLOAD_RESUME("恢复下载"), DOWNLOAD_SUCCESS("成功下载"),

    DOWNLOAD_FAILED("下载失败");

    private String msg;

    private DownloadUiStatus(String msg) {
        this.msg = msg;
    }

    public String getDownloadMsg() {
        return msg;
    }


}
