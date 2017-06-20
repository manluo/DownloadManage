package com.xman.downloadmanagedemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.xman.downloadmanagedemo.dao.DownloadInfo;
import com.xman.downloadmanagedemo.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyunlong
 * 基类adapter
 * on 2016/8/4.
 */
public abstract class DownloadUIBaseAdapter<T extends Misson> extends BaseAdapter {
    public Context context;
    public LayoutInflater inflater;
    /**
     * 下载列表
     */
    List<T> downloadingList;

    public DownloadUIBaseAdapter(Context context) {
        init(context);
    }

    public DownloadUIBaseAdapter(Context context, List<T> list) {
        this.downloadingList = list;
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return downloadingList != null && downloadingList.size() > 0 ? downloadingList.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return downloadingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItemView(position, convertView, parent);
    }

    public void setData(List<T> list) {
        this.downloadingList = list;
        notifyDataSetChanged();
    }

    /**
     * 没有数据 返回一个空集合
     *
     * @return
     */
    public List<T> getData() {
        if (downloadingList != null) {
            return downloadingList;
        }
        return new ArrayList<T>();
    }

    /**
     * 根据位置移除数据
     *
     * @param position 位置
     */
    public void removeDataByPos(int position) {
        if (downloadingList != null && downloadingList.size() > 0) {
            downloadingList.remove(position);
            notifyDataSetChanged();
        }
    }

    /**
     * 移除全部数据
     */
    public void removeDataAll() {
        if (downloadingList != null) {
            downloadingList.clear();
            notifyDataSetChanged();
        }
    }


    protected abstract View getItemView(int position, View convertView, ViewGroup parent);

    protected abstract ListView getViewContainer();

    protected abstract void refreshPartData(View refreshItemView, int refreshItemIndex);

    /**
     * 局部刷新
     *
     * @param view      当前需要更新的view
     * @param itemIndex 更新的view的下标 找到数据
     */
    public void getUpdateViewAdapter(View view, int itemIndex) {
        if (view == null) {
            return;
        }
        refreshPartData(view, itemIndex);

    }

    /**
     * 局部刷新入口
     *
     * @param misson
     */
    public void refreshData(Misson misson) {
        Integer itemIndex = getMissionIndex(misson);
        LogUtils.e("---->onReceive--->itemIndex" + itemIndex + ",misson" + misson.toString());
        if (itemIndex != null) {
            updateView(itemIndex);
        }
    }

    /**
     * 更新局部UI  更新频繁 导致按钮失效 滑动不顺畅
     * 由activity 调用
     *
     * @param misson
     * @return
     */
    private Integer getMissionIndex(Misson misson) {
        if (misson == null || downloadingList == null) {
            return null;
        }
        if (downloadingList.size() <= 0) {
            return null;
        }
        int itemIndex = 0;
        for (int i = 0; i < downloadingList.size(); i++) {
            T downloadItem = downloadingList.get(i);
            if (downloadItem != null) {
                String downloadUrl = downloadItem.getmDownloadUrl();
                if (misson.getmDownloadUrl().equals(downloadUrl)) {
                    itemIndex = i;
                    break;
                }
            } else {
                return null;
            }

        }
        return itemIndex;
    }

    /**
     * 找到数据元的对应的view
     *
     * @param itemIndex 需要更新数据的下标
     */
    private void updateView(int itemIndex) {
        //得到第一个可显示控件的位置，
        if (getViewContainer() != null) {
            int firstVisiblePosition = getViewContainer().getFirstVisiblePosition(); //第一个条目可见的位置 例如下标是2
            int lastVisiblePosition = getViewContainer().getLastVisiblePosition();  //屏幕可见最后一个可见的位置 例如下标是13
            //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
            if (itemIndex - firstVisiblePosition >= 0 && itemIndex <= lastVisiblePosition) { //在可见的条目更新进度节省资源
                //得到要更新的item的view
                //TODO itemIndex - firstVisiblePosition 因为getChildCount 是13-2+1 这个是总个数  假如更新可见的条目是2 那么listview的child位置就是0
                View view = getViewContainer().getChildAt(itemIndex - firstVisiblePosition);
                LogUtils.e("---->downloadBaseAdapter" + view);
                //调用adapter更新界面
                getUpdateViewAdapter(view, itemIndex);
            }
        }

    }

    /**
     * 数据库中的状态
     *
     * @param downloadInfo 数据库中的数据
     */
    public void setUIStatus(Misson mission, DownloadInfo downloadInfo) {
        int downloadStatusDao = downloadInfo.getDownloadStatus();
        if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOADING.ordinal()) { //正在下载
            mission.setDownloadUiStatus(DownloadUiStatus.DOWNLOADING);
            DownloadHelper.getInstance().startDownload(mission);
//            mission.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_PAUSE);
//            mission.setCancel(true);
        } else if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_PAUSE.ordinal() || downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_ERROR.ordinal()) { //暂停下载
            mission.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_PAUSE);
            mission.setCancel(true);
        } else if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_WAIT.ordinal()) { //正在等待
            mission.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_WAIT);
            DownloadHelper.getInstance().startDownload(mission);
        }
    }
    public void setUIStatusGetDao(Misson mission, DownloadInfo downloadInfo) {
        int downloadStatusDao = downloadInfo.getDownloadStatus();
        if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOADING.ordinal()) { //正在下载
//            mission.setDownloadUiStatus(DownloadUiStatus.DOWNLOADING);
//            DownloadHelper.getInstance().startDownload(mission);
            mission.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_PAUSE);
            mission.setCancel(true);
        } else if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_PAUSE.ordinal() || downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_ERROR.ordinal()) { //暂停下载
            mission.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_PAUSE);
            mission.setCancel(true);
        } else if (downloadStatusDao == DownloadInfo.DownloadStatus.DOWNLOAD_WAIT.ordinal()) { //正在等待
            mission.setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_PAUSE);
            mission.setCancel(true);
        }
    }

}
