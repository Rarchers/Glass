package com.glass.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;


import com.glass.component.Config;
import com.glass.component.ultraviolet.net.OnMsgReturnedListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
                listener.onStateMsg("发现以下局域网内ip");

                listener.onStateMsg("当前扫描："+ Config.DEVICE_IP);
                String testIp = prefix + String.valueOf(195);
                InetAddress address = InetAddress.getByName(testIp);
                boolean reachable = address.isReachable(1000);
                String hostName = address.getCanonicalHostName();
                if (reachable)
                    listener.onStateMsg("Host: " + String.valueOf(hostName) + "(" + String.valueOf(testIp) + ") is reachable!");
                else
                    listener.onStateMsg("Host: " + String.valueOf(hostName) + "(" + String.valueOf(testIp) + ") is not reachable!");

                listener.onStateMsg("开始执行ping命令确认是否ping通：");
                Process p = Runtime.getRuntime().exec(
                        "ping -c 5 -w 5 "+Config.DEVICE_IP);//
                p.waitFor();
                int status = p.exitValue();
                InputStreamReader reader = new InputStreamReader(p.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = "";
                StringBuilder echo = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null){
                    echo.append(line).append("\n");
                }

                listener.onStateMsg("ping 结果：");
                listener.onStateMsg(echo.toString());
                if (status == 0){


                    listener.onStateMsg("ping通过");
                }else {



                    listener.onStateMsg("ping失败");
                }






                listener.onMsgReturned(true);
            }
        } catch (Throwable t) {
            listener.onError(new Exception("扫描网段ip失败"));
        }

    }
}
