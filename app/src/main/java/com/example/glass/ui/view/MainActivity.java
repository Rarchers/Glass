package com.example.glass.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glass.R;

import com.rokid.glass.instruct.InstructLifeManager;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;
import com.rokid.glass.speech.AsrCallBack;
import com.rokid.glass.speech.SpeechUserManager;
import com.rokid.glass.speech.TtsCallBack;
import com.rokid.glass.ui.button.GlassButton;

public class MainActivity extends AppCompatActivity {


    private InstructLifeManager mLifeManager;
    private SpeechUserManager mSpeechManager;
    private GlassButton ultraviolet;
    private GlassButton infrared;
    private GlassButton camera;
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
       ultraviolet = findViewById(R.id.ultravioletButton);
       infrared = findViewById(R.id.infraredButton);
       camera = findViewById(R.id.cameraButton);

       camera.setClickable(true);
       ultraviolet.setClickable(true);
       infrared.setClickable(true);

       camera.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(MainActivity.this,CameraActivity.class);
               startActivity(intent);
           }
       });

       ultraviolet.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this,UltravioletActivity.class);
               startActivity(intent);
           }
       });

       infrared.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this,InfraredActivity.class);
               startActivity(intent);
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