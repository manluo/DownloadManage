package com.xman.downloadmanagedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xman.downloadmanagedemo.dao.MissonSave;

/**
 * Created by nieyunlong on 17/6/15.
 * adapter 使用广播更新进度
 * 关于服务什么时候 需要停止 可以在baseActivity里面 查询一次 是否还有任务存在 不存在就杀死服务 @see DownloadHelper.class
 */

public class DownloadService extends Service implements Misson.MissonListener<Misson> {

    public static final String SEND_MISSION_ACTION = "mission_downloading";

    public static final String SEND_BROADCAST_MISSION_KEY = "mission_downloading_key";

    public static final String SEND_BROADCAST_MISSION_BUNDLE = "mission_downloading_bundle";

    private DownloadServiceBind messageBinder = new DownloadServiceBind();

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("---->服务开启===>onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messageBinder;
    }

    @Override
    public void addMisson(Misson misson) {
        sendMissionBroadCast(misson);
    }

    @Override
    public void onStart(Misson misson) {
        sendMissionBroadCast(misson);
    }

    @Override
    public void getProgress(Misson misson) {
        sendMissionBroadCast(misson);
    }

    @Override
    public void onSuccess(Misson misson) {
        LogUtils.e("---->downloadService====>onSuccess" + misson.getmDownloadUrl());
        sendMissionBroadCast(misson);
//        //如果下载任务没有了 自杀
//        if (ThreadPoolManage.getInstance().getMissionCache() == null || ThreadPoolManage.getInstance().getMissionCache().size() <= 0) {
//            DownloadHelper.getInstance().unbindDownloadService();
//        }
    }

    @Override
    public void onFailed(Misson misson) {
        sendMissionBroadCast(misson);
    }

    @Override
    public void onPause(Misson misson) {
        misson.setCancel(true);
        sendMissionBroadCast(misson);
    }

    @Override
    public void onCancel(Misson misson) {
        //会走onPause 回调
        misson.setCancel(true);
        sendMissionBroadCast(misson);
    }

    public class DownloadServiceBind extends Binder {

        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    public void startDownload(Misson misson) {
        if (misson == null) {
            return;
        }
        //如果已经下载了 只需要恢复就可以了 不需要重新add监听执行 其实效果是一样的 内部监听不会重复注册
        if (ThreadPoolManage.getInstance().getMissionCache().size() > 0 && ThreadPoolManage.getInstance().getMissionCache().contains(misson)) {
            ThreadPoolManage.getInstance().resumeMission(misson);
            return;
        }
        misson.registerMissonListener(new MissonSave());
        misson.registerMissonListener(new MissionListenerForNotification(DownloadService.this));
        misson.registerMissonListener(DownloadService.this);
        ThreadPoolManage.getInstance().addMisson(misson);
        ThreadPoolManage.getInstance().execute(misson);
    }

    @Override
    public void onDestroy() {
        LogUtils.e("---->服务死掉===>onDestroy===>");
        super.onDestroy();
    }

    /**
     * 把进度发出去
     *
     * @param misson
     */
    public void sendMissionBroadCast(Misson misson) {
        Intent intent = new Intent();
        intent.setAction(SEND_MISSION_ACTION);
        Bundle bundle = new Bundle();
        bundle.putParcelable(SEND_BROADCAST_MISSION_KEY, misson);
        intent.putExtra(SEND_BROADCAST_MISSION_BUNDLE, bundle);
        sendBroadcast(intent);
    }

}
