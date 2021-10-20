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

import com.example.glass.application.InstructionApplication;
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

    private GlassButton ultraviolet;
    private GlassButton infrared;
    private GlassButton camera;
    private StringBuilder builder = new StringBuilder();
    private String TAG ="AUDIO";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configInstruct();
        initView();


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
               openCamera(MainActivity.this);
           }
       });

       ultraviolet.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               openUltraviolet(MainActivity.this);
           }
       });

       infrared.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               openInfrared(MainActivity.this);
           }
       });
    }

    private void openCamera(Activity activity){
        Intent intent = new Intent(activity,CameraActivity.class);
        startActivity(intent);
    }

    private void openUltraviolet(Activity activity){
        Intent intent = new Intent(activity,UltravioletActivity.class);
        startActivity(intent);
    }
    private void openInfrared(Activity activity){
        Intent intent = new Intent(activity,InfraredActivity.class);
        startActivity(intent);
    }


    public void configInstruct() {
        mLifeManager = new InstructLifeManager(this, getLifecycle(), mInstructLifeListener);
        mLifeManager.addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("普通相机模块", null))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "camera"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                if (act != null)
                                openCamera(act);
                            }
                        })
        )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("红外模块", null))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "infrared"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        if (act != null)
                                        openInfrared(act);
                                    }
                                })
                )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("离开", null))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "leave"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        if (act != null)
                                           act.finish();
                                    }
                                })
                )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("紫外模块", null))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "ultraviolet"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        if (act != null) {
                                         openUltraviolet(act);
                                        }
                                    }
                                })

                );
    }

    private InstructLifeManager.IInstructLifeListener mInstructLifeListener = new InstructLifeManager.IInstructLifeListener() {

        /** 是否拦截处理当前语音指令，拦截后之前配置的指令闭包不会被调用
         * （可以用来提前处理一些指令，然后返回false）
         * @param command
         * @return true：拦截事件 false：不进行拦截
         */
        @Override
        public boolean onInterceptCommand(String command) {
            Log.d(TAG, "doReceiveCommand command = " + command);

            if ("返回".equals(command)) {
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