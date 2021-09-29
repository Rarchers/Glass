package com.example.glass.component.ultraviolet.net;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPClientResponse extends Thread{
    private DatagramSocket receiveSocket;
    public final static int ANDROID_PARAM_PORT = 60082;
    private OnMsgReturnedListener listener;


    public UDPClientResponse(OnMsgReturnedListener listener){
        this.listener = listener;
    }
    @Override
    public void run() {
        super.run();
        try {
            receiveSocket = new DatagramSocket(ANDROID_PARAM_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                listener.onStateMsg("开始监听 端口："+ANDROID_PARAM_PORT);

                byte[] data = new byte[1024];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                receiveSocket.receive(packet);
                listener.onStateMsg("收到了数据");
                System.out.println(packet.getAddress());   // /ip
                System.out.println(packet.getPort());  //端口
                System.out.println(packet.getSocketAddress());  //  /ip:端口
                String message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                listener.onMsgReturned(message);//收到的服务端消息
//                System.out.println("receive client's data: " + message);
            } catch (Exception e) {
                e.printStackTrace();
                listener.onError(e);
            }
        }

    }
}
