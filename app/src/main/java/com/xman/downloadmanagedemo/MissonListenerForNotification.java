package com.xman.downloadmanagedemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.xman.downloadmanagedemo.utils.LogUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nieyunlong on 17/6/20.
 * 通知管理
 */

public class MissonListenerForNotification implements Misson.MissonListener<Misson> {

    private Context context;
    /**
     * 暂停按钮
     */
    private PendingIntent pausePendingIntent;
    /**
     * 跳转到对应的下载界面
     */
    private Intent contentIntent;
    /**
     * 内容intent
     */
    private PendingIntent contentPendingIntent;
    private NotificationManager notificationManager;
    private RemoteViews mRemoteViews;
    private NotificationCompat.Builder mBuilder;

    public MissonListenerForNotification(Context context) {
        this.context = context;
    }

    @Override
    public void addMisson(Misson misson) {

    }

    @Override
    public void onStart(Misson misson) {
        LogUtils.e("----->====>通知---->onStart" + misson.toString());
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(context);
        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notify_custom_view);
        if (misson.isCancel()) { //暂停
            mRemoteViews.setImageViewResource(R.id.iv_notify_pause, R.drawable.ic_no_download);
        } else {
            mRemoteViews.setImageViewResource(R.id.iv_notify_pause, R.drawable.ic_pause);
        }
        //下载的名称
        mRemoteViews.setTextViewText(R.id.tv_notify_name, misson.getSaveName());
        mRemoteViews.setProgressBar(R.id.pb_notify, 100, misson.getPercentage(), true);
        mRemoteViews.setTextViewText(R.id.tv_notify_progress, misson.getPercentage() + "%");
        pausePendingIntent = MissionActionReceiver.buildReceiverPendingIntent(context, MissionActionReceiver.MISSION_TYPE.PAUSE_MISSION, misson.getMissionID(), misson.getmDownloadUrl(), misson);
        mRemoteViews.setOnClickPendingIntent(R.id.iv_notify_pause, pausePendingIntent);

        contentIntent = new Intent(context, DownloadRecordActivity.class);
        contentPendingIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContent(mRemoteViews)
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentIntent(contentPendingIntent)
                .setOngoing(true);
        notificationManager.notify(misson.getMissionID(), mBuilder.build());
    }

    @Override
    public void getProgress(Misson misson) {
        LogUtils.e("----->====>通知--->getProgress" + misson.getPercentage());
        mRemoteViews.setProgressBar(R.id.pb_notify, 100, misson.getPercentage(), false);
        mRemoteViews.setTextViewText(R.id.tv_notify_progress, misson.getPercentage() + "%");
        if (misson.isCancel()) { //暂停
            mRemoteViews.setImageViewResource(R.id.iv_notify_pause, R.drawable.ic_no_download);
        } else {
            mRemoteViews.setImageViewResource(R.id.iv_notify_pause, R.drawable.ic_pause);
        }
        notificationManager.notify(misson.getMissionID(), mBuilder.build());
    }

    @Override
    public void onSuccess(Misson misson) {
        LogUtils.e("----->====>通知--->onSuccess" + misson.toString());
        mBuilder = new android.support.v4.app.NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(misson.getSaveName())
                .setContentText("下载成功")
                .setProgress(100, misson.getPercentage(), false)
//                .setContentIntent(pendingIntent)
                .setOngoing(false);
        notificationManager.notify(misson.getMissionID(), mBuilder.build());
        cancelTimer(misson);
    }

    @Override
    public void onFailed(Misson misson) {
        LogUtils.e("----->====>通知--->onFailed" + misson.toString());
        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(misson.getSaveName())
                .setContentText("下载错误")
                .setContentInfo(misson.getPercentage() + "%")
                .setContentIntent(contentPendingIntent)
                .setOngoing(false);
        notificationManager.notify(misson.getMissionID(), mBuilder.build());
    }

    @Override
    public void onPause(Misson misson) {

    }

    @Override
    public void onCancel(Misson misson) {
        LogUtils.e("----->====>通知--->onCancel" + misson.toString());
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(context);
        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notify_custom_view);
        if (misson.isCancel()) { //暂停
            mRemoteViews.setImageViewResource(R.id.iv_notify_pause, R.drawable.ic_no_download);
        } else {
            mRemoteViews.setImageViewResource(R.id.iv_notify_pause, R.drawable.ic_pause);
        }
        //下载的名称
        mRemoteViews.setTextViewText(R.id.tv_notify_name, misson.getSaveName());
        mRemoteViews.setProgressBar(R.id.pb_notify, 100, misson.getPercentage(), true);
        mRemoteViews.setTextViewText(R.id.tv_notify_progress, misson.getPercentage() + "%");
        pausePendingIntent = MissionActionReceiver.buildReceiverPendingIntent(context, MissionActionReceiver.MISSION_TYPE.PAUSE_MISSION, misson.getMissionID(), misson.getmDownloadUrl(), misson);
        mRemoteViews.setOnClickPendingIntent(R.id.iv_notify_pause, pausePendingIntent);

        contentIntent = new Intent(context, DownloadRecordActivity.class);
        contentPendingIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContent(mRemoteViews)
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentIntent(contentPendingIntent)
                .setOngoing(true);
        notificationManager.notify(misson.getMissionID(), mBuilder.build());
    }

    public void cancelTimer(final Misson misson) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                notificationManager.cancel(misson.getMissionID());
            }
        }, 10000);
    }
}
