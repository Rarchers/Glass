package com.example.glass.component.ultraviolet.net;

import android.provider.SyncStateContract;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDP {

    private static final String TAG = "UDP";
    private DatagramSocket client;
    private DatagramPacket receivePacket;

    private static final int POOL_SIZE = 5;
    private static final int BUFFER_LENGTH = 1024;
    private byte[] receiveByte = new byte[BUFFER_LENGTH];

    private boolean isThreadRunning = false;

    private ExecutorService mThreadPool;
    private Thread clientThread;
    private OnMsgReturnedListener listener;

    public UDP(OnMsgReturnedListener listener) {
        this.listener = listener;
        int cpuNumbers = Runtime.getRuntime().availableProcessors();
//        根据CPU数目初始化线程池
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * POOL_SIZE);

    }

    public void startUDPSocket() {
        if (client != null) return;
        try {
//            表明这个 Socket 在设置的端口上监听数据。
            client = new DatagramSocket(60082);

            if (receivePacket == null) {
                receivePacket = new DatagramPacket(receiveByte, BUFFER_LENGTH);
            }
            startSocketThread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    /**
     * 开启发送数据的线程
     **/
    private void startSocketThread() {
        Thread clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "clientThread is running...");
                listener.onStateMsg("clientThread is running...");
                receiveMessage();
            }
        });
        isThreadRunning = true;
        clientThread.start();
    }
    /**
     * 处理接受到的消息
     **/
    private void receiveMessage() {
        listener.onStateMsg("开始处理接收信息");
        while (isThreadRunning) {
            listener.onStateMsg("等待数据");
            if (client != null) {
                try {
                    client.receive(receivePacket);
                } catch (IOException e) {
                    Log.e(TAG, "UDP数据包接收失败！线程停止");
                    listener.onError(new Exception("UDP数据包接收失败！线程停止"));
                    e.printStackTrace();
                    return;
                }
            }

            if (receivePacket == null || receivePacket.getLength() == 0) {
                Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");

                listener.onError(new Exception("无法接收UDP数据或者接收到的UDP数据为空"));
                continue;
            }

            String strReceive = new String(receivePacket.getData(), 0, receivePacket.getLength());
            Log.d(TAG, strReceive + " from " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());
            listener.onMsgReturned(strReceive + " from " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());
//            每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (receivePacket != null) {
                receivePacket.setLength(BUFFER_LENGTH);
            }
        }
    }
    /**
     * 发送信息
     **/
    public void sendMessage(final String message) {
        if (client == null) {
            startUDPSocket();
        }
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress targetAddress = InetAddress.getByName("192.168.1.195");
                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), targetAddress,60002);
                    client.send(packet);
                    listener.onStateMsg("发送完毕， ip ： 192.168.1.195 端口 60002 信息："+message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
