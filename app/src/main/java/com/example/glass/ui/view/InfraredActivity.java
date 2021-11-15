package com.example.glass.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.glass.R;
import com.example.glass.utils.GlobalConfig;
import com.example.glass.utils.ThresholdHelp;
import com.example.glass.utils.imagehelp.ImageHelp;

import com.flir.thermalsdk.ErrorCode;
import com.flir.thermalsdk.androidsdk.ThermalSdkAndroid;
import com.flir.thermalsdk.androidsdk.image.BitmapAndroid;
import com.flir.thermalsdk.image.JavaImageBuffer;
import com.flir.thermalsdk.image.palettes.Palette;
import com.flir.thermalsdk.image.palettes.PaletteManager;
import com.flir.thermalsdk.live.Camera;
import com.flir.thermalsdk.live.CommunicationInterface;
import com.flir.thermalsdk.live.Identity;
import com.flir.thermalsdk.live.connectivity.ConnectionStatusListener;
import com.flir.thermalsdk.live.discovery.DiscoveryEventListener;
import com.flir.thermalsdk.live.discovery.DiscoveryFactory;
import com.flir.thermalsdk.live.remote.Battery;
import com.flir.thermalsdk.live.remote.RemoteControl;
import com.flir.thermalsdk.live.streaming.ThermalImageStreamListener;
import com.rokid.glass.instruct.InstructLifeManager;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;
import com.rokid.glass.ui.button.GlassButton;


public class InfraredActivity extends AppCompatActivity{

    private ImageView thermalImageView;

    private TextView aver,max,min,batterytv; //最高-平均
    private SoundPool sp;
    private int sound;
    private GlassButton getpic;
    private int maxX, maxY;
    private TextView information;
    private StringBuilder builder = new StringBuilder();
    private int width;
    private int height;

    private ScrollView scrollView;
    private double maxTemp, meantTemp,minTemp;//最高-平均温度
    Camera cameraInstance;
    private InstructLifeManager mLifeManager;
    RemoteControl remoteControl;
    Palette palette;

