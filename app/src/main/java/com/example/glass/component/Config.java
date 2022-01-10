package com.example.glass.component;

public class Config {
    public static String DEVICE_IP = "192.168.1.195";
    public final static String serverIP = "119.29.143.22";      //服务器IP
    public final static int server_port = 27888;                //服务器端口
    public final static String TAG="mylog";
    public final static String remoteIP = "192.168.137.4";
    public final static String testIP ="192.168.137.4";
    public final static String remoteIP_PhoneUse =  "10.220.92.157";
    public final static int sendPort = 1234;
    public final static int receivePort = 1234;

    //视频组件相关API
    public final static String VIDEO_FETCH_PAINT = "http://59.67.246.92:8089/api/get_cfg";
    public final static String VIDEO_VOICE_URL = "59.67.246.92";
    public final static String VIDEO_VOICE_PORT = "8818";
    public final static String VIDEO_CHECK_SUCCESS = "http://59.67.246.92:8088/live?app=live&stream=stream-1";
    public final static String VIDEO_PUSH = "rtmp://59.67.246.92/live/stream-1";
}
