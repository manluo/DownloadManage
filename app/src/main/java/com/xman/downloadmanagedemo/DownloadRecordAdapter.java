package com.xman.downloadmanagedemo;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by nieyunlong on 17/6/13.
 * 从数据库里面拿数据
 */

public class DownloadRecordAdapter extends DownloadUIBaseAdapter<Misson> {


    private final LayoutInflater mLayoutInflater;
    private Drawable indicator;

    private ListView listView;

    public DownloadRecordAdapter(Context context) {
        super(context);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        indicator = context.getResources().getDrawable(
                R.drawable.progress_indicator);
        Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth() + 5,
                indicator.getIntrinsicHeight());
        indicator.setBounds(bounds);
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        final Misson data = getItem(position);
        LogUtils.e("---->刷新数据" + data);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_download_record, null);
            viewHolder.status_icon = (DownloadPercentView) convertView.findViewById(R.id.status_icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.download_percent = (TextView) convertView.findViewById(R.id.download_percent);
            viewHolder.progressbar = (ProgressBar) convertView.findViewById(R.id.progressbar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        LogUtils.e("====>数据内容" + data.getDownloadUiStatus());
        setDatas(viewHolder, data);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("===>暂停" + data.isCancel());
                if (data.isCancel()) { //是暂停
                    DownloadHelper.getInstance().startDownload(data);
                } else {
                    DownloadHelper.getInstance().pauseDownload(data);
                }
            }
        });
        return convertView;
    }

    private void setDatas(ViewHolder viewHolder, Misson misson) {
        viewHolder.name.setText(misson.getSaveName());
        viewHolder.progressbar.setProgress(misson.getPercentage());
        viewHolder.download_percent.setText("已下载" + misson.getPercentage() + "%");
        setIconByStatus(viewHolder.status_icon, misson.getDownloadUiStatus());
        viewHolder.status_icon.setProgress(misson.getPercentage());
    }

    private void setIconByStatus(DownloadPercentView downloadPercentView, DownloadUiStatus status) {
        downloadPercentView.setVisibility(View.VISIBLE);

        if (status == DownloadUiStatus.DOWNLOAD_READ) {
            downloadPercentView.setStatus(DownloadUiStatus.DOWNLOAD_READ.ordinal());
        }
        if (status == DownloadUiStatus.DOWNLOADING) {
            downloadPercentView.setStatus(DownloadUiStatus.DOWNLOADING.ordinal());
        }
        if (status == DownloadUiStatus.DOWNLOAD_WAIT) {
            downloadPercentView.setStatus(DownloadUiStatus.DOWNLOAD_WAIT.ordinal());
        }
        if (status == DownloadUiStatus.DOWNLOAD_PAUSE) {
            downloadPercentView.setStatus(DownloadUiStatus.DOWNLOAD_PAUSE.ordinal());
        }
        if (status == DownloadUiStatus.DOWNLOAD_SUCCESS) {
            downloadPercentView.setStatus(DownloadUiStatus.DOWNLOAD_SUCCESS.ordinal());
        }
        if (status == DownloadUiStatus.DOWNLOAD_RESUME) {
            downloadPercentView.setStatus(DownloadUiStatus.DOWNLOAD_RESUME.ordinal());
        }
        if(status==DownloadUiStatus.DOWNLOAD_FAILED){ //下载失败对应的暂停
            downloadPercentView.setStatus(DownloadUiStatus.DOWNLOAD_PAUSE.ordinal());
        }
    }

    @Override
    protected ListView getViewContainer() {
        return listView;
    }

    public void setContainer(ListView listView) {
        this.listView = listView;
    }


    @Override
    protected void refreshPartData(View refreshItemView, int refreshItemIndex) {
        //从view中取得holder
        DownloadRecordAdapter.ViewHolder viewHolder = (DownloadRecordAdapter.ViewHolder) refreshItemView.getTag();
        setDataToView(viewHolder, refreshItemIndex);
    }


    static class ViewHolder {
        DownloadPercentView status_icon; //包含了下载暂停
        TextView name; //下载的名称
        TextView download_percent; //下载进度
        ProgressBar progressbar; //下载的进度
    }


    /**
     * 局部刷新
     *
     * @param viewHolder 找到的条目 viewHolder
     * @param itemIndex  当前数据所在的位置
     */
    public void setDataToView(ViewHolder viewHolder, int itemIndex) {
        Misson dataItem = getItem(itemIndex);
        setDatas(viewHolder, dataItem);
    }
}