    boolean startPic = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.carriageaxletemperature);
        information = findViewById(R.id.infraredInfo);
        thermalImageView = (ImageView) findViewById(R.id.imageView);
        scrollView = findViewById(R.id.scrollviewInfrared);
        aver = (TextView) findViewById(R.id.aver);
        max = (TextView) findViewById(R.id.max);
        min = (TextView) findViewById(R.id.min);
        batterytv = findViewById(R.id.battery);
        getpic = findViewById(R.id.getpic);


        getpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPic = true;
            }
        });


        configInstruct();
        //Intent serviceIntent = new Intent(InfraredActivity.this, UpLoadService.class);
        //startService(serviceIntent);

        //最高-低
        max.setText("最高");
        min.setText("最低");
        aver.setText("平均");

        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        sound = sp.load(this, R.raw.sound, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initCamera();
    }

    private void initCamera(){
        ThermalSdkAndroid.init(this);
        cameraInstance = new Camera();
        remoteControl = cameraInstance.getRemoteControl();
        palette = PaletteManager.getDefaultPalettes().get(0);

        DiscoveryFactory.getInstance().scan(aDiscoveryEventListener, CommunicationInterface.USB);
        setText("开始搜寻相机，请确保相机已开机并连接");
        setText("若长时间无画面请检查相机后重新进入此页面...");


    }

    Battery.BatteryPercentageListener percentageListener = new Battery.BatteryPercentageListener() {
        @Override
        public void onPercentageChange(int i) {
            setBatterytv(i);
        }
    };

    ConnectionStatusListener listener = new ConnectionStatusListener() {
        @Override
        public void onDisconnected(ErrorCode errorCode) {
            setText("丢失连接,错误： "+errorCode.toString());
        }


    };

    DiscoveryEventListener aDiscoveryEventListener = new DiscoveryEventListener() {
        @Override
        public void onCameraFound(Identity identity) {
            // identity describes a device and is used to connect to device
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setText("搜寻到了相机,开始连接");
                      //  cameraInstance.connect(identity, listener, new ConnectParameters(2000));
                        cameraInstance.connect(identity,listener);
                        cameraInstance.subscribeStream(streamingCallbackImpl);
                        if(remoteControl != null) { // check if device supports RemoteControl
                            Battery battery = remoteControl.getBattery();
                            if(battery != null) { // check if device supports Battery
                                // read out battery status and percentage once
                                Battery.ChargingState state = battery.getChargingState();
                                int percentageLeft = battery.getPercentage();
                                setBatterytv(percentageLeft);
                                // subscribe for updates related with battery state and percentage left
                                // assuming Battery.BatteryStateListener instance is stateListener
                                // assuming Battery.BatteryPercentageListener instance ispercentageListener
                                try {
                                    battery.subscribePercentage(percentageListener);
                                }catch (Exception e){
                                    setText(e.getMessage());
                                }

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        setText("连接出错啦！"+e.getMessage());
                        setText("准备重新尝试连接...");
                        cameraInstance.disconnect();
                        onCameraFound(identity);
                    }
                }
            }).start();
        }

        @Override
        public void onDiscoveryError(CommunicationInterface communicationInterface, ErrorCode errorCode) {
            setText("搜寻出错，错误代码："+errorCode);
        }

        @Override
        public void onCameraLost(Identity identity) {
            setText("相机丢失");
        }

        @Override
        public void onDiscoveryFinished(CommunicationInterface communicationInterface) {
            setText("搜寻完成");
        }
    };

    ThermalImageStreamListener streamingCallbackImpl
            = new ThermalImageStreamListener() {
        @Override
        public void onImageReceived() {
            cameraInstance.withImage((thermalImage) -> {
                thermalImage.setPalette(palette);
                JavaImageBuffer javaBuffer = thermalImage.getImage();

                if (startPic){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InfraredActivity.this, "拍照上传！！", Toast.LENGTH_SHORT).show();
                            setText("点击了拍照上传");
                        }
                    });
                    startPic = false;


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadPic(javaBuffer);
                        }
                    }).start();
                }



              /*  width = thermalImage.getWidth();
                height = thermalImage.getHeight();
                int centerPixelIndex = width * (height / 2) + (width / 2);
                int[] centerPixelIndexes = new int[]{
                        centerPixelIndex, centerPixelIndex - 1, centerPixelIndex + 1,
                        centerPixelIndex - width,
                        centerPixelIndex - width - 1,
                        centerPixelIndex - width + 1,
                        centerPixelIndex + width,
                        centerPixelIndex + width - 1,
                        centerPixelIndex + width + 1
                };
                int pixelTemp;
                double pixelCMax = 0,pixelCMin = 100;
                int maxIndex = 0;
                double pixelCAll = 0;
                double[] temp = new double[width * height];
                for (int i = 0; i < width * height; i++) {
                    pixelTemp = pixels[i] & 0xffff;
                    temp[i] = (pixelTemp*1.0 / 100) - 273.15;
                    pixelCMax = Math.max(pixelCMax, temp[i]);
                    pixelCMin = Math.min(pixelCMin, temp[i]);
                    if (pixelCMax == temp[i]) {
                        maxIndex = i;
                    }
                    pixelCAll += temp[i];
                    meantTemp = pixelCAll / (width * height); //全屏平均温度
                }

                maxTemp = pixelCMax; //全屏最高温度
                minTemp = pixelCMin;
                maxX = maxIndex % width; //最高温度x坐标
                maxY = maxIndex / width; //最高温度y坐标


                double averageTemp = 0;

                for (int i = 0; i < centerPixelIndexes.length; i++) {  //centerPixelIndexes.length = 9
                    // Remember: all primitives are signed, we want the unsigned value,
                    // we could also use renderedImage.thermalPixelValues() instead
                    int pixelValue = (pixels[centerPixelIndexes[i]]) & 0xffff;
                    averageTemp += (((double) pixelValue) - averageTemp) / ((double) i + 1);
                }
                //Log.i("centerPixelIndex", centerPixelIndexes.length + "");
                double averageC = (averageTemp / 100) - 273.15;

*/



                Message msg = new Message();
                msg.what = (int) meantTemp;
                msg.arg1 = (int) (thermalImage.getScale().getRangeMax()- 273.15);
                msg.arg2 = (int)(thermalImage.getScale().getRangeMin()- 273.15);
                handleTemp.sendMessage(msg);



                android.graphics.Bitmap bmp = BitmapAndroid.createBitmap(javaBuffer).getBitMap();
                // refresh UI with the new android.graphics.Bitmap
                updateThermalImageView(bmp);
       //         double low = thermalImage.getScale().getRangeMin().value;
        //        double height = thermalImage.getScale().getRangeMax().value;
           //     setTemp(0,(int)low,(int)height);
            });
        }
    };


