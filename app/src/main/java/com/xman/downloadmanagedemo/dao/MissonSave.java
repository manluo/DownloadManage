package com.xman.downloadmanagedemo.dao;

import com.xman.downloadmanagedemo.utils.LogUtils;
import com.xman.downloadmanagedemo.Misson;
import com.xman.downloadmanagedemo.ThreadPoolManage;

/**
 * Created by nieyunlong on 17/6/15.
 * 数据库操作
 * 数据库保存数据操作 以及 线程池缓存操作数据
 * 进度保存没有那么频繁 使用百分比 progress%2==0 进行保存
 */

public class MissonSave implements Misson.MissonListener<Misson> {

    @Override
    public void addMisson(Misson misson) {
        LogUtils.e("---->数据库===>addMission" + misson.toString());
        DownloadDaoUtils.saveDownloadRecord(misson);
    }

    @Override
    public void onStart(Misson misson) {
        LogUtils.e("---->数据库===>onStart" + misson.toString());
        DownloadDaoUtils.saveDownloadRecord(misson);
    }

    @Override
    public void getProgress(Misson misson) {
        if (misson.getProgressCurrent() % 2 == 0) {
            LogUtils.e("---->数据库保存===>getProgress" + misson.getProgressCurrent());
            DownloadDaoUtils.saveDownloadRecord(misson);
        }
    }

    @Override
    public void onSuccess(Misson misson) {
        LogUtils.e("---->数据库===>onSuccess" + misson.toString());
        DownloadDaoUtils.saveDownloadRecord(misson);
        ThreadPoolManage.getInstance().removeMissionCache(misson);
    }

    @Override
    public void onFailed(Misson misson) {
        LogUtils.e("---->数据库===>onFailed" + misson.toString());
        DownloadDaoUtils.saveDownloadRecord(misson);
    }

    @Override
    public void onPause(Misson misson) {
        LogUtils.e("---->数据库===>onPause" + misson.toString());
        DownloadDaoUtils.saveDownloadRecord(misson);
    }

    @Override
    public void onCancel(Misson misson) {
        LogUtils.e("---->数据库===>onCancel===>会回调onPause" + misson.toString());
        DownloadDaoUtils.saveDownloadRecord(misson);
    }

    @Override
    public void onDelete(Misson misson) {
        LogUtils.e("---->数据库===>onDelete===>删除数据" + misson.toString());
        DownloadDaoUtils.deleteDownloadRecord(misson);
        ThreadPoolManage.getInstance().removeMissionCache(misson);
    }
}
