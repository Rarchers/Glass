package com.example.glass.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glass.R;

import com.example.glass.component.ultraviolet.bean.TagBroadCastPacket;
import com.example.glass.component.ultraviolet.net.OnMsgReturnedListener;
import com.example.glass.component.ultraviolet.net.UDP;
import com.example.glass.component.ultraviolet.net.UDPBroadCast;
import com.example.glass.component.ultraviolet.net.UDPClient;
import com.example.glass.component.ultraviolet.net.UDPClientResponse;
import com.example.glass.component.ultraviolet.net.UdpBroadCastResponse;
import com.example.glass.utils.IpAddress;
import com.rokid.glass.instruct.InstructLifeManager;
import com.rokid.glass.instruct.VoiceInstruction;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;
import com.rokid.glass.speech.AsrCallBack;
import com.rokid.glass.speech.SpeechUserManager;
import com.rokid.glass.speech.TtsCallBack;
import com.rokid.glass.ui.button.GlassButton;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Button getIp;
    private TextView info;
    private InstructLifeManager mLifeManager;
    private SpeechUserManager mSpeechManager;
    private GlassButton testButton;
    private StringBuilder builder = new StringBuilder();
    private String TAG ="AUDIO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configInstruct();
        initView();
        mSpeechManager = new SpeechUserManager(this, false);

    }


    public void initView(){
        getIp = findViewById(R.id.getIP);
        info = findViewById(R.id.information);
        testButton = findViewById(R.id.custom_dialog_btn);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.append("点击了测试按钮\n");
                info.setText(builder.toString());
                Toast.makeText(MainActivity.this, "点击测试", Toast.LENGTH_SHORT).show();
            }
        });

        getIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void configInstruct() {
        mLifeManager = new InstructLifeManager(this, getLifecycle(), mInstructLifeListener);
        mLifeManager.getInstructConfig().setIgnoreSystem(true);
        mLifeManager.addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("在线测试", "zai xian ce shi"))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "online test"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                if (mSpeechManager != null) {
                                    mSpeechManager.doSpeechAsr(mAsrCallBack);
                                }
                            }
                        })
        ).addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("声音测试", "sheng yin ce shi"))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "tts test"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                if (mSpeechManager != null) {
                                    mSpeechManager.doSpeechTts("乌龙茶饮料很好喝", mTtsCallBack);
                                }
                            }
                        })
        );
    }

    private InstructLifeManager.IInstructLifeListener mInstructLifeListener = new InstructLifeManager.IInstructLifeListener() {
        @Override
        public boolean onInterceptCommand(String command) {
            return false;
        }

        @Override
        public void onTipsUiReady() {
            Log.d("AudioAi", "onTipsUiReady Call ");
        }

        @Override
        public void onHelpLayerShow(boolean show) {

        }
    };

    private AsrCallBack mAsrCallBack = new AsrCallBack() {
        @Override
        public void onStart(int id) throws RemoteException {
            Log.d(TAG, "AsrCallBack onStart id = " + id);
        }

        @Override
        public void onIntermediateResult(int id, String asr, String extra) throws RemoteException {
            Log.d(TAG, "AsrCallBack onIntermediateResult id = " + id+ ", asr = " + asr + ", extra = " + extra);
        }

        @Override
        public void onAsrComplete(int id, String asr) throws RemoteException {
            Log.d(TAG, "AsrCallBack onAsrComplete id = " + id + ", asr = " + asr);
        }

        @Override
        public void onComplete(int id, String nlp, String action) throws RemoteException {
            Log.d(TAG, "AsrCallBack onComplete id = " + id + ", nlp = " + nlp + ", action = " + action);
        }

        @Override
        public void onCancel(int id) throws RemoteException {
            Log.d(TAG, "AsrCallBack onCancel id = " + id);
        }

        @Override
        public void onError(int id, int err) throws RemoteException {
            Log.d(TAG, "AsrCallBack onError id = " + id);
        }
    };

    private TtsCallBack mTtsCallBack = new TtsCallBack() {
        @Override
        public void onStart(int id) throws RemoteException {
            Log.d(TAG, "TtsCallBack onStart id = " + id);
        }

        @Override
        public void onVoicePlay(int id, String text) throws RemoteException {
            Log.d(TAG, "TtsCallBack onVoicePlay id = " + id);
        }

        @Override
        public void onCancel(int id) throws RemoteException {
            Log.d(TAG, "TtsCallBack onCancel id = " + id);
        }

        @Override
        public void onComplete(int id) throws RemoteException {
            Log.d(TAG, "TtsCallBack onComplete id = " + id);
        }

        @Override
        public void onError(int id, int err) throws RemoteException {
            Log.d(TAG, "TtsCallBack onError id = " + id);
        }
    };

    @Override
    protected void onDestroy() {
        if (mSpeechManager != null) {
            mSpeechManager.onDestroy();
            mSpeechManager = null;
        }

        super.onDestroy();
    }

}