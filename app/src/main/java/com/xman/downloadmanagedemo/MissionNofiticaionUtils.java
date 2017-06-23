package com.xman.downloadmanagedemo;

import android.app.NotificationManager;
import android.content.Context;

/**
 * Created by nieyunlong on 17/6/23.
 * 通知管理
 */

public class MissionNofiticaionUtils {

    public static void deleteNotifyById(Context context, int missionId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(missionId);
    }
}
