package com.xman.downloadmanagedemo;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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

public class DownloadListAdapter extends DownloadUIBaseAdapter<Misson> {


    private final Drawable indicator;

    private ListView listView;

    public DownloadListAdapter(Context context, List<Misson> list) {
        super(context, list);
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
                DownloadHelper.getInstance().startDownload(data);
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

    public void setContainer(ListView listView) {
        this.listView = listView;
    }

    @Override
    protected ListView getViewContainer() {
        return listView;
    }

    @Override
    protected void refreshPartData(View refreshItemView, int refreshItemIndex) {
        updateViewAdapter(refreshItemView, refreshItemIndex);
    }


    static class ViewHolder {
        TextView item_download_name; //下载名字
        SaundProgressBar item_download_regularprogressbar; //下载名字
        Button item_download_btn_start_download; //开始下载
        Button item_download_btn_pause_download; //暂停下载
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
        setDataToUi(viewHolder, itemIndex);
    }

    private void setDataToUi(ViewHolder viewHolder, int itemIndex) {
        Misson dataItem = getItem(itemIndex);
        viewHolder.item_download_regularprogressbar.setProgress(dataItem.getProgressCurrent());
    }
}
