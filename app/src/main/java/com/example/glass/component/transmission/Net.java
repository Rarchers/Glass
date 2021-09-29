package com.example.glass.component.transmission;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public  class Net {

    private  Socket socket = null;


    public  Net(){
        try{
            System.out.println("开始连结服务器");
            this.socket = new Socket("",8000);
            System.out.println("连接服务器成功");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("连接服务器失败");
        }

    }

    public  void sendMessage(String message){
        try{
            new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true).println(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取NetworkInfo对象
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        // 遍历每一个对象
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                // debug信息
                System.out.println("TypeName = " + networkInfo.getTypeName());
                // 网络状态可用
                return true;
            }
        }
        // 没有可用的网络
        return false;
    }

    public static boolean getNetWork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        if (networkInfo != null && networkInfo.length > 0) {
            for (int i = 0; i < networkInfo.length; i++) {
                PrintStream printStream = System.out;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(i);
                stringBuilder.append("===状态===");
                stringBuilder.append(networkInfo[i].getState());
                printStream.println(stringBuilder.toString());
                printStream = System.out;
                stringBuilder = new StringBuilder();
                stringBuilder.append(i);
                stringBuilder.append("===类型===");
                stringBuilder.append(networkInfo[i].getTypeName());
                printStream.println(stringBuilder.toString());
                if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }







}
