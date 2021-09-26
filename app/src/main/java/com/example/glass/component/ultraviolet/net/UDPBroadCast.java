package com.example.glass.component.ultraviolet.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPBroadCast {
    public final static int CAMERA_IP_PORT = 60000;
    public UDPBroadCast() {

    }

    /**
     * 发送广播
     */
    public static void queryIP(String msg)  {
        try {
            System.out.println("开始发送广播");
            //1.获取 datagramSocket 实例,不创建端口，客户端的端口由系统随机分配
            DatagramSocket socket = new DatagramSocket();
            //2.创建一个 udp 的数据包
            byte[] buf = msg.getBytes();

            DatagramPacket packet = new DatagramPacket(buf,
                    buf.length,
                    InetAddress.getByName("255.255.255.255"),
                    CAMERA_IP_PORT);
            //给服务端发送数据
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }




}

