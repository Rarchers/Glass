package com.example.glass.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.glass.R;
import com.example.glass.component.ultraviolet.bean.ParamSetting;
import com.example.glass.component.ultraviolet.net.OnMsgReturnedListener;
import com.example.glass.component.ultraviolet.net.TCPClient;
import com.example.glass.component.ultraviolet.net.UDPClient;
import com.rokid.glass.ui.button.GlassButton;

import java.util.Arrays;

public class UltravioletSettingActivity extends AppCompatActivity {

    private GlassButton send;
    private TextView information;
    private UDPClient udpClient;
    private volatile StringBuilder builder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultraviolet_setting);
        initView();
    }


    private void initView(){
        send = findViewById(R.id.ultravioletSettingButton);
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
                    setText("开始恢复出厂设置",0);
                    udpClient.sendMessage(ParamSetting.CFAU);
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
    }
}