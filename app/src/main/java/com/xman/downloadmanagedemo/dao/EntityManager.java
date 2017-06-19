package com.xman.downloadmanagedemo.dao;


import com.xman.downloadmanagedemo.gen.DownloadInfoDao;

/**
 * Created by nieyunlong on 17/6/5.
 */


public class EntityManager {
    private static EntityManager entityManager;
    public DownloadInfoDao downloadDao;

    /**
     * 获取下载dao
     *
     * @return
     */
    public DownloadInfoDao getDownloadDao() {
        downloadDao = DaoManager.getInstance().getSession().getDownloadInfoDao();
        return downloadDao;
    }

    /**
     * 创建单例
     *
     * @return
     */
    public synchronized static EntityManager getInstance() {
        if (entityManager == null) {
            entityManager = new EntityManager();
        }
        return entityManager;
    }
}
