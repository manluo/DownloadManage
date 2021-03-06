package com.xman.downloadmanagedemo;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xman.downloadmanagedemo.utils.FileUtil;
import com.xman.downloadmanagedemo.utils.LogUtils;

public class MissionActionReceiver extends BroadcastReceiver {

    private static final String TAG = "MissionActionReceiver";
    private static final String ID = "MISSION_ID";
    private static final String MISSION_URL = "MISSION_URL";
    private static final String TYPE = "MISSION_TYPE";
    public static final String MISSION = "MISSION";

    public enum MISSION_TYPE {
        PAUSE_MISSION, RESUME_MISSION, CANCEL_MISSION
    }

    public static PendingIntent buildReceiverPendingIntent(Context context, MISSION_TYPE type, int missionID, String downloadUrl, Misson misson) {
        Intent intent = new Intent(context.getApplicationContext(), MissionActionReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(ID, missionID);
        intent.putExtra(MISSION_URL, downloadUrl);
        intent.putExtra(TYPE, type);
        intent.putExtra(MISSION, misson);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, FileUtil.randInt(1, 1000000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MISSION_TYPE type = (MISSION_TYPE) intent.getExtras().getSerializable(TYPE);
        int missionID = intent.getExtras().getInt(ID);
        String missionUrl = intent.getExtras().getString(MISSION_URL);
        Misson misson = intent.getExtras().getParcelable(MISSION);
        if (missionID == -1 || TextUtils.isEmpty(missionUrl)) {
            return;
        }
        switch (type) {
            case PAUSE_MISSION:
                LogUtils.e("----->暂停" + misson.toString());
                //只添加了一个按钮 只会走这个
                if (misson.isCancel()) {
//                    DownloadHelper.getInstance().startDownload(misson); //使用它会出现异常
                    DownloadHelper.getInstance().resumeDownload(misson);
                } else {
                    DownloadHelper.getInstance().pauseDownload(misson);
                }
                break;
            case RESUME_MISSION:
                ThreadPoolManage.getInstance().resumeMission(missionUrl);
                break;
            case CANCEL_MISSION:
                ThreadPoolManage.getInstance().pauseMission(missionUrl);
                break;
        }
    }
}
