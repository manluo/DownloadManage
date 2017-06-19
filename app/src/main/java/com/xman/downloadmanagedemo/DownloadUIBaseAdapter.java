package com.xman.downloadmanagedemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyunlong
 * 基类adapter
 * on 2016/8/4.
 */
public abstract class DownloadUIBaseAdapter<T> extends BaseAdapter {
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


    public abstract View getItemView(int position, View convertView, ViewGroup parent);

}
