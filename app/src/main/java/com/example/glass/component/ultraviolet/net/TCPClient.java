package com.example.glass.component.ultraviolet.net;

import java.net.Socket;

public class TCPClient {
    public final static int CAMERA_DATA_PORT = 60003;
    private String ServiceIP;
    private Socket socket;

    public TCPClient(String serviceIP) {
        ServiceIP = serviceIP;

    }
}
