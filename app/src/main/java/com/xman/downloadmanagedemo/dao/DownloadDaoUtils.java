package com.xman.downloadmanagedemo.dao;

import com.xman.downloadmanagedemo.DownloadUiStatus;
import com.xman.downloadmanagedemo.utils.LogUtils;
import com.xman.downloadmanagedemo.Misson;
import com.xman.downloadmanagedemo.gen.DownloadInfoDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyunlong on 17/6/15.
 */

public class DownloadDaoUtils {

    /**
     * 插入下载条目
     *
     * @param misson 任务对象
     */
    public synchronized static boolean saveDownloadRecord(Misson misson) {
        if (misson == null) {
            throw new IllegalStateException("任务不能为空");
        }
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setCurrentSize(misson.getmDownloadedSize());
        if (misson.getDownloadUiStatus() == DownloadUiStatus.DOWNLOAD_PAUSE) {  //暂停
            downloadInfo.setDownloadStatus(DownloadInfo.DownloadStatus.DOWNLOAD_PAUSE.ordinal());
        } else if (misson.getDownloadUiStatus() == DownloadUiStatus.DOWNLOAD_SUCCESS) { //成功
            downloadInfo.setDownloadStatus(DownloadInfo.DownloadStatus.DOWNLOAD_SUCCESS.ordinal());
        } else if (misson.getDownloadUiStatus() == DownloadUiStatus.DOWNLOAD_FAILED) { //失败了
            downloadInfo.setDownloadStatus(DownloadInfo.DownloadStatus.DOWNLOAD_ERROR.ordinal());
        } else if (misson.getDownloadUiStatus() == DownloadUiStatus.DOWNLOAD_WAIT) {  //等待下载
            downloadInfo.setDownloadStatus(DownloadInfo.DownloadStatus.DOWNLOAD_WAIT.ordinal());
        } else {
            downloadInfo.setDownloadStatus(DownloadInfo.DownloadStatus.DOWNLOADING.ordinal());
        }
        downloadInfo.setDownloadUrl(misson.getmDownloadUrl());
        downloadInfo.setMFileSize(misson.getmFileSize()); //文件大小
        downloadInfo.setSaveDir(misson.getSaveDir());
        downloadInfo.setSaveName(misson.getSaveName());
        boolean flag = false;
        try {
            DownloadInfoDao downloadDao = EntityManager.getInstance().getDownloadDao();
            //查询数据库是否有这Url
            DownloadInfo downloadTask = downloadDao.queryBuilder().where(DownloadInfoDao.Properties.DownloadUrl.eq(downloadInfo.getDownloadUrl())).build().unique();
            if (downloadTask == null) {  //数据库没有这条数据 需要插入
                downloadDao.insert(downloadInfo);
            } else {
                downloadInfo.setId(downloadTask.getId());
                downloadDao.update(downloadInfo);
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除某一条下载记录
     *
     * @param misson
     */
    public synchronized static void deleteDownloadRecord(Misson misson) {
        DownloadInfoDao downloadDao = EntityManager.getInstance().getDownloadDao();
        DownloadInfo findDownloadRecord = downloadDao.queryBuilder().where(DownloadInfoDao.Properties.DownloadUrl.eq(misson.getmDownloadUrl())).build().unique();
        if (findDownloadRecord != null) {
            LogUtils.e("=======>数据库查询到下载数据" + findDownloadRecord.getDownloadUrl());
            downloadDao.delete(findDownloadRecord);
        } else {
            LogUtils.e("=======>数据库没有查询到下载数据===>error" + misson.getmDownloadUrl());
        }
    }
    public synchronized static void deleteAllDownloadRecord() {
        DownloadInfoDao downloadDao = EntityManager.getInstance().getDownloadDao();
        List<DownloadInfo> downloadList = downloadDao.queryBuilder().where(DownloadInfoDao.Properties.DownloadStatus.in(DownloadInfo.DownloadStatus.DOWNLOADING.ordinal(), DownloadInfo.DownloadStatus.DOWNLOAD_WAIT.ordinal(), DownloadInfo.DownloadStatus.DOWNLOAD_PAUSE.ordinal(), DownloadInfo.DownloadStatus.DOWNLOAD_ERROR.ordinal())).build().list();
        if (downloadList != null) {
            LogUtils.e("=======>数据库查询到下载数据");
            downloadDao.deleteInTx(downloadList);
        } else {
            LogUtils.e("=======>数据库没有查询到下载数据===>error");
        }
    }

    /**
     * 点击下载是否有已经下载过了
     *
     * @param downloadInfo
     * @return
     */
    public synchronized static boolean isDownloading(DownloadInfo downloadInfo) {
        DownloadInfoDao downloadDao = EntityManager.getInstance().getDownloadDao();
        if (downloadDao != null) {
            List<DownloadInfo> downloadList = downloadDao.queryBuilder().build().list();
            if (downloadList != null && downloadList.size() > 0) {
                for (int i = 0; i < downloadList.size(); i++) {
                    if (downloadList.get(i).getDownloadUrl().equals(downloadInfo.getDownloadUrl())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public synchronized static boolean isDownloading(String downloadUrl) {
        DownloadInfoDao downloadDao = EntityManager.getInstance().getDownloadDao();
        if (downloadDao != null) {
            DownloadInfo downloadList = downloadDao.queryBuilder().where(DownloadInfoDao.Properties.DownloadUrl.eq(downloadUrl)).build().unique();
            if (downloadList != null) {
                return true;
            }
        }
        return false;
    }

    public synchronized static DownloadInfo getDownloadInfo(String downloadUrl) {
        DownloadInfoDao downloadDao = EntityManager.getInstance().getDownloadDao();
        if (downloadDao != null) {
            DownloadInfo downloadInfo = downloadDao.queryBuilder().where(DownloadInfoDao.Properties.DownloadUrl.eq(downloadUrl)).build().unique();
            return downloadInfo;
        }
        return null;
    }


    public synchronized static boolean isDownloading(Misson misson) {
        List<DownloadInfo> downloadingList = getDownloading();
        if (downloadingList.size() > 0) {
            for (int i = 0; i < downloadingList.size(); i++) {
                if (downloadingList.get(i).getDownloadUrl().equals(misson.getmDownloadUrl())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取下载列表
     *
     * @return
     */
    public static synchronized List<DownloadInfo> getDownloading() {
        DownloadInfoDao downloadDao = EntityManager.getInstance().getDownloadDao();
        if (downloadDao != null) {
            List<DownloadInfo> downloadList = downloadDao.queryBuilder().where(DownloadInfoDao.Properties.DownloadStatus.in(DownloadInfo.DownloadStatus.DOWNLOADING.ordinal(), DownloadInfo.DownloadStatus.DOWNLOAD_WAIT.ordinal(), DownloadInfo.DownloadStatus.DOWNLOAD_PAUSE.ordinal(), DownloadInfo.DownloadStatus.DOWNLOAD_ERROR.ordinal())).build().list();
            if (downloadList != null) {
                return downloadList;
            }
        }
        return new ArrayList<DownloadInfo>();
    }
}