//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        //若未连接设备，则显示"请连接设备"
////        if (Device.getSupportedDeviceClasses(this).contains(FlirUsbDevice.class)) {
////            setText("未连接设备");
////            findViewById(R.id.pleaseConnect).setVisibility(View.VISIBLE);
////        }
//        try {
//            DiscoveryFactory.getInstance().scan(aDiscoveryEventListener,
//                    CommunicationInterface.USB);
//        } catch (IllegalStateException e) {
//            setText("开始搜寻相机，请确保相机已开机并连接");
//            setText("若长时间无画面请检查相机后重新进入此页面...");
//            // it's okay if we've already started discovery
//        } catch (SecurityException e) {
//            // On some platforms, we need the user to select the app to give us permisison to the USB device.
//            Toast.makeText(this, "请插入一个Flir设备", Toast.LENGTH_LONG).show();
//            // There is likely a cleaner way to recover, but for now, exit the activity and
//            // wait for user to follow the instructions;
//            finish();
//        }
//    }
    private Handler handleTemp = new Handler(){
        public void handleMessage(Message msg) {
            setTemp(msg.what,msg.arg1,msg.arg2);
            super.handleMessage(msg);
        }
    };


    private void uploadPic(JavaImageBuffer buffer){

    }



    private void updateThermalImageView(final Bitmap frame) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                thermalImageView.setImageBitmap(frame);
            }
        });
    }
    //显示最高-平均温度
    public void setTemp(int averT,int maxT,int minT){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                aver.setText("平均:"+ averT+"℃    ");
                max.setText("最高:" + maxT +"℃    ");
                min.setText("最低:" + minT +"℃    ");
            }
        });

    }

    public void setBatterytv(double battery){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                batterytv.setText("当前电量："+battery);
            }
        });

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.pleaseConnect).setVisibility(View.GONE);
                thermalImageView.setImageBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8));
                thermalImageView.clearColorFilter();
                findViewById(R.id.tuningProgressBar).setVisibility(View.GONE);
                findViewById(R.id.tuningTextView).setVisibility(View.GONE);

            }
        });
        setText("丢失与红外相机的连接");

    }

    public void setText(String info){
        builder.append(info);
        builder.append("\n");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                information.setText(builder.toString());
                scrollToBottom(scrollView,information);
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void scrollToBottom(final View scroll, final View inner) {

        Handler mHandler = new Handler();

        mHandler.post(new Runnable() {
            public void run() {
                if (scroll == null || inner == null) {
                    return;
                }

                int offset = inner.getMeasuredHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }

                scroll.scrollTo(0, offset);
            }
        });
    }




    public void configInstruct() {
        mLifeManager = new InstructLifeManager(this, getLifecycle(), mInstructLifeListener);
        mLifeManager.addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("上一步", null))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "back"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                if (act != null){
                                    Toast.makeText(act, "leave", Toast.LENGTH_SHORT).show();
                                    moveTaskToBack(true);
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