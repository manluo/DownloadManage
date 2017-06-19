package com.xman.downloadmanagedemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by nieyunlong on 17/6/12.
 * 线程池 默认是3个线程
 */

public class ThreadPoolManage {

    private static ThreadPoolManage instance;
    /*key downloadUrl value Mission*/
    private final HashMap<String, Misson> mMissionBook;
    private static final int MAX_MISSION_COUNT = 1; //设置线程池最大线程个数
    private ExecutorService threadPool = null;
    /**
     * 缓存用于更新文件列表
     */
    private ArrayList<Misson> listMission;

    public ThreadPoolManage() {
        mMissionBook = new LinkedHashMap<>();
        threadPool = new ThreadPoolExecutor(MAX_MISSION_COUNT, MAX_MISSION_COUNT, 15, TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>());
    }

    public static ThreadPoolManage getInstance() {
        if (instance == null) {
            synchronized (ThreadPoolManage.class) {
                instance = new ThreadPoolManage();
            }
        }
        return instance;
    }

    public void addMisson(Misson mission) {
        mission.notifyAddMisson();
    }


    public void pauseMission(Misson misson) {
        if (mMissionBook.containsKey(misson.getmDownloadUrl())) {
            mMissionBook.get(misson.getmDownloadUrl()).cancel();
        }
    }

    public void resumeMission(Misson misson) {
        if (mMissionBook.containsKey(misson.getmDownloadUrl())) {
//            mMissionBook.get(misson.getmDownloadUrl()).resume();
            LogUtils.e("--->恢复执行" + misson.getmDownloadUrl());
            execute(misson);
        }
    }

    public void stopMission(Misson misson) {
        if (mMissionBook.containsKey(misson.getmDownloadUrl())) {
            mMissionBook.get(misson.getmDownloadUrl()).cancel();
            mMissionBook.remove(misson.getmDownloadUrl());
        }
    }

    public void removeMissionCache(Misson misson) {
        if (mMissionBook.size() > 0) {
            if (mMissionBook.containsKey(misson.getmDownloadUrl())) {
                LogUtils.e("---->删除缓存任务" + misson.getmDownloadUrl());
                mMissionBook.remove(misson.getmDownloadUrl());
            }
        }
    }

    /**
     * 获取缓存列表
     *
     * @return
     */
    public ArrayList<Misson> getMissionCache() {
        if (mMissionBook.size() > 0) {
            listMission = new ArrayList<>();
            for (Map.Entry<String, Misson> item : mMissionBook.entrySet()) {
                if (item.getValue() != null) {
                    listMission.add(item.getValue());
                }
            }
        }
        return listMission;

    }

    /**
     * 执行任务
     *
     * @param misson
     */
    public synchronized void execute(Misson misson) {
        if (threadPool == null || threadPool.isShutdown()) {
            threadPool = new ThreadPoolExecutor(MAX_MISSION_COUNT, MAX_MISSION_COUNT, 15, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<Runnable>());
        }
        if (misson.isCancel()) { //如果当前的任务已经被取消
            misson.setCancel(false);
        }
        mMissionBook.put(misson.getmDownloadUrl(), misson);
        threadPool.execute(misson);
    }

    /**
     * 取消所有的请求
     */
    public synchronized void workSurrender() {
        if (threadPool != null && mMissionBook.size() > 0) {
            for (Map.Entry<String, Misson> misson : mMissionBook.entrySet()) {
                misson.getValue().cancel();
            }
            if (mMissionBook.size() > 0) {
                mMissionBook.clear();
            }
        }
    }


    public synchronized void shutDownWork() {
        if (threadPool != null) {
            //阻止等待任务启动并试图停止当前正在执行的任务
            threadPool.shutdownNow();
        }
    }

}
