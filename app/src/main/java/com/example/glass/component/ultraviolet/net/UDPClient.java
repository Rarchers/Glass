package com.example.glass.component.ultraviolet.net;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

    public final static int CAMERA_PARAM_PORT = 60002;


    private InetAddress mServerAddress; //服务端的IP
    private DatagramSocket mSocket;


    public UDPClient() {
        try {
            mServerAddress = InetAddress.getByName("192.168.1.195");
            mSocket = new DatagramSocket();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //发送数据给服务端的方法 msg:发送的数据  OnMsgReturnedListener 接口是为了拿到收到的信息
    public void requireParam(String msg, OnMsgReturnedListener listener) {
        //因为是耗时操作，所以这边开了个子线程
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //下面一大堆和服务端的代码基本上一样，无非就是一个是从控制台输的数据，一个从页面输的
                    byte[] clientMsgBytes = msg.getBytes();

                    DatagramPacket clientPacket = new DatagramPacket(clientMsgBytes,
                            clientMsgBytes.length,
                            mServerAddress,
                            CAMERA_PARAM_PORT);
                    mSocket.send(clientPacket);
                    listener.onStateMsg("发送到：192.168.1.195\n"+"发送到端口："+CAMERA_PARAM_PORT+"\n发送数据："+msg);

                } catch (Exception e) {
                    listener.onError(e);
                }
            }
        }.start();
    }








    public void onDestroy(){
        if (mSocket != null) {
            mSocket.close();
        }
    }
}
