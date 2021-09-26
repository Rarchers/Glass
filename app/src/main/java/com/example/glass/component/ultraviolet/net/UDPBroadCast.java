package com.example.glass.component.ultraviolet.net;

import com.example.glass.component.ultraviolet.bean.TagBroadCastPacket;
import com.example.glass.utils.Java2StructUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
    public static void queryIP(TagBroadCastPacket tagBroadCastPacket,OnMsgReturnedListener onMsgReturnedListener)  {
        try {
            onMsgReturnedListener.onStateMsg("开始发送广播");
            System.out.println("开始发送广播");
            //1.获取 datagramSocket 实例,不创建端口，客户端的端口由系统随机分配
            DatagramSocket socket = new DatagramSocket();
            //2.创建一个 udp 的数据包
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.write(tagBroadCastPacket.getHead());
            out.write(tagBroadCastPacket.getIp());
            out.write(tagBroadCastPacket.getMac());
            out.write(tagBroadCastPacket.getId());
            out.write(tagBroadCastPacket.getIsModifyCameraIp());
            out.write(tagBroadCastPacket.getReserved1());
            out.write(tagBroadCastPacket.getModifyIp());
            out.write(tagBroadCastPacket.getModifyGateway());
            out.write(tagBroadCastPacket.getModifySubnet());
            out.write(Java2StructUtils.toLH(tagBroadCastPacket.getPcSearchIpPort())); // 大端转小端，int类型得转一下
            out.write(tagBroadCastPacket.getReserved());
            byte[] buf = baos.toByteArray();
            onMsgReturnedListener.onStateMsg("发送数据包内容：\n"+tagBroadCastPacket.toString());
            onMsgReturnedListener.onStateMsg("发送数据包大小："+buf.length);
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

