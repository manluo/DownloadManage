package com.xman.downloadmanagedemo;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

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
        CrashHandler.getInstance().init(this);
    }

    public static Appctx getInstance() {
        if (appctx == null) {
            appctx = new Appctx();
        }
        return appctx;
    }

}
