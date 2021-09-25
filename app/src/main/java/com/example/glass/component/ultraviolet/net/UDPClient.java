package com.example.glass.component.ultraviolet.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    public final static int CAMERA_IP_PORT = 60000;
    public final static int ANDROID_IP_PORT = 60080;
    public final static int CAMERA_PARAM_PORT = 60002;
    public final static int ANDROID_PARAM_PORT = 60082;

    private InetAddress mServerAddress; //服务端的IP
    private DatagramSocket mSocket;

    public UDPClient(String mServerIp) {
        try {
            mServerAddress = InetAddress.getByName(mServerIp);
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
                    byte[] buf = new byte[1024];
                    DatagramPacket serverMsgPacket = new DatagramPacket(buf, buf.length);
                    mSocket.receive(serverMsgPacket);
                    String serverMsg = new String(serverMsgPacket.getData(), 0, serverMsgPacket.getLength());
                    listener.onMsgReturned(serverMsg);//收到的服务端消息
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
