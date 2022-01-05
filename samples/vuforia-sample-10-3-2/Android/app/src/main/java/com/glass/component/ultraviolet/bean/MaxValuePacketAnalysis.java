package com.glass.component.ultraviolet.bean;

public class MaxValuePacketAnalysis {

    public static MaxValuePacket analysis(byte[] data) {
        MaxValuePacket packet = new MaxValuePacket();
        byte[] flag1 = new byte[4];
        byte[] flag2 = new byte[4];
        byte[] nWidth = new byte[2];
        byte[] nHeight = new byte[2];
        byte[]  nImagePackageLen = new byte[4];
        byte[]  nCounter = new byte[4];
        byte[]  nImageInfo = new byte[2];
        byte[]  nCalcResultPrecion = new byte[2];
        byte[]  sIndex = new byte[4];
        byte[]  eIndex = new byte[4];
        byte[]  aIndex = new byte[4];
        byte[]  aSize = new byte[4];
        byte[]	MaxValue = new byte[2]; 	// 单位时间内最大值，时间可通过参数stfp设置
        byte[]	nReserved0 = new byte[2];
        byte[]  nReserved = new byte[20];
        System.arraycopy(data,0,flag1,0,4);
        System.arraycopy(data,4,flag2,0,4);
        System.arraycopy(data,8,nWidth,0,2);
        System.arraycopy(data,10,nHeight,0,2);
        System.arraycopy(data,12,nImagePackageLen,0,4);
        System.arraycopy(data,16,nCounter,0,4);
        System.arraycopy(data,20,nImageInfo,0,2);
        System.arraycopy(data,22,nCalcResultPrecion,0,2);
        System.arraycopy(data,24,sIndex,0,4);
        System.arraycopy(data,28,eIndex,0,4);
        System.arraycopy(data,32,aIndex,0,4);
        System.arraycopy(data,36,aSize,0,4);
        System.arraycopy(data,40,MaxValue,0,2);
        System.arraycopy(data,42,nReserved0,0,2);
        System.arraycopy(data,44,nReserved,0,20);

        packet.flag1 = flag1;
        packet.flag2 = flag2;
        packet.nWidth = nWidth;
        packet.nHeight = nHeight;
        packet.nImagePackageLen = nImagePackageLen;
        packet.nCounter = nCounter;
        packet.nImageInfo = nImageInfo;
        packet.nCalcResultPrecion = nCalcResultPrecion;
        packet.sIndex = sIndex;
        packet.eIndex = eIndex;
        packet.aIndex = aIndex;
        packet.aSize = aSize;
        packet.MaxValue = MaxValue;
        packet.nReserved0 = nReserved0;
        packet.nReserved = nReserved;

        return packet;
    }
}
