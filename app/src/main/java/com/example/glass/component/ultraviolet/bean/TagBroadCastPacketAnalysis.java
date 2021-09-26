package com.example.glass.component.ultraviolet.bean;

public class TagBroadCastPacketAnalysis {

    public static TagBroadCastPacket analysis(byte[] bytes){
        TagBroadCastPacket packet = new TagBroadCastPacket();
        byte[] head = new byte[4];
        byte[] ip = new byte[4];
        byte[] mac = new byte[8];


        System.arraycopy(bytes,0,head,0,4);
        System.arraycopy(bytes,4,ip,0,4);
        System.arraycopy(bytes,8,mac,0,8);



        packet.head = head;
        packet.ip = ip;
        packet.mac = mac;

        return packet;
    }



}
