package com.example.glass.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glass.R;

import com.example.glass.component.ultraviolet.bean.PicInfoPacket;
import com.example.glass.component.ultraviolet.net.OnMsgReturnedListener;
import com.example.glass.component.ultraviolet.net.TCPClient;
import com.example.glass.component.ultraviolet.net.UDPClient;
import com.example.glass.utils.Java2StructUtils;
import com.rokid.glass.instruct.InstructLifeManager;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;
import com.rokid.glass.ui.button.GlassButton;

import java.util.Arrays;

public class UltravioletActivity extends AppCompatActivity {

    private InstructLifeManager mLifeManager;
    private GlassButton send;
    private TextView information;
    private UDPClient udpClient;
    private TCPClient tcpClient;

    private volatile StringBuilder builder = new StringBuilder();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultraviolet);
        initView();


    }


    private void initView(){
        send = findViewById(R.id.ultravioletButton);
        information = findViewById(R.id.ultravioletInformation);
        configInstruct();

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
                startCommend();
            }
        });


    }


    private void startCommend(){
        if (UDPClient.scanFinish){

            tcpClient = new TCPClient(new OnMsgReturnedListener() {
                @Override
                public void onMsgReturned(Object msg) {
                    try{
                        PicInfoPacket packet = (PicInfoPacket) msg;
                        if (Java2StructUtils.byteArrayToInt32(packet.getaSize())>0){
                            Toast.makeText(UltravioletActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

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





            setText("\ntcp????????????",2);
            tcpClient.startTcpReceive();
        }else {
            setText("?????????????????????????????????...",2);
        }



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


    public void configInstruct() {
        mLifeManager = new InstructLifeManager(this, getLifecycle(), mInstructLifeListener);
        mLifeManager.addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("??????", null))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "start"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                startCommend();
                            }
                        })
        ).addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("?????????", null))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "back"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                if (act != null)
                                    act.finish();
                            }
                        })
        );;
    }

    private InstructLifeManager.IInstructLifeListener mInstructLifeListener = new InstructLifeManager.IInstructLifeListener() {

        /** ??????????????????????????????????????????????????????????????????????????????????????????
         * ??????????????????????????????????????????????????????false???
         * @param command
         * @return true??????????????? false??????????????????
         */
        @Override
        public boolean onInterceptCommand(String command) {

            if ("??????".equals(command)) {
                return true;
            }
            return false;
        }

        @Override
        public void onTipsUiReady() {
            Log.d("AudioAi", "onTipsUiReady Call ");
            if (mLifeManager != null) {
                mLifeManager.setLeftBackShowing(false);
            }
        }

        @Override
        public void onHelpLayerShow(boolean show) {

        }
    };
}