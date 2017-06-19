package com.xman.downloadmanagedemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

    public DownloadService.DownloadServiceBind mBinder;

    public DownloadRecordAdapter downloadRecordAdapter;

    List<Misson> listDownload = new ArrayList<>();

    public boolean isConnected;

    private ServiceConnection connection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e("---->连接上服务了");
            mBinder = (DownloadService.DownloadServiceBind) service;
            isConnected = true;
            listview_record = (ListView) findViewById(R.id.listview_record);
            downloadRecordAdapter = new DownloadRecordAdapter(DownloadRecordActivity.this, downloadHelper);
            downloadRecordAdapter.getContainer(listview_record);
            getData(downloadRecordAdapter);
            listview_record.setAdapter(downloadRecordAdapter);
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnected = false;
        }
    };
    private MissionBroadCast missionBradCast;

    private void getData(DownloadRecordAdapter adapter) {
        ArrayList<Misson> listCacheMission = ThreadPoolManage.getInstance().getMissionCache();
        List<DownloadInfo> downloadInfos = DownloadDaoUtils.getDownloading();
        if (listCacheMission == null || listCacheMission.size() <= 0) {
            if (downloadInfos.size() > 0) {
                for (int i = 0; i < downloadInfos.size(); i++) {
                    DownloadInfo downloadItem = downloadInfos.get(i);
                    Misson misson = new Misson(downloadItem.getDownloadUrl(), downloadItem.getCurrentSize(), downloadItem.getMFileSize(), downloadItem.getSaveDir(), downloadItem.getSaveName());
                    misson.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_RESUME);
                    misson.setCancel(true);
//                    misson.registerMissonListener(downloadRecordAdapter);
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
                        mission.setDownloadUiStatus(DownloadUiStatus.DOWNLOADING);
//                        mission.registerMissonListener(adapter);
                        isFind = true;
                        listDownload.add(mission);
//                        downloadHelper.startDownload(mission);
                        break;
                    }
                }
                if (!isFind) {
                    Misson misson = new Misson(downloadItem.getDownloadUrl(), downloadItem.getCurrentSize(), downloadItem.getMFileSize(), downloadItem.getSaveDir(), downloadItem.getSaveName());
                    misson.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_RESUME);  //所有可下载的 都是暂停状态 在UI展示是恢复下载
                    misson.setCancel(true);
//                    misson.registerMissonListener(downloadRecordAdapter);
                    listDownload.add(misson);
                }

            }
        }
        adapter.setData(listDownload);
        LogUtils.e("---->daoList" + downloadInfos.toString() + ",downloadList" + listDownload.toString());
    }

    private DownloadHelper downloadHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        downloadHelper = new DownloadHelper(this);
        initDatas();
        IntentFilter intentFilter = new IntentFilter(DownloadService.SEND_MISSION_ACTION);
        missionBradCast = new MissionBroadCast();
        registerReceiver(missionBradCast, intentFilter);
    }

    private void initDatas() {
        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isConnected) {
            unbindService(connection);
        }
        if (downloadHelper != null) {
            downloadHelper.unbindDownloadService();
        }
        unregisterReceiver(missionBradCast);
    }

    private class MissionBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra(DownloadService.SEND_BROADCAST_MISSION_BUNDLE);
            Misson mission = bundle.getParcelable(DownloadService.SEND_BROADCAST_MISSION_KEY);
//            downloadRecordAdapter.updatePartUI(mission);
            Integer itemIndex = getMissionIndex(mission);
            LogUtils.e("---->onReceive--->itemIndex" + itemIndex + ",misson" + mission.toString());
            if (itemIndex != null) {
                updateView(itemIndex);
            }
        }
    }

    public Integer getMissionIndex(Misson misson) {
        if (misson == null || listDownload == null) {
            return null;
        }
        if (listDownload.size() <= 0) {
            return null;
        }
        int itemIndex = 0;
        for (int i = 0; i < listDownload.size(); i++) {
            String downloadUrl = listDownload.get(i).getmDownloadUrl();
            if (misson.getmDownloadUrl().equals(downloadUrl)) {
                itemIndex = i;
                break;
            }
        }
        return itemIndex;
    }

    private void updateView(int itemIndex) {
        //得到第一个可显示控件的位置，
        if (listview_record != null) {
            int firstVisiblePosition = listview_record.getFirstVisiblePosition(); //第一个条目可见的位置 例如下标是2
            int lastVisiblePosition = listview_record.getLastVisiblePosition();  //屏幕可见最后一个可见的位置 例如下标是13
            //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
            if (itemIndex - firstVisiblePosition >= 0 && itemIndex <= lastVisiblePosition) { //在可见的条目更新进度节省资源
                //得到要更新的item的view
                //TODO itemIndex - firstVisiblePosition 因为getChildCount 是13-2+1 这个是总个数  假如更新可见的条目是2 那么listview的child位置就是0
                View view = listview_record.getChildAt(itemIndex - firstVisiblePosition);
                LogUtils.e("---->downloadBaseAdapter" + view);
                //调用adapter更新界面
                downloadRecordAdapter.getUpdateViewAdapter(view, itemIndex);
            }
        }

    }

}
