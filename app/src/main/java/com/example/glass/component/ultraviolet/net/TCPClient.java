package com.example.glass.component.ultraviolet.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPClient extends Thread {
    public final static int CAMERA_DATA_PORT = 60003;

    private Socket socket;
    private OnMsgReturnedListener listener;

    public TCPClient(String serviceIP, OnMsgReturnedListener listener) {
        this.listener = listener;
        try {
            socket = new Socket(serviceIP, CAMERA_DATA_PORT);
        } catch (Exception e) {
            listener.onError(e);
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        super.run();

            try {

                if (socket == null){
                    listener.onError(new Exception("Socket init error"));
                    return;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                while (true) {
                    String readline = br.readLine();
                    if (readline != null) {
                        listener.onMsgReturned(readline);
                    }
                }

            } catch (IOException e) {
                listener.onError(e);
        }
    }
}
