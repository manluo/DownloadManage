package com.xman.downloadmanagedemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nieyunlong on 17/6/16.
 */

public class MissionListenerForNotification implements Misson.MissonListener<Misson> {
    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notifyBuilder;

    private PendingIntent pausePendingIntent, resumePendingIntent, cancelPendingIntent;
    private Intent contentIntent;
    private PendingIntent contentPendingIntent;

    public MissionListenerForNotification(Context context) {
        this.context = context;
    }

    @Override
    public void addMisson(Misson misson) {

    }

    @Override
    public void onStart(Misson misson) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        contentIntent = new Intent(context, DownloadRecordActivity.class);
        contentPendingIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notifyBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(misson.getSaveName())
                .setContentText("准备下载")
                .setProgress(100, 100, true)
                .setContentInfo("0%")
                .setContentIntent(contentPendingIntent)
                .setOngoing(true);
        notificationManager.notify(misson.getMissionID(), notifyBuilder.build());
    }

    @Override
    public void getProgress(Misson misson) {
        notifyBuilder.setProgress(100, misson.getPercentage(), false);
        notifyBuilder.setContentInfo(misson.getPercentage() + "%");
        notifyBuilder.setContentText("正在下载");
        notificationManager.notify(misson.getMissionID(), notifyBuilder.build());
    }

    @Override
    public void onSuccess(Misson misson) {
//        Intent intent = new Intent(context, DownloadActivity.class);
//        DownloadDetail detail = (DownloadDetail)mission.getExtraInformation(mission.getUri());
//        intent.putExtra("Detail", detail);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.progress_indicator)
                .setContentTitle(misson.getSaveName())
                .setContentText("下载成功")
                .setProgress(100, misson.getPercentage(), false)
//                .setContentIntent(pendingIntent)
                .setOngoing(false);
        notificationManager.notify(misson.getMissionID(), notifyBuilder.build());
        cancelTimer(misson);
    }

    @Override
    public void onFailed(Misson misson) {
        notifyBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(misson.getSaveName())
                .setContentText("下载错误")
                .setContentInfo(misson.getPercentage() + "%")
                .setContentIntent(contentPendingIntent)
                .setOngoing(false);
        notificationManager.notify(misson.getMissionID(), notifyBuilder.build());
    }

    @Override
    public void onPause(Misson misson) {
        notifyBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(misson.getSaveName())
                .setContentText("暂停")
                .setProgress(100, misson.getPercentage(), false)
                .setContentInfo(misson.getPercentage() + "%")
                .addAction(R.drawable.ic_action_rocket, "恢复下载", resumePendingIntent)
                .addAction(R.drawable.ic_action_cancel, "取消下载", cancelPendingIntent)
                .setContentIntent(contentPendingIntent)
                .setOngoing(true);
        notificationManager.notify(misson.getMissionID(), notifyBuilder.build());
    }

    @Override
    public void onCancel(Misson misson) {
        notifyBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(misson.getSaveName())
                .setContentText("暂停下载")
                .setContentInfo(misson.getPercentage() + "%")
                .setContentIntent(contentPendingIntent)
                .setOngoing(false);
        notificationManager.notify(misson.getMissionID(), notifyBuilder.build());
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
