package com.example.glass.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.glass.R;
import com.example.glass.component.ultraviolet.net.OnMsgReturnedListener;
import com.example.glass.component.ultraviolet.net.TCPClient;
import com.example.glass.component.ultraviolet.net.UDPClient;
import com.rokid.glass.ui.button.GlassButton;

import java.util.Arrays;

public class UltravioletActivity extends AppCompatActivity {


    private GlassButton send;
    private TextView information;
    private UDPClient udpClient;
    private TCPClient tcpClient;

    private volatile StringBuilder builder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultraviolet);
        initView();


    }


    private void initView(){
        send = findViewById(R.id.ultravioletButton);
        information = findViewById(R.id.ultravioletInformation);


        udpClient =  new UDPClient(this,new OnMsgReturnedListener() {
            @Override
            public void onMsgReturned(Object msg) {

                setText((String)msg,0);
            }

            @Override
            public void onError(Exception ex) {
                setText(ex.toString(),1);
                setText(Arrays.toString(ex.getStackTrace()),1);
            }

            @Override
            public void onStateMsg(String state) {
                setText(state,2);
            }
        });











        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UDPClient.scanFinish){

                    tcpClient = new TCPClient(new OnMsgReturnedListener() {
                        @Override
                        public void onMsgReturned(Object msg) {

                            setText((String)msg,0);
                        }

                        @Override
                        public void onError(Exception ex) {
                            setText(ex.toString(),1);
                            setText(Arrays.toString(ex.getStackTrace()),1);
                        }

                        @Override
                        public void onStateMsg(String state) {
                            setText(state,2);
                        }
                    });





                    setText("\ntcp连接准备",2);
                    tcpClient.startTcpReceive();
                }else {
                    setText("请等待确认紫外相机连接...",2);
                }



            }
        });


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
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (udpClient != null){
            udpClient.closeAll();
        }

        if (tcpClient != null){
            tcpClient.closeAll();
        }

    }
}