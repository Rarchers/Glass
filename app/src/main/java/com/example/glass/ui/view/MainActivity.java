package com.example.glass.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.glass.R;

import com.example.glass.component.ultraviolet.net.OnMsgReturnedListener;
import com.example.glass.component.ultraviolet.net.UDPBroadCast;
import com.example.glass.component.ultraviolet.net.UdpBroadCastResponse;
import com.rokid.glass.instruct.VoiceInstruction;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;

public class MainActivity extends AppCompatActivity {

    private Button getIp;
    private TextView info;

    private final StringBuilder builder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInstruct();
        initView();


    }


    public void initView(){
        getIp = findViewById(R.id.getIP);
        info = findViewById(R.id.information);



        getIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryPacket = "";

                UDPBroadCast.queryIP(queryPacket);


                new UdpBroadCastResponse(new OnMsgReturnedListener() {
                    @Override
                    public void onMsgReturned(String msg) {
                        builder.append("获取到紫外相机IP地址udp广播包 \n");
                        builder.append("包内容：\n");
                        builder.append(msg);
                        builder.append("\n");
                        info.setText(builder.toString());
                    }

                    @Override
                    public void onError(Exception ex) {
                        builder.append("获取IP发生错误：\n");
                        builder.append(ex.toString());
                        builder.append("\n");
                        info.setText(builder.toString());
                    }
                }).start();






            }
        });
    }


    public void initInstruct(){
        // 初始化语音指令SDK，App运行时默认关闭百灵鸟
        VoiceInstruction.init(this);
        // 设置全局指令，无全局指令可以删掉下面的代码
        // eg：”返回“指令
        VoiceInstruction.getInstance().addGlobalInstruct(
                new InstructEntity()
                        .setGlobal(true)
                        .addEntityKey(new EntityKey("返回", "fan hui"))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "back last page"))
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                try {
                                    if (act != null) {
                                        act.finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }));

    }

}