package com.glass.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.rokid.glass.instruct.InstructLifeManager;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;
import com.rokid.glass.ui.button.GlassButton;
import com.vuforia.engine.native_sample.R;

public class MainActivity extends AppCompatActivity {


    private InstructLifeManager mLifeManager;

    private GlassButton download;
    private GlassButton help;
    private GlassButton camera;
    private GlassButton setting;
    private GlassButton history;
    private StringBuilder builder = new StringBuilder();
    private String TAG ="AUDIO";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        configInstruct();
        initView();


    }


    public void initView(){
        download = findViewById(R.id.downloadButton);
        help = findViewById(R.id.helpButton);
        camera = findViewById(R.id.cameraButton);
        history = findViewById(R.id.historyButton);
        setting = findViewById(R.id.settingButton);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDownload(MainActivity.this);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera(MainActivity.this);
            }
        });


        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHelp(MainActivity.this);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSetting(MainActivity.this);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHistory(MainActivity.this);
            }
        });



    }

    private void openCamera(Activity activity){
        Intent intent = new Intent(activity,CameraActivity.class);
        startActivity(intent);
    }

    private void openSetting(Activity activity){
//        Intent intent = new Intent(activity,UltravioletActivity.class);
//        startActivity(intent);
        Toast.makeText(activity, "暂未开放", Toast.LENGTH_SHORT).show();
    }
    private void openHelp(Activity activity){
        Intent intent = new Intent(activity,VedioActivity.class);
        startActivity(intent);
    }

    private void openDownload(Activity activity){
//        Intent intent = new Intent(activity,UltravioletSettingActivity.class);
//        startActivity(intent);
        Toast.makeText(activity, "暂未开放", Toast.LENGTH_SHORT).show();
    }

    private void openHistory(Activity activity){
//        Intent intent = new Intent(activity,UltravioletSettingActivity.class);
//        startActivity(intent);
        Toast.makeText(activity, "暂未开放", Toast.LENGTH_SHORT).show();
    }


    public void configInstruct() {
        mLifeManager = new InstructLifeManager(this, getLifecycle(), mInstructLifeListener);
        mLifeManager.addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("开始巡检", null))
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
                                .addEntityKey(new EntityKey("远程协助", null))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "help"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        if (act != null)
                                        openHelp(act);
                                    }
                                })
                )


                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("历史数据", null))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "history"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        if (act != null)
                                            openHistory(act);
                                    }
                                })
                )

                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("信息下载", null))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "download"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        if (act != null)
                                            openDownload(act);
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
                                .addEntityKey(new EntityKey("设置", null))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "setting"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        if (act != null) {
                                        openSetting(act);
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