package com.xman.downloadmanagedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.xman.downloadmanagedemo.dao.DownloadDaoUtils;
import com.xman.downloadmanagedemo.dao.DownloadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyunlong on 17/6/15.
 * 下载记录list
 */

public class DownloadRecordActivity extends AppCompatActivity {

    private ListView listview_record;


    public DownloadRecordAdapter downloadRecordAdapter;

    List<Misson> listDownload = new ArrayList<>();
    private MissionBroadCast missionBradCast;

    private void getData(DownloadRecordAdapter adapter) {
        ArrayList<Misson> listCacheMission = ThreadPoolManage.getInstance().getMissionCache();
        List<DownloadInfo> downloadInfos = DownloadDaoUtils.getDownloading();
        LogUtils.e("=====>下载列表" + downloadInfos.toString());
        if (listCacheMission == null || listCacheMission.size() <= 0) {
            if (downloadInfos.size() > 0) {
                for (int i = 0; i < downloadInfos.size(); i++) {
                    DownloadInfo downloadItem = downloadInfos.get(i);
                    Misson misson = new Misson(downloadItem.getDownloadUrl(), downloadItem.getCurrentSize(), downloadItem.getMFileSize(), downloadItem.getSaveDir(), downloadItem.getSaveName());
//                    int downloadStatusDao = downloadItem.getDownloadStatus();
//                    LogUtils.e("---->状态" + downloadStatusDao);
//                    if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOADING.ordinal()) { //正在下载
//                        misson.setDownloadUiStatus(DownloadUiStatus.DOWNLOADING);
//                    } else if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_PAUSE.ordinal() || downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_ERROR.ordinal()) { //暂停下载
//                        misson.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_PAUSE);
//                        misson.setCancel(true);
//                    } else if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_WAIT.ordinal()) { //正在等待
//                        misson.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_WAIT);
//                    }
                    adapter.setUIStatus(misson, downloadItem);
                    listDownload.add(misson);
//                    downloadHelper.startDownload(misson);
                }
            }
        } else if (listCacheMission.size() > 0) {
            for (int i = 0; i < downloadInfos.size(); i++) {
                DownloadInfo downloadItem = downloadInfos.get(i);
                boolean isFind = false;
                for (int y = 0; y < listCacheMission.size(); y++) {
                    if (downloadItem.getDownloadUrl().equals(listCacheMission.get(y).getmDownloadUrl())) {
                        Misson mission = listCacheMission.get(y);
//                        int downloadStatusDao = downloadItem.getDownloadStatus();
//                        if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOADING.ordinal()) { //正在下载
//                            mission.setDownloadUiStatus(DownloadUiStatus.DOWNLOADING);
//                        } else if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_PAUSE.ordinal() || downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_ERROR.ordinal()) { //暂停下载
//                            mission.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_PAUSE);
//                            mission.setCancel(true);
//                        } else if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_WAIT.ordinal()) { //正在等待
//                            mission.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_WAIT);
//                        }
                        adapter.setUIStatus(mission, downloadItem);
                        isFind = true;
                        listDownload.add(mission);
//                        downloadHelper.startDownload(mission);
                        break;
                    }
                }
                if (!isFind) {
                    Misson misson = new Misson(downloadItem.getDownloadUrl(), downloadItem.getCurrentSize(), downloadItem.getMFileSize(), downloadItem.getSaveDir(), downloadItem.getSaveName());

                    int downloadStatusDao = downloadItem.getDownloadStatus();
                    LogUtils.e("---->状态" + downloadStatusDao);
//                    if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOADING.ordinal()) { //正在下载
//                        misson.setDownloadUiStatus(DownloadUiStatus.DOWNLOADING);
//                    } else if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_PAUSE.ordinal() || downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_ERROR.ordinal()) { //暂停下载
//                        misson.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_PAUSE);
//                        misson.setCancel(true);
//                    } else if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_WAIT.ordinal()) { //正在等待
//                        misson.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_WAIT);
//                    }
                    adapter.setUIStatus(misson, downloadItem);
                    listDownload.add(misson);
                }

            }
        }
        adapter.setData(listDownload);
        LogUtils.e("---->daoList" + downloadInfos.toString() + ",downloadList" + listDownload.toString());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        IntentFilter intentFilter = new IntentFilter(DownloadService.SEND_MISSION_ACTION);
        missionBradCast = new MissionBroadCast();
        registerReceiver(missionBradCast, intentFilter);
        listview_record = (ListView) findViewById(R.id.listview_record);
        downloadRecordAdapter = new DownloadRecordAdapter(DownloadRecordActivity.this);
        downloadRecordAdapter.setContainer(listview_record);
        listview_record.setAdapter(downloadRecordAdapter);
        getData(downloadRecordAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(missionBradCast);
    }

    private class MissionBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra(DownloadService.SEND_BROADCAST_MISSION_BUNDLE);
            Misson mission = bundle.getParcelable(DownloadService.SEND_BROADCAST_MISSION_KEY);
            downloadRecordAdapter.refreshData(mission);
        }
    }


}
