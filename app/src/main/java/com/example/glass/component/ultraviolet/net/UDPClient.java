package com.example.glass.component.ultraviolet.net;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import com.example.glass.utils.IpAddress;
import com.example.glass.utils.ScanReachableNet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class UDPClient {

    public final static int CAMERA_PARAM_PORT = 60002;
    public final static int ANDROID_PARAM_PORT = 60082;
    private InetAddress mServerAddress; //服务端的IP
    private String BROADCAST_IP = "192.168.1.195";
    private InetAddress inetAddress = null;
    private DatagramSocket sendSocket = null;
    private DatagramSocket receiveSocket = null;
    private OnMsgReturnedListener listener;
    private DatagramPacket dpReceive = null;
    private boolean scanFinish = false;
    private volatile boolean shutdown = false;
    private SendThread sendThread;
    private ReceiveThread receiveThread;

    public UDPClient(OnMsgReturnedListener listener) {
        try {
            this.listener = listener;
            mServerAddress = InetAddress.getByName(BROADCAST_IP);
            String ipv4 = IpAddress.getIPv4Address();
            listener.onStateMsg("当前客户端ip地址："+ipv4);
            listener.onStateMsg("目标服务端ip地址："+BROADCAST_IP);
 //           listener.onStateMsg("即将开始扫描局域网内存活ip...");
//            new ScanReachableNet(context, new OnMsgReturnedListener() {
//                @Override
//                public void onMsgReturned(Object msg) {
//                    scanFinish = (boolean)msg;
//                    listener.onStateMsg("扫描完成！");
//
//                }
//
//                @Override
//                public void onError(Exception ex) {
//                    listener.onError(ex);
//                }
//
//                @Override
//                public void onStateMsg(String state) {
//                    listener.onStateMsg(state);
//                }
//            }).start();
            try{
                listener.onStateMsg("开启接受线程...");
                receiveSocket = new DatagramSocket(ANDROID_PARAM_PORT);
                receiveThread = new ReceiveThread(listener);
                receiveThread.start();
            }catch (Exception e){
                listener.onError(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(String msg){
        listener.onStateMsg("开启发送线程...");
        sendThread = new SendThread(listener,msg);
        sendThread.start();
    }



    private class SendThread extends Thread{
        private OnMsgReturnedListener listener;
        private String sendMsg;
        public SendThread(OnMsgReturnedListener listener,String msg){
            this.listener = listener;
            this.sendMsg = msg;
        }
        @Override
        public void run() {
            super.run();
            try{
            DatagramPacket dpSend = null;
            byte[] data = sendMsg.getBytes();
            dpSend = new DatagramPacket(data,0,data.length,mServerAddress,CAMERA_PARAM_PORT);
                sendSocket = new DatagramSocket();



                sendSocket.send(dpSend);
                listener.onStateMsg("发送数据(utf-8)："+sendMsg);
                listener.onStateMsg("发送数据(byte)："+ Arrays.toString(data));
                sendSocket.close();
            }catch (Exception e){
                listener.onError(e);
            }

        }
    }


    private class ReceiveThread extends Thread{
        private OnMsgReturnedListener listener;
        public ReceiveThread(OnMsgReturnedListener listener){
            this.listener = listener;
        }



        @Override
        public void run() {
            super.run();
            while (!shutdown){
                String receiveContent = null;
                byte[] buf = new byte[1024];
                dpReceive = new DatagramPacket(buf, buf.length);
                try{
                    receiveSocket.receive(dpReceive);
                    receiveContent = new String(buf, 0, dpReceive.getLength());
                    Log.i(TAG, "run: receive message " + receiveContent);
                    Log.i(TAG, "run: " + dpReceive.getAddress().toString());
                    listener.onMsgReturned("从"+dpReceive.getAddress().toString()+"接收到的数据："+receiveContent);
                }catch (Exception e){
                    listener.onError(e);
                }

            }


        }
    }


    public void closeAll(){
        if (receiveSocket !=null ){
            shutdown = true;
            receiveSocket.close();
            if (receiveThread != null)
                receiveThread.interrupt();
        }

        if (sendSocket != null){
            sendSocket.close();
            if (sendThread != null)
                sendThread.interrupt();
        }


    }






}
