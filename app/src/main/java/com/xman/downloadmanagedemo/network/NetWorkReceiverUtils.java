package com.xman.downloadmanagedemo.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;


/**
 * Created by NYL for RoundTable
 * 监听网络变化
 * Date: 2015/12/21
 */
public class NetWorkReceiverUtils {
    /**
     * 网络
     */
    private boolean isNetwork = false;
    private Context context;
    private static NetWorkReceiverUtils networkUtils;
    private MyNetWorkReceiver myNetWorkReceiver;

    private ObserverNetWork observerNetWork;

    public static NetWorkReceiverUtils getInstance() {
        if (networkUtils == null) {
            networkUtils = new NetWorkReceiverUtils();
        }
        return networkUtils;
    }

    public boolean getNetwork() {
        if (context != null) {
            isNetwork = NetWorkUtils.isNetWorkConnect(context);
        }
        return isNetwork;
    }

    public void addNetWorkObserver(ObserverNetWork observerNetWork) {
        this.observerNetWork = observerNetWork;
    }

    public void registerNetWorkReceiver(Context context) {
        Log.i("--->", "--------注册了网路广播");
        this.context = context;
        IntentFilter mFilter = new IntentFilter();
        myNetWorkReceiver = new MyNetWorkReceiver();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(myNetWorkReceiver, mFilter);
    }

    class MyNetWorkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetWorkUtils.NetWorkStatus net_type = NetWorkUtils.getNetWorkStatus(context);
                if (net_type != NetWorkUtils.NetWorkStatus.NETWORK_ERROR) { //有网络
                    if (observerNetWork != null) {
                        observerNetWork.onConnect(net_type);
                    }
                } else {

                    if (observerNetWork != null) {
                        observerNetWork.onDisconnect();
                    }

                }
            }
        }
    }


    /**
     * 注销网络广播
     */
    public void ungisterNetWork() {
        if (myNetWorkReceiver != null) {
            context.unregisterReceiver(myNetWorkReceiver);
        }
    }

    public interface ObserverNetWork {
        public void onDisconnect();

        public void onConnect(NetWorkUtils.NetWorkStatus type);
    }
}
