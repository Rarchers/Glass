package com.glass.component.ultraviolet.bean;

public class MaxValuePacket {
    byte[] flag1;  // 0x44504300
    byte[] flag2;   //0x00445043
    byte[] nWidth;   //Normal模式下，为实际值；Snap模式下，固定为0
    byte[] nHeight;     //Normal模式下，为实际值；Snap模式下，固定为1
    byte[] nImagePackageLen;  //Snap模式下，该值即为数据宽度
    byte[] nCounter;  // 本包数据第一个采样点的编号，从上电开始一直编号0-0xffffffff
    byte[] nImageInfo;    // normal模式下是0x3000  Snap模式下8bit采样：0x5008；16bit采样：0x5009
    byte[] nCalcResultPrecion;
    byte[] sIndex;   //本包数据内的第一个采样的编号（一次燃弧中，第一包第一个为0）
    byte[] eIndex;   //本包数据内的，最后一个采样点的编号
    byte[] aIndex;   //本次燃弧，上传数据包所属的编号
    byte[] aSize;  //本包内的燃弧大小：本包的燃弧累加值*系数
    byte[]	MaxValue; 	// 单位时间内最大值，时间可通过参数stfp设置
    byte[]	nReserved0;
    byte[] nReserved;


    public MaxValuePacket() {
        flag1 = new byte[4];
        flag2 = new byte[4];
        nWidth = new byte[2];
        nHeight = new byte[2];
        nImagePackageLen = new byte[4];
        nCounter = new byte[4];
        nImageInfo = new byte[2];
        nCalcResultPrecion = new byte[2];
        sIndex = new byte[4];
        eIndex = new byte[4];
        aIndex = new byte[4];
        aSize = new byte[4];
        MaxValue = new byte[2];
        nReserved0 = new byte[2];
        nReserved = new byte[20];
    }

    public byte[] getFlag1() {
        return flag1;
    }

    public void setFlag1(byte[] flag1) {
        this.flag1 = flag1;
    }

    public byte[] getFlag2() {
        return flag2;
    }

    public void setFlag2(byte[] flag2) {
        this.flag2 = flag2;
    }

    public byte[] getnWidth() {
        return nWidth;
    }

    public void setnWidth(byte[] nWidth) {
        this.nWidth = nWidth;
    }

    public byte[] getnHeight() {
        return nHeight;
    }

    public void setnHeight(byte[] nHeight) {
        this.nHeight = nHeight;
    }

    public byte[] getnImagePackageLen() {
        return nImagePackageLen;
    }

    public void setnImagePackageLen(byte[] nImagePackageLen) {
        this.nImagePackageLen = nImagePackageLen;
    }

    public byte[] getnCounter() {
        return nCounter;
    }

    public void setnCounter(byte[] nCounter) {
        this.nCounter = nCounter;
    }

    public byte[] getnImageInfo() {
        return nImageInfo;
    }

    public void setnImageInfo(byte[] nImageInfo) {
        this.nImageInfo = nImageInfo;
    }

    public byte[] getnCalcResultPrecion() {
        return nCalcResultPrecion;
    }

    public void setnCalcResultPrecion(byte[] nCalcResultPrecion) {
        this.nCalcResultPrecion = nCalcResultPrecion;
    }

    public byte[] getsIndex() {
        return sIndex;
    }

    public void setsIndex(byte[] sIndex) {
        this.sIndex = sIndex;
    }

    public byte[] geteIndex() {
        return eIndex;
    }

    public void seteIndex(byte[] eIndex) {
        this.eIndex = eIndex;
    }

    public byte[] getaIndex() {
        return aIndex;
    }

    public void setaIndex(byte[] aIndex) {
        this.aIndex = aIndex;
    }

    public byte[] getaSize() {
        return aSize;
    }

    public void setaSize(byte[] aSize) {
        this.aSize = aSize;
    }

    public byte[] getMaxValue() {
        return MaxValue;
    }

    public void setMaxValue(byte[] maxValue) {
        MaxValue = maxValue;
    }

    public byte[] getnReserved0() {
        return nReserved0;
    }

    public void setnReserved0(byte[] nReserved0) {
        this.nReserved0 = nReserved0;
    }

    public byte[] getnReserved() {
        return nReserved;
    }

    public void setnReserved(byte[] nReserved) {
        this.nReserved = nReserved;
    }
}
