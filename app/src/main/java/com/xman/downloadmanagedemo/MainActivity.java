package com.xman.downloadmanagedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String url = "http://gdown.baidu.com/data/wisegame/fb446df5fdbe3680/aiqiyishipin_80612.apk";
    private String[] downloadUrl = {"http://gdown.baidu.com/data/wisegame/fb446df5fdbe3680/aiqiyishipin_80612.apk", "http://gdown.baidu.com/data/wisegame/34a6293862d4b5e5/qunaerlvxing_84.apk", "http://gdown.baidu.com/data/wisegame/a381fe31ef817fa5/kugouyinle_7161.apk"};
    private List<Misson> list = new ArrayList<>();

    private String downloadDir = "download";
    private ListView listview;
    /*跳转下载列表*/
    TextView tv_skip_download_list;
    private DownloadHelper downloadHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        Misson missonAiqiyi = new Misson(downloadUrl[0], 0, 0, downloadDir, "aiyiqi.apk");
        Misson missonQunawang = new Misson(downloadUrl[1], 0, 0, downloadDir, "qunawang.apk");
        Misson missonKuGou = new Misson(downloadUrl[2], 0, 0, downloadDir, "kugou.apk");
        //TODO 判断是否在下载列表 以及数据库中是否有纪录
        list.add(missonAiqiyi);
        list.add(missonQunawang);
        list.add(missonKuGou);
        downloadHelper = new DownloadHelper(this);
        DownloadListAdapter adapter = new DownloadListAdapter(this, list, downloadHelper);
        listview.setAdapter(adapter);
        adapter.getContainer(listview);
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
        downloadHelper.unbindDownloadService();
    }
}
