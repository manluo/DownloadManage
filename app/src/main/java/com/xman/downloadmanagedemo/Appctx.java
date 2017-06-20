package com.xman.downloadmanagedemo;

import android.app.Application;

import com.xman.downloadmanagedemo.network.NetWorkReceiverUtils;
import com.xman.downloadmanagedemo.utils.CrashHandler;
import com.xman.downloadmanagedemo.utils.LogUtils;

/**
 * Created by nieyunlong on 17/6/12.
 */

public class Appctx extends Application {

    private static Appctx appctx;

    @Override
    public void onCreate() {
        super.onCreate();
        appctx = this;
        LogUtils.openLog();
        NetWorkReceiverUtils.getInstance().registerNetWorkReceiver(this);
        CrashHandler.getInstance().init(this);
    }

    public static Appctx getInstance() {
        if (appctx == null) {
            appctx = new Appctx();
        }
        return appctx;
    }

}
