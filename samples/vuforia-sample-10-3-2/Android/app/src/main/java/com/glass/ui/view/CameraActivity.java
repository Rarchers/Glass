package com.glass.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


import com.glass.component.camera.Camera2Helper;
import com.glass.ui.other.AutoFitTextureView;
import com.rokid.glass.instruct.InstructLifeManager;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;
import com.rokid.glass.ui.button.GlassButton;
import com.vuforia.engine.native_sample.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CameraActivity extends AppCompatActivity implements Camera2Helper.AfterDoListener{

    private InstructLifeManager mLifeManager;
    private String TAG = "CameraActivity";
    private Camera2Helper camera2Helper;
    private AutoFitTextureView textureView;
    private File file;
    private GlassButton infraredButton;
    private GlassButton ultravioletStartButton;
    public static final String PHOTO_PATH = Environment.getExternalStorageDirectory().getPath();
    public static final String PHOTO_NAME = "camera2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        configInstruct();
        init();

    }


    private void init(){
        file = new File(PHOTO_PATH, PHOTO_NAME + ".jpg");
        infraredButton = findViewById(R.id.infraredStartButton);
        ultravioletStartButton = findViewById(R.id.ultravioletStartButton);
        textureView= (AutoFitTextureView) findViewById(R.id.camera_preview);
        camera2Helper=Camera2Helper.getInstance(CameraActivity.this,textureView,file);
        camera2Helper.setAfterDoListener(this);

        infraredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startInfrared(CameraActivity.this);
            }
        });

        ultravioletStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUltraviolet(CameraActivity.this);
            }
        });

    }

    private void startInfrared(Activity activity){
        Intent intent = new Intent(activity,InfraredActivity.class);
        startActivity(intent);
    }


    private void startUltraviolet(Activity activity){
        Intent intent = new Intent(activity,UltravioletActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        camera2Helper.startCameraPreView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera2Helper.onDestroyHelper();
    }

    @Override
    public void onAfterPreviewBack() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onAfterTakePicture() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputStream input = null;
                try {
                    input = new FileInputStream(file);
                    byte[] byt = new byte[input.available()];
                    input.read(byt);
                  //  imageView.setImageBitmap(BitmapUtil.bytes2Bitmap(byt));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }




    public void configInstruct() {
        mLifeManager = new InstructLifeManager(this, getLifecycle(), mInstructLifeListener);
        mLifeManager.addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("开启紫外模式", null))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "start ultraviolet"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                startUltraviolet(act);
                            }
                        })
        ).addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("开启红外模式", null))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "start infrared"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                if (act != null)
                                    startInfrared(act);
                            }
                        })
        ).addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("上一步", null))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "back"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                if (act != null)
                                    act.finish();
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