package com.xman.downloadmanagedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xman.downloadmanagedemo.dao.DownloadDaoUtils;
import com.xman.downloadmanagedemo.dao.DownloadInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] downloadUrl = {"http://gdown.baidu.com/data/wisegame/fb446df5fdbe3680/aiqiyishipin_80612.apk", "http://gdown.baidu.com/data/wisegame/34a6293862d4b5e5/qunaerlvxing_84.apk", "http://gdown.baidu.com/data/wisegame/a381fe31ef817fa5/kugouyinle_7161.apk","http://gdown.baidu.com/data/wisegame/344aaed52b9a278d/shoujitianmao_79.apk"};
    private List<Misson> list = new ArrayList<>();

    private String downloadDir = "download";
    private ListView listview;
    /*跳转下载列表*/
    TextView tv_skip_download_list;
    private DownloadListAdapter adapter;
    private MissionBroadCast missionBradCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Misson missonAiqiyi = new Misson(downloadUrl[0], 0, 0, downloadDir, "aiyiqi.apk");
        Misson missonQunawang = new Misson(downloadUrl[1], 0, 0, downloadDir, "qunawang.apk");
        Misson missonKuGou = new Misson(downloadUrl[2], 0, 0, downloadDir, "kugou.apk");
        Misson missonTianMao = new Misson(downloadUrl[3], 0, 0, downloadDir, "tianMAO.apk");
        //TODO 判断是否在下载列表 以及数据库中是否有纪录
        list.add(missonAiqiyi);
        list.add(missonQunawang);
        list.add(missonKuGou);
        list.add(missonTianMao);
        adapter = new DownloadListAdapter(this, list);
        listview.setAdapter(adapter);
        adapter.setContainer(listview);
        IntentFilter intentFilter = new IntentFilter(DownloadService.SEND_MISSION_ACTION);
        missionBradCast = new MainActivity.MissionBroadCast();
        registerReceiver(missionBradCast, intentFilter);
    }

    private void initView() {
        listview = (ListView) findViewById(R.id.listview);
        tv_skip_download_list = (TextView) findViewById(R.id.tv_skip_download_list);
        tv_skip_download_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DownloadRecordActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DownloadHelper.getInstance().stopAllDownload();
        unregisterReceiver(missionBradCast);
    }

    private class MissionBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.e("---->下载进度MainActivity");
            Bundle bundle = intent.getBundleExtra(DownloadService.SEND_BROADCAST_MISSION_BUNDLE);
            Misson mission = bundle.getParcelable(DownloadService.SEND_BROADCAST_MISSION_KEY);
            adapter.refreshData(mission);
        }
    }
}
