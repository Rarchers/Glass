package com.example.glass.ui.view;

import static com.example.glass.ui.view.InfraredActivity.scrollToBottom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.glass.R;
import com.example.glass.component.Config;
import com.example.glass.component.ultraviolet.bean.ParamSetting;
import com.example.glass.component.ultraviolet.net.OnMsgReturnedListener;
import com.example.glass.component.ultraviolet.net.TCPClient;
import com.example.glass.component.ultraviolet.net.UDPClient;
import com.rokid.glass.ui.button.GlassButton;

import org.sipdroid.net.RtpSocket;
import org.sipdroid.net.SipdroidSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class UltravioletSettingActivity extends AppCompatActivity {

    private GlassButton send;
    private TextView information;
    private UDPClient udpClient;
    private ScrollView ultravioletscrollView;
    private volatile StringBuilder builder = new StringBuilder();

    InetSocketAddress serverAddress;        //服务器地址
    InetSocketAddress clientAddress;        //客户端地址   用于p2p
    RtpSocket socket;
    RtpSocket sendrtp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultraviolet_setting);
        initView();
    }


    private void initView(){
        send = findViewById(R.id.ultravioletSettingButton);
        information = findViewById(R.id.ultravioletInformation);
        ultravioletscrollView = findViewById(R.id.ultravioletscrollView);

        serverAddress = new InetSocketAddress(Config.remoteIP_PhoneUse,
                20000);
        try{
            socket = new RtpSocket(new SipdroidSocket(20000));
            sendrtp = new RtpSocket(new SipdroidSocket(1234), InetAddress.getByName(Config.remoteIP_PhoneUse),20000);
        }catch (Exception e){
            e.printStackTrace();
        }





        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sendrtp.send("hello",serverAddress);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        new Thread(new Runnable() {
            byte data[] = new byte[2048];
            DatagramPacket packet = new DatagramPacket(data, 2048);
            @Override
            public void run() {
                while (true){
                    try {
                        Log.e(Config.TAG, "run: receive");
                        socket.socket.receive(packet);
                        String receivemsg = new String(data).trim();
                        setText(receivemsg,0);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();



//        udpClient =  new UDPClient(this,new OnMsgReturnedListener() {
//            @Override
//            public void onMsgReturned(Object msg) {
//
//                setText((String)msg,0);
//            }
//
//            @Override
//            public void onError(Exception ex) {
//                setText(ex.toString(),1);
//                setText(Arrays.toString(ex.getStackTrace()),1);
//            }
//
//            @Override
//            public void onStateMsg(String state) {
//                setText(state,2);
//            }
//        });
//
//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (UDPClient.scanFinish){
//                    setText("开始恢复出厂设置",0);
//                    udpClient.sendMessage(ParamSetting.CFAU);
//                }else {
//                    setText("请等待确认紫外相机连接...",2);
//                }
//
//
//
//            }
//        });


    }


    private void setText(String msg,int flag){
//        switch (flag){
//            case 0:{
//                builder.append("<font color='green'>");
//                break;
//            }
//            case 1: {
//                builder.append("<font color='red'>");
//                break;
//            }
//            case 2:{
//                builder.append("<font color='black'>");
//                break;
//            }
//        }
        builder.append(msg);
        builder.append("\n");
        //  builder.append("</font>");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                information.setText(builder.toString());
                scrollToBottom(ultravioletscrollView,information);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (udpClient != null){
            udpClient.closeAll();
        }
    }
}