package com.example.glass.component.ultraviolet.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

public class TCPClient {
    public final static int CAMERA_DATA_PORT = 60033;

    private Socket socket;
    private final OnMsgReturnedListener listener;
    private volatile boolean shutdown = false;

    public TCPClient(OnMsgReturnedListener listener) {
        this.listener = listener;
    }


    public void startTcpReceive(){
        new ReceiveThread(listener).start();

    }


    private class ReceiveThread extends Thread {

        private OnMsgReturnedListener listener;

        public ReceiveThread(OnMsgReturnedListener listener) {
            this.listener = listener;


        }

        @Override
        public void run() {
            super.run();

            try {
                socket = new Socket("192.168.1.195", CAMERA_DATA_PORT);
                socket.sendUrgentData(0xFF);
                listener.onStateMsg("tcp Socket初始化完成"+socket.isConnected());
            } catch (Exception e) {
                listener.onError(e);
            }


            try {
                listener.onStateMsg("监听TCP中...端口："+CAMERA_DATA_PORT);
                listener.onStateMsg("tcp状态 "+socket.isConnected());
                while (!shutdown) {
                    final byte[] buffer = new byte[1024*512];//创建接收缓冲区
                    InputStream inputStream = socket.getInputStream();
                    final int len = inputStream.read(buffer);//数据读出来，并且返回数据的长度
                    listener.onStateMsg("数据长度： " + len);

                }
            } catch (Exception e) {
                listener.onError(e);
            }
        }
    }


    public void closeAll() {
        shutdown = true;
    }


}
