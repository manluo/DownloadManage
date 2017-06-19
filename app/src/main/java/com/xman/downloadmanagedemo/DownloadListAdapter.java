package com.xman.downloadmanagedemo;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xman.downloadmanagedemo.widget.SaundProgressBar;

import java.util.List;

/**
 * Created by nieyunlong on 17/6/13.
 */

public class DownloadListAdapter extends DownloadUIBaseAdapter<Misson> implements Misson.MissonListener<Misson> {


    private final Drawable indicator;
    private final DownloadHelper downloadHelper;


    private ListView listView;
    /**
     * 局部刷新
     */
    private static final int WHAT_NOTIFY_PART_UI = 2;
    /**
     * 全部刷新
     */
    private static final int WHAT_NOTIFY_ALL_UI = 1;

    public DownloadListAdapter(Context context, List<Misson> list, DownloadHelper downloadHelper) {
        super(context, list);
        this.downloadHelper = downloadHelper;
        indicator = context.getResources().getDrawable(
                R.drawable.progress_indicator);
        Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth() + 5,
                indicator.getIntrinsicHeight());
        indicator.setBounds(bounds);

    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        final Misson data = getItem(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_download, null);
            viewHolder.item_download_name = (TextView) convertView.findViewById(R.id.item_download_name);
            viewHolder.item_download_regularprogressbar = (SaundProgressBar) convertView.findViewById(R.id.item_download_regularprogressbar);
            viewHolder.item_download_btn_start_download = (Button) convertView.findViewById(R.id.item_download_btn_start_download);
            viewHolder.item_download_btn_pause_download = (Button) convertView.findViewById(R.id.item_download_btn_pause_download);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        convertView.setId(position);
        viewHolder.item_download_name.setText(data.getSaveName());
        final ViewHolder finalViewHolder = viewHolder;

        viewHolder.item_download_regularprogressbar.setProgressIndicator(indicator);
        viewHolder.item_download_regularprogressbar.setProgress(0);
        viewHolder.item_download_regularprogressbar.setVisibility(View.VISIBLE);
        viewHolder.item_download_btn_start_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finalViewHolder.item_download_btn_pause_download.setVisibility(View.VISIBLE);
                finalViewHolder.item_download_btn_start_download.setVisibility(View.GONE);
                data.registerMissonListener(DownloadListAdapter.this);
                downloadHelper.startDownload(data);
            }
        });
