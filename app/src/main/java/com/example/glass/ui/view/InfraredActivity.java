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
import com.flir.flironesdk.Device;
import com.flir.flironesdk.FlirUsbDevice;
import com.flir.flironesdk.Frame;
import com.flir.flironesdk.FrameProcessor;
import com.flir.flironesdk.RenderedImage;
import com.rokid.glass.instruct.InstructLifeManager;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Timer;
import java.util.TimerTask;

public class InfraredActivity extends AppCompatActivity implements Device.Delegate, FrameProcessor.Delegate, Device.StreamDelegate, Device.PowerUpdateDelegate{

    private ImageView thermalImageView;
    private volatile Device flirOneDevice;
    private FrameProcessor frameProcessor;
    private TextView aver,max,min,battery; //最高-平均
    private SoundPool sp;
    private int sound;
    private ImageHelp imageHelp;
    private int maxX, maxY;
    private TextView information;
    private StringBuilder builder = new StringBuilder();
    private int width;
    private int height;
    private short[] thermalPixels;
    private ScrollView scrollView;
    private Device.TuningState currentTuningState = Device.TuningState.Unknown;
    private double maxTemp, meantTemp,minTemp;//最高-平均温度

    private GlobalConfig config;

    private ProgressBar loading;
    private ImageView spotMeterIcon;
    private InstructLifeManager mLifeManager;
    ScaleGestureDetector mScaleDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.carriageaxletemperature);
        configInstruct();
        information = findViewById(R.id.infraredInfo);
        scrollView = findViewById(R.id.scrollviewInfrared);

       // Intent serviceIntent = new Intent(InfraredActivity.this, UpLoadService.class);
        //startService(serviceIntent);
        config = new GlobalConfig();
        config.readConfig();

        spotMeterIcon = (ImageView) findViewById(R.id.spotMeterIcon);
        loading = (ProgressBar) findViewById(R.id.loading);

        //最高-低
        aver = (TextView) findViewById(R.id.aver);
        max = (TextView) findViewById(R.id.max);
        min = (TextView) findViewById(R.id.min);



        max.setText("最高");
        min.setText("最低");
        aver.setText("平均");

        try {
            Device.startDiscovery(this, this);
        } catch (IllegalStateException e) {
            setText("开始搜寻相机，请确保相机已开机并连接");
            setText("若长时间无画面请检查相机后重新进入此页面...");
            // it's okay if we've already started discovery
        } catch (SecurityException e) {
            setText(Arrays.toString(e.getStackTrace()));
            // On some platforms, we need the user to select the app to give us permisison to the USB device.
            Toast.makeText(this, "请插入一个Flir设备", Toast.LENGTH_LONG).show();
            // There is likely a cleaner way to recover, but for now, exit the activity and
            // wait for user to follow the instructions;
            finish();
        }





        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        sound = sp.load(this, R.raw.sound, 0);






        RenderedImage.ImageType defaultImageType = RenderedImage.ImageType.BlendedMSXRGBA8888Image;
        frameProcessor = new FrameProcessor(this, this, EnumSet.of(defaultImageType, RenderedImage.ImageType.ThermalRadiometricKelvinImage));

        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                frameProcessor.setMSXDistance(detector.getScaleFactor());
                return false;
            }
        });


        imageHelp = new ImageHelp(GlobalConfig.IMAGE_PATH);

        imageHelp.checkAllImagesDate();


    }


    @Override
    protected void onStart() {
        super.onStart();
        thermalImageView = (ImageView) findViewById(R.id.imageView);
        //若未连接设备，则显示"请连接设备"
//        if (Device.getSupportedDeviceClasses(this).contains(FlirUsbDevice.class)) {
//            setText("未连接设备");
//            findViewById(R.id.pleaseConnect).setVisibility(View.VISIBLE);
//        }
        try {
            Device.startDiscovery(this, this);
        } catch (IllegalStateException e) {
            setText("开始搜寻相机，请确保相机已开机并连接");
            setText("若长时间无画面请检查相机后重新进入此页面...");
            // it's okay if we've already started discovery
        } catch (SecurityException e) {
            // On some platforms, we need the user to select the app to give us permisison to the USB device.
            Toast.makeText(this, "请插入一个Flir设备", Toast.LENGTH_LONG).show();
            // There is likely a cleaner way to recover, but for now, exit the activity and
            // wait for user to follow the instructions;
            finish();
        }
    }

    @Override
    public void onTuningStateChanged(Device.TuningState tuningState) {
        setText("当前相机状态："+tuningState);
        currentTuningState = tuningState;
        if (tuningState == Device.TuningState.InProgress) {
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    super.run();
                    setText("校准相机中...");
                    thermalImageView.setColorFilter(Color.DKGRAY, PorterDuff.Mode.DARKEN);
                    findViewById(R.id.tuningProgressBar).setVisibility(View.VISIBLE);
                    findViewById(R.id.tuningTextView).setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                    spotMeterIcon.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Thread() {
                @Override
                public void run() {
                    super.run();
                    setText("完成校准!");
                    thermalImageView.clearColorFilter();
                    findViewById(R.id.pleaseConnect).setVisibility(View.GONE);
                    findViewById(R.id.tuningProgressBar).setVisibility(View.GONE);
                    findViewById(R.id.tuningTextView).setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onAutomaticTuningChanged(boolean b) {

    }



    @Override
    public void onDeviceConnected(Device device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.pleaseConnect).setVisibility(View.GONE);
            }
        });
        setText("连接红外相机成功");

        flirOneDevice = device;
        flirOneDevice.setPowerUpdateDelegate(this);
        flirOneDevice.startFrameStream(this);
    }

    @Override
    public void onDeviceDisconnected(Device device) {
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
        flirOneDevice = null;
    }



    @Override
    public void onFrameReceived(Frame frame) {
        if (currentTuningState != Device.TuningState.InProgress) {
            frameProcessor.processFrame(frame);
        }
    }
    private Bitmap thermalBitmap = null;
    @Override
    public void onFrameProcessed(RenderedImage renderedImage) {

        if (renderedImage.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
            // Note: this code is not optimized

            thermalPixels = renderedImage.thermalPixelData();

            width = renderedImage.width();
            height = renderedImage.height();
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

            new Thread(new Runnable() {
                short[] thermalPixels = renderedImage.thermalPixelData();
                int width = renderedImage.width();
                int height = renderedImage.height();


                @Override
                public void run() {
                    double pixelCMax = 0,pixelCMin = 100;
                    double pixelCAll = 0;
                    int maxIndex = 0;
                    int pixelTemp;
                    double[] temp = new double[width * height];
                    for (int i = 0; i < width * height; i++) {
                        pixelTemp = thermalPixels[i] & 0xffff;
                        temp[i] = (pixelTemp / 100) - 273.15;
                        pixelCMax = pixelCMax < temp[i] ? temp[i] : pixelCMax;
                        pixelCMin = pixelCMin > temp[i] ? temp[i] : pixelCMin;
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




//                    mp = MediaPlayer.create(InfraredActivity.this, R.raw.warn);
//                    mp_strong = MediaPlayer.create(InfraredActivity.this, R.raw.warn_strong);
//                    if (warnButton.isChecked() == true) {
//                        if (pixelCMax > thresholdHelp.getThreshold_low() && pixelCMax < thresholdHelp.getThreshold_high()) {
//                            mp.start();
//                        } else if (pixelCMax > thresholdHelp.getThreshold_high()) {
//                            mp.stop();
//                            mp_strong.start();
//                        }
//                    }
                }
            }).start();
            //////

            double averageTemp = 0;

            for (int i = 0; i < centerPixelIndexes.length; i++) {  //centerPixelIndexes.length = 9
                // Remember: all primitives are signed, we want the unsigned value,
                // we could also use renderedImage.thermalPixelValues() instead
                int pixelValue = (thermalPixels[centerPixelIndexes[i]]) & 0xffff;
                averageTemp += (((double) pixelValue) - averageTemp) / ((double) i + 1);
            }
            //Log.i("centerPixelIndex", centerPixelIndexes.length + "");
            double averageC = (averageTemp / 100) - 273.15;

            Message msg = new Message();
            msg.what = (int) meantTemp;
            msg.arg1 = (int) maxTemp;
            msg.arg2 = (int) minTemp;
            handleTemp.sendMessage(msg);

            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
            final String spotMeterValue = numberFormat.format(averageC) + "ºC";

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.spotMeterValue)).setText(spotMeterValue);
                }
            });

            // if radiometric is the only type, also show the image
            if (frameProcessor.getImageTypes().size() == 1) {
                // example of a custom colorization, maps temperatures 0-100C to 8-bit gray-scale
                byte[] argbPixels = new byte[width * height * 4];
                final byte aPixValue = (byte) 255;
                for (int p = 0; p < thermalPixels.length; p++) {
                    int destP = p * 4;
                    byte pixValue = (byte) (Math.min(0xff, Math.max(0x00, ((int) thermalPixels[p] - 27315) * (255.0 / 10000.0))));

                    argbPixels[destP + 3] = aPixValue;
                    // red pixel
                    argbPixels[destP] = argbPixels[destP + 1] = argbPixels[destP + 2] = pixValue;
                }
                thermalBitmap = Bitmap.createBitmap(width, renderedImage.height(), Bitmap.Config.ARGB_8888);

                thermalBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(argbPixels));

                updateThermalImageView(thermalBitmap);
            }
        } else {
            thermalBitmap = renderedImage.getBitmap();
            updateThermalImageView(thermalBitmap);
        }




    }

    private void updateThermalImageView(final Bitmap frame) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                thermalImageView.setImageBitmap(frame);
            }
        });
    }
    private Handler handleTemp = new Handler(){
        public void handleMessage(Message msg) {
            setTemp(msg.what,msg.arg1,msg.arg2);
            super.handleMessage(msg);
        }
    };
    //显示最高-平均温度
    public void setTemp(int averT,int maxT,int minT){
        aver.setText("平均:"+ averT+"℃    ");
        max.setText("最高:" + maxT +"℃    ");
        min.setText("最低:" + minT +"℃    ");
    }

    @Override
    public void onBatteryChargingStateReceived(Device.BatteryChargingState batteryChargingState) {
        Log.i("ExampleApp", "Battery charging state received!");

    }

    @Override
    public void onBatteryPercentageReceived(byte percentage) {
        Log.i("ExampleApp", "Battery percentage received!");

        final TextView levelTextView = (TextView)findViewById(R.id.battery);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                levelTextView.setText(String.valueOf((int) percentage) + "%");
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
        flirOneDevice = null;

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
                        .addEntityKey(new EntityKey("返回", null))
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