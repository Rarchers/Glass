package com.glass.component.ultraviolet.bean;

import java.util.Arrays;

public class TagBroadCastPacket {
    byte[] head;
    byte[] ip;
    byte[] mac;
    byte[] id;
    byte[] isModifyCameraIp;
    byte[] reserved1;
    byte[] modifyIp;
    byte[] modifyGateway;
    byte[] modifySubnet;
    int pcSearchIpPort;
    byte[] reserved;


    public TagBroadCastPacket() {
        head = new byte[4];
        ip = new byte[4];
        mac = new byte[8];
        id = new byte[8];
        isModifyCameraIp = new byte[1];
        reserved1 = new byte[3];
        modifyIp = new byte[4];
        modifyGateway = new byte[4];
        modifySubnet = new byte[4];
        pcSearchIpPort = 60080;
        reserved = new byte[104];
    }

    public byte[] getHead() {
        return head;
    }

    public void setHead(byte[] head) {
        this.head = head;
    }

    public byte[] getIp() {
        return ip;
    }

    public void setIp(byte[] ip) {
        this.ip = ip;
    }

    public byte[] getMac() {
        return mac;
    }

    public void setMac(byte[] mac) {
        this.mac = mac;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getIsModifyCameraIp() {
        return isModifyCameraIp;
    }

    public void setIsModifyCameraIp(byte[] isModifyCameraIp) {
        this.isModifyCameraIp = isModifyCameraIp;
    }

    public byte[] getReserved1() {
        return reserved1;
    }

    public void setReserved1(byte[] reserved1) {
        this.reserved1 = reserved1;
    }

    public byte[] getModifyIp() {
        return modifyIp;
    }

    public void setModifyIp(byte[] modifyIp) {
        this.modifyIp = modifyIp;
    }

    public byte[] getModifyGateway() {
        return modifyGateway;
    }

    public void setModifyGateway(byte[] modifyGateway) {
        this.modifyGateway = modifyGateway;
    }

    public byte[] getModifySubnet() {
        return modifySubnet;
    }

    public void setModifySubnet(byte[] modifySubnet) {
        this.modifySubnet = modifySubnet;
    }

    public int getPcSearchIpPort() {
        return pcSearchIpPort;
    }

    public void setPcSearchIpPort(int pcSearchIpPort) {
        this.pcSearchIpPort = pcSearchIpPort;
    }

    public byte[] getReserved() {
        return reserved;
    }

    public void setReserved(byte[] reserved) {
        this.reserved = reserved;
    }


    @Override
    public String toString() {
        return "TagBroadCastPacket{" +
                "\nhead=" + Arrays.toString(head) +
                ", \nip=" + Arrays.toString(ip) +
                ", \nmac=" + Arrays.toString(mac) +
                ", \nid=" + Arrays.toString(id) +
                ", \nisModifyCameraIp=" + Arrays.toString(isModifyCameraIp) +
                ", \nreserved1=" + Arrays.toString(reserved1) +
                ", \nmodifyIp=" + Arrays.toString(modifyIp) +
                ", \nmodifyGateway=" + Arrays.toString(modifyGateway) +
                ", \nmodifySubnet=" + Arrays.toString(modifySubnet) +
                ", \npcSearchIpPort=" + pcSearchIpPort +
                ",\nreserved=" + Arrays.toString(reserved) +
                '}';
    }
}