//        finalViewHolder.item_download_btn_pause_download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogUtils.e("---->点击了暂停" + System.currentTimeMillis());
//                finalViewHolder.item_download_btn_pause_download.setVisibility(View.VISIBLE);
//                finalViewHolder.item_download_btn_start_download.setVisibility(View.GONE);
//                if (data.isCancel()) {
//                    finalViewHolder.item_download_btn_pause_download.setText("暂停下载");
//                    ThreadPoolManage.getInstance().resumeMission(data);
//                } else {
//                    //恢复下载
//                    finalViewHolder.item_download_btn_pause_download.setText("恢复下载");
//                    ThreadPoolManage.getInstance().pauseMission(data);
//                }
//            }
//        });

        viewHolder.item_download_regularprogressbar.setProgress(data.getProgressCurrent());
        return convertView;
    }

    @Override
    public void addMisson(Misson misson) {

    }

    @Override
    public void onStart(Misson misson) {

    }

    @Override
    public void getProgress(final Misson misson) {
        LogUtils.e("---->progress" + misson.getProgressCurrent() + ",statusUI" + misson.getDownloadUiStatus().ordinal());
        updatePartUI(misson);
    }

    @Override
    public void onSuccess(Misson misson) {
        updateAllUI();
        LogUtils.e("---->onSuccess" + misson.getProgressCurrent() + ",statusUI" + misson.getDownloadUiStatus().ordinal());
    }

    @Override
    public void onFailed(Misson misson) {
        LogUtils.e("---->onFailed" + misson.getProgressCurrent() + ",statusUI" + misson.getDownloadUiStatus().ordinal());

    }

    @Override
    public void onPause(Misson misson) {
        LogUtils.e("---->onPause" + misson.getProgressCurrent() + ",UIStatus" + misson.getDownloadUiStatus());
    }

    @Override
    public void onCancel(Misson misson) {
        LogUtils.e("---->onCancel" + misson.getProgressCurrent() + ",UIStatus" + misson.getDownloadUiStatus());

    }

    public void updateAllUI() {
        LogUtils.e("---->更新UI");
        Message message = Message.obtain();
        message.what = WHAT_NOTIFY_ALL_UI;
        windTalker.sendMessage(message);
    }

    /**
     * 更新局部UI 不使用updateAllUi 原因 更新频繁 导致按钮失效 滑动不顺畅
     */
    public void updatePartUI(final Misson misson) {
        if (misson == null) {
            return;
        }
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putParcelable("mission", misson);
        message.setData(bundle);
        message.what = WHAT_NOTIFY_PART_UI;
        LogUtils.e("---->更新UI");
        windTalker.sendMessage(message);
    }


    public void getContainer(ListView listView) {
        this.listView = listView;
    }

    private Handler windTalker = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_NOTIFY_ALL_UI) {
                notifyDataSetChanged();
            } else if (msg.what == WHAT_NOTIFY_PART_UI) { //局部刷新
                Bundle bundle = msg.getData();
                Misson misson = bundle.getParcelable("mission");
                if (misson == null) {
                    return;
                }
                int itemIndex = 0;
                for (int i = 0; i < downloadingList.size(); i++) {
                    String downloadUrl = downloadingList.get(i).getmDownloadUrl();
                    if (misson.getmDownloadUrl().equals(downloadUrl)) {
                        //TODO 可以把元数据进行改变 进行局部刷新 防止拿到的数据不是元数据 刷新不生效
                        itemIndex = i;
                        break;
                    }
                }
                LogUtils.e("---->通知局部刷新" + misson.getProgressCurrent() + ",位置" + itemIndex);
                updateView(itemIndex);

            }
        }
    };

    static class ViewHolder {
        TextView item_download_name; //下载名字
        SaundProgressBar item_download_regularprogressbar; //下载名字
        Button item_download_btn_start_download; //开始下载
        Button item_download_btn_pause_download; //暂停下载
    }

    private void updateView(int itemIndex) {
        //得到第一个可显示控件的位置，
        if (listView != null) {
            int firstVisiblePosition = listView.getFirstVisiblePosition(); //第一个条目可见的位置 例如下标是2
            int lastVisiblePosition = listView.getLastVisiblePosition();  //屏幕可见最后一个可见的位置 例如下标是13
            //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
            if (itemIndex - firstVisiblePosition >= 0 && itemIndex <= lastVisiblePosition) { //在可见的条目更新进度节省资源
                //得到要更新的item的view
                //TODO itemIndex - firstVisiblePosition 因为getChildCount 是13-2+1 这个是总个数  假如更新可见的条目是2 那么listview的child位置就是0
                View view = listView.getChildAt(itemIndex - firstVisiblePosition);
                //调用adapter更新界面
                updateViewAdapter(view, itemIndex);
            }
        }

    }

    /**
     * 局部刷新
     *
     * @param view      找到的条目 view
     * @param itemIndex 当前数据所在的位置
     */
    public void updateViewAdapter(View view, int itemIndex) {
        if (view == null) {
            return;
        }
        //从view中取得holder
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.item_download_name = (TextView) view.findViewById(R.id.item_download_name);
        viewHolder.item_download_regularprogressbar = (SaundProgressBar) view.findViewById(R.id.item_download_regularprogressbar);
        viewHolder.item_download_btn_start_download = (Button) view.findViewById(R.id.item_download_btn_start_download);
        viewHolder.item_download_btn_pause_download = (Button) view.findViewById(R.id.item_download_btn_pause_download);
        setDataToUi(viewHolder, itemIndex);
    }

    private void setDataToUi(ViewHolder viewHolder, int itemIndex) {
        Misson dataItem = getItem(itemIndex);
        viewHolder.item_download_regularprogressbar.setProgress(dataItem.getProgressCurrent());
    }
}
