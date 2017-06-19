package com.xman.downloadmanagedemo;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xman.downloadmanagedemo.widget.SaundProgressBar;

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
            convertView = mLayoutInflater.inflate(R.layout.item_download, null);
            viewHolder.item_download_name = (TextView) convertView.findViewById(R.id.item_download_name);
            viewHolder.item_download_status = (TextView) convertView.findViewById(R.id.item_download_status);
            viewHolder.item_download_regularprogressbar = (SaundProgressBar) convertView.findViewById(R.id.item_download_regularprogressbar);
            viewHolder.item_download_btn_start_download = (Button) convertView.findViewById(R.id.item_download_btn_start_download);
            viewHolder.item_download_btn_pause_download = (Button) convertView.findViewById(R.id.item_download_btn_pause_download);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.item_download_btn_start_download.setVisibility(View.GONE);

        viewHolder.item_download_name.setText(data.getSaveName());
        LogUtils.e("====>数据内容" + data.getDownloadUiStatus());
        viewHolder.item_download_status.setText(data.getDownloadUiStatus().getDownloadMsg());
        viewHolder.item_download_btn_pause_download.setText(data.getDownloadUiStatus().getDownloadMsg());
        final ViewHolder finalViewHolder = viewHolder;
        finalViewHolder.item_download_btn_pause_download.setVisibility(View.VISIBLE);

        viewHolder.item_download_regularprogressbar.setProgressIndicator(indicator);
        viewHolder.item_download_regularprogressbar.setProgress(data.getPercentage());
        viewHolder.item_download_regularprogressbar.setVisibility(View.VISIBLE);
        finalViewHolder.item_download_btn_pause_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("---->点击了暂停" + System.currentTimeMillis() + ",data.isCancel" + data.isCancel());
                finalViewHolder.item_download_btn_pause_download.setVisibility(View.VISIBLE);
                finalViewHolder.item_download_btn_start_download.setVisibility(View.GONE);
                if (data.isCancel()) {
                    finalViewHolder.item_download_btn_pause_download.setText("暂停下载");
                    DownloadHelper.getInstance().startDownload(data);
                } else {
                    //恢复下载
                    finalViewHolder.item_download_btn_pause_download.setText("恢复下载");
                    data.setCancel(true);
                    DownloadHelper.getInstance().pauseDownload(data);
                }
            }
        });
        return convertView;
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
        TextView item_download_name; //下载名字
        TextView item_download_status; //状态
        SaundProgressBar item_download_regularprogressbar; //下载名字
        Button item_download_btn_start_download; //开始下载
        Button item_download_btn_pause_download; //暂停下载
    }


    /**
     * 局部刷新
     *
     * @param viewHolder 找到的条目 viewHolder
     * @param itemIndex  当前数据所在的位置
     */
    public void setDataToView(ViewHolder viewHolder, int itemIndex) {
        Misson dataItem = getItem(itemIndex);
        viewHolder.item_download_regularprogressbar.setProgress(dataItem.getProgressCurrent());
        viewHolder.item_download_status.setText(dataItem.getDownloadUiStatus().getDownloadMsg());
    }
}
