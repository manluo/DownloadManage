package com.xman.downloadmanagedemo;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.xman.downloadmanagedemo.dao.DownloadDaoUtils;
import com.xman.downloadmanagedemo.dao.DownloadInfo;

/**
 * Created by nieyunlong on 17/6/16.
 */

public class DownloadHelper {

    public DownloadService.DownloadServiceBind mDownloadServiceBinder;
    private boolean isConnected = false;
    private Object o = new Object();
    private Context context;

    public DownloadHelper(Context context) {
        this.context = context;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadServiceBinder = (DownloadService.DownloadServiceBind) service;
            System.out.println("--->进来了吗---》downloadHelper");
            synchronized (o) {
                isConnected = true;
                o.notify();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnected = false;
        }
    };

    public void startDownload(Misson misson) {
        //是否下载过
        DownloadInfo downloadInfo = DownloadDaoUtils.getDownloadInfo(misson.getmDownloadUrl());
        if (downloadInfo != null) {  //说明已经下载过
            if (downloadInfo.getDownloadStatus() == DownloadInfo.DownloadStatus.DOWNLOAD_SUCCESS.ordinal()) { //已经下载成功了
                misson.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_SUCCESS);
                LogUtils.e("---->downloadHelper===>startDownload 已经下载陈宫了");
                Toast.makeText(Appctx.getInstance(), "该文件已经下载成功了", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        download(misson);
    }

    public void pauseDownload(Misson misson) {
        ThreadPoolManage.getInstance().pauseMission(misson);
    }

    public void download(final Misson misson) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                LogUtils.e("--->服务活着吗" + isDownloadServiceRunning());
                if (context == null) {
                    return;
                }
                if (!isDownloadServiceRunning() && context != null) {
                    context.startService(new Intent(context,
                            DownloadService.class));
                }
                if (mDownloadServiceBinder == null || isConnected == false && context != null) {
                    context.bindService(new Intent(context,
                                    DownloadService.class), connection,
                            Context.BIND_AUTO_CREATE);
                    synchronized (o) {
                        while (!isConnected) {
                            try {
                                o.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                mDownloadServiceBinder.getService().startDownload(misson);
            }
        }.start();
    }

    /*
     * 判断下载服务是否在运行
	 */
    private boolean isDownloadServiceRunning() {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (DownloadService.class.getName().equals(
                    service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void unbindDownloadService() {
        if (isDownloadServiceRunning() && isConnected && connection != null && context != null) {
            context.unbindService(connection);
        }
    }
}
