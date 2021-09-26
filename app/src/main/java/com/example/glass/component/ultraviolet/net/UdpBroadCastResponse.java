package com.example.glass.component.ultraviolet.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpBroadCastResponse extends Thread{

    /**
     * 监听服务端发送回来的数据并打印出来
     */
        private boolean isFinish = false;
        private final static int ANDROID_IP_PORT = 60080;
        DatagramSocket socket;
        private OnMsgReturnedListener listener;
        public UdpBroadCastResponse(OnMsgReturnedListener listener){
            this.listener = listener;
        }

        @Override
        public void run() {
            super.run();
            try {
                socket = new DatagramSocket(ANDROID_IP_PORT);
                while(!isFinish) {
                    //监听回送端口
                    byte[] buf = new byte[512];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    //拿数据
                    socket.receive(packet);

                    //拿到发送端的一些信息
                    String ip = packet.getAddress().getHostAddress();
                    int port = packet.getPort();
                    int length = packet.getLength();

                    String msg = new String(buf, 0, length);
                    System.out.println("监听到: " + ip + "\tport: " + port + "\t信息: " + msg);
                    listener.onMsgReturned(msg);
                }

            }catch (Exception e){
                listener.onError(e);
                e.printStackTrace();
            }finally {
                exit();
            }
        }

        public void exit(){
            if (socket != null){
                socket.close();
                socket = null;
            }
            isFinish = true;
        }


}
