package com.xman.downloadmanagedemo.daoutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.xman.downloadmanagedemo.LogUtils;
import com.xman.downloadmanagedemo.gen.DaoMaster;
import com.xman.downloadmanagedemo.gen.DownloadInfoDao;

import org.greenrobot.greendao.database.Database;

/**
 * 类名：HMROpenHelper
 * 类描述：用于数据库升级的工具类
 * 创建人：孙广竹
 * 创建日期： 2016/10/20.
 * 版本：V1.0
 */

public class HMROpenHelper extends DaoMaster.OpenHelper {

    public HMROpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * 数据库升级
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        LogUtils.e("--->升级" + oldVersion + ",new" + newVersion);
        //操作数据库的更新
        MigrationHelper.migrate(db, DownloadInfoDao.class);
    }

}
