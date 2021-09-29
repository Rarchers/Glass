package com.example.glass.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.example.glass.component.ultraviolet.net.OnMsgReturnedListener;

import java.lang.ref.WeakReference;
import java.net.InetAddress;

public class ScanReachableNet extends Thread{
    private WeakReference<Context> mContextRef;
    private OnMsgReturnedListener listener;
    public ScanReachableNet(Context context, OnMsgReturnedListener listener){
        mContextRef = new WeakReference<>(context);
        this.listener = listener;
    }

    @Override
    public void run() {
        super.run();


        try {
            Context context = mContextRef.get();

            if (context != null) {

                ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                WifiManager wm = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo connectionInfo = wm.getConnectionInfo();
                int ipAddress = connectionInfo.getIpAddress();
                String ipString = Formatter.formatIpAddress(ipAddress);
                String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                //listener.onStateMsg("发现以下局域网内ip");
                for (int i = 0; i < 255; i++) {
                    listener.onStateMsg("第"+i+"个");
                    String testIp = prefix + String.valueOf(i);
                    InetAddress address = InetAddress.getByName(testIp);
                    boolean reachable = address.isReachable(1000);
                    String hostName = address.getCanonicalHostName();
                    if (reachable)
                        listener.onStateMsg("Host: " + String.valueOf(hostName) + "(" + String.valueOf(testIp) + ") is reachable!");
                }
                listener.onStateMsg("扫描完成");
                listener.onMsgReturned(true);
            }
        } catch (Throwable t) {
            listener.onError(new Exception("扫描网段ip失败"));
        }

    }
}
