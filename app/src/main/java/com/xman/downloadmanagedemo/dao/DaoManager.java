package com.xman.downloadmanagedemo.dao;


import com.xman.downloadmanagedemo.Appctx;
import com.xman.downloadmanagedemo.daoutils.HMROpenHelper;
import com.xman.downloadmanagedemo.gen.DaoMaster;
import com.xman.downloadmanagedemo.gen.DaoSession;

/**
 * Created by nieyunlong on 17/6/5.
 */

public class DaoManager {
    private static DaoManager mInstance;
    private final HMROpenHelper devOpenHelper;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private DaoManager() {
        devOpenHelper = new HMROpenHelper(Appctx.getInstance(), "download-db", null);
        mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public static synchronized DaoManager getInstance() {
        if (mInstance == null) {
            mInstance = new DaoManager();
        }
        return mInstance;
    }
}
