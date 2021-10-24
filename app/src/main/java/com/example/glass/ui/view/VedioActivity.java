package com.example.glass.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.glass.R;
import com.example.glass.component.video.VideoServer;
import com.example.glass.component.video.receive.SelfVideoplay;
import com.example.glass.component.video.receive.Videoplay;
import com.example.glass.utils.TimeUtil;

import java.io.ByteArrayOutputStream;

public class VedioActivity extends AppCompatActivity implements VideoServer.ReceiveVideoCallback {
    private static final String TAG = "VideoChatActivity";
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 10;

    private Videoplay view = null;      //对面大视图视频
    private Videoplay view2 = null;       //对面小视图视频
    private boolean isSmallView = false;      //是否是小视图

    // private SurfaceView surfaceView;
    private SelfVideoplay selfVideo;
    private Camera mCamera = null; //创建摄像头处理类
    private int cameraPosition = 1;//1代表前置摄像头，0代表后置摄像头


    private VideoServer videoServer;

    private TextView duration_text;

    private long chat_start_time;

    public static final int SHOW_TIME = 5;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SHOW_TIME) {
                //更改通话时间
                duration_text.setText(TimeUtil.getTimeStr(chat_start_time, System.currentTimeMillis()));
                handler.sendEmptyMessageDelayed(SHOW_TIME, 1000);

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        chat_start_time = System.currentTimeMillis();
        videoServer = new VideoServer(this,8080);


        initView();

    }


    private void initView() {
        view = (Videoplay) this.findViewById(R.id.video_play);
        view2 = (Videoplay) this.findViewById(R.id.video_play2);

        // surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        // holder = surfaceView.getHolder();
        // holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        selfVideo = (SelfVideoplay) findViewById(R.id.surface_view);

        duration_text = (TextView) findViewById(R.id.chat_duration);

        handler.sendEmptyMessage(SHOW_TIME);

      //  audioServer.startRecording();

        videoServer.doStart();
    }


    @Override
    public void receiveVideoStream(byte[] frmbuf, int frmSize, long timestamp) {
        //与语音同步
        //    videoServer.decode.DecoderNal(frmbuf, frmSize, wrapper_view.mPixel);//解码后的图像存在mPixel中
        //    wrapper_view.postInvalidate();
        //Log.e(Config.TAG, "接受视频数据间隔x：" + (audioServer.lastTime - timestamp));


        //播方视频
        //Log.e(Config.TAG,"播放视频");
        if (isSmallView) {
            videoServer.decode.DecoderNal(frmbuf, frmSize, view2.mPixel);//解码后的图像存在mPixel中
            view2.postInvalidate();
        } else {
            videoServer.decode.DecoderNal(frmbuf, frmSize, view.mPixel);//解码后的图像存在mPixel中
            view.postInvalidate();
        }

      /*
        VideoData videoData = new VideoData(frmbuf, frmSize, timestamp);
        dataLinkedList.addLast(videoData);
       while (dataLinkedList.size() > 0) {
            final VideoData data = dataLinkedList.getFirst();
            if (Math.abs(data.time - audioServer.lastTime) <= 200) {
                //播方视频
                //Log.e(Config.TAG,"播放视频");
                if (isStop && wrapper_view != null) {
                    videoServer.decode.DecoderNal(data.data, data.size, wrapper_view.mPixel);//解码后的图像存在mPixel中
                    wrapper_view.postInvalidate();

                } else if (isSmallView) {
                    videoServer.decode.DecoderNal(data.data, data.size, view2.mPixel);//解码后的图像存在mPixel中
                    view2.postInvalidate();
                } else {
                    videoServer.decode.DecoderNal(data.data, data.size, view.mPixel);//解码后的图像存在mPixel中
                    view.postInvalidate();
                }
                dataLinkedList.removeFirst();
                break;
            } else if (data.time - audioServer.lastTime > 200) {
                //视频流比音频流快  不播放
                //Log.e(Config.TAG,"视频流比音频流快  不播放:"+(data.time - audioServer.lastTime));
                break;
            } else if (data.time - audioServer.lastTime < 200) {
                //视频流比音频流慢  舍弃  继续遍历
                //Log.e(Config.TAG,"视频流比音频流慢  舍弃");
                dataLinkedList.removeFirst();
                continue;
            }
        }
        //Log.d(com.nercms.Config.TAG,"size:"+frmSize+"  gg1:"+ Arrays.toString(frmbuf));
        // }
*/

    }


    /**
     * 开启 接受 发送rtp线程  开启本地摄像头
     */
    public void doStart() {
        openCamera();

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("xxxx", "onResume");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doStart();

            }
        }, 100);

    }

    public void openCamera() {
        if (mCamera == null) {

            //摄像头设置，预览视频
            mCamera = Camera.open(cameraPosition); //实例化摄像头类对象
            Camera.Parameters p = mCamera.getParameters(); //将摄像头参数传入p中
            p.setFlashMode("off");
            p.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            p.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            //p.setPreviewFormat(PixelFormat.YCbCr_420_SP); //设置预览视频的格式
            p.setPreviewFormat(ImageFormat.NV21);

            p.setPreviewSize(352, 288); //设置预览视频的尺寸，CIF格式352×288
            //p.setPreviewSize(800, 600);
            p.setPreviewFrameRate(15); //设置预览的帧率，15帧/秒
            mCamera.setParameters(p); //设置参数



  /*          int PreviewWidth = 0;
            int PreviewHeight = 0;
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);//获取窗口的管理器
            Display display = wm.getDefaultDisplay();//获得窗口里面的屏幕
            Camera.Parameters parameters  = mCamera.getParameters();
            // 选择合适的预览尺寸
            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();

            // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
            if (sizeList.size() > 1) {
                Iterator<Camera.Size> itor = sizeList.iterator();
                while (itor.hasNext()) {
                    Camera.Size cur = itor.next();
                    if (cur.width >= PreviewWidth
                            && cur.height >= PreviewHeight) {
                        PreviewWidth = cur.width;
                        PreviewHeight = cur.height;
                        break;
                    }
                }
            }
            parameters.setPreviewSize(PreviewWidth, PreviewHeight); //获得摄像区域的大小
            parameters.setPreviewFrameRate(3);//每秒3帧  每秒从摄像头里面获得3个画面
            parameters.setPictureFormat(PixelFormat.JPEG);//设置照片输出的格式
            parameters.set("jpeg-quality", 85);//设置照片质量
            parameters.setPictureSize(PreviewWidth, PreviewHeight);//设置拍出来的屏幕大小
            //
            mCamera.setParameters(parameters);//把上面的设置 赋给摄像头
            */


            byte[] rawBuf = new byte[1400];
            mCamera.addCallbackBuffer(rawBuf);
            mCamera.setDisplayOrientation(90); //视频旋转90度
         /*   try {
                mCamera.setPreviewDisplay(holder); //预览的视频显示到指定窗口
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            mCamera.startPreview(); //开始预览

            //获取帧
            //预览的回调函数在开始预览的时候以中断方式被调用，每秒调用15次，回调函数在预览的同时调出正在播放的帧
            Callback a = new Callback();
            mCamera.setPreviewCallback(a);

        }
    }

    //mCamera回调的类
    class Callback implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] frame, Camera camera) {
            //audioServer.encode();
            // Log.d("xxxxx","size1:"+Arrays.toString(frame));
            //YUVtoRGBUtil.decodeYUV420SP(selfVideo.mPixel,frame,352,288);


            YuvImage image = new YuvImage(frame, ImageFormat.NV21, 352, 288, null);            //ImageFormat.NV21  640 480
            ByteArrayOutputStream outputSteam = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 70, outputSteam); // 将NV21格式图片，以质量70压缩成Jpeg，并得到JPEG数据流
            selfVideo.jpegData = outputSteam.toByteArray();
            selfVideo.postInvalidate();
            videoServer.sendData(frame);
        }
    }


    /**
     * 关闭摄像头 并释放资源
     */
    public void close() {

        //释放摄像头资源
        if (mCamera != null) {
            mCamera.setPreviewCallback(null); //停止回调函数
            mCamera.stopPreview(); //停止预览
            mCamera.release(); //释放资源
            mCamera = null; //重新初始化
        }

        videoServer.stopServer();
        //audioServer.stopRecording();

        //通知对方关闭
        if (!isClosed) {
//            Request request = new Request();
//            request.from_id = MyApplication.getInstance().getSpUtil().getUser().id;
//            request.into_id = remote_user.id;
//            request.tag = MessageTag.STOP_VIDEO;
//
//            MyApplication.getInstance().getSendMsgUtil().sendMessageToServer(
//                    MyApplication.getInstance().getGson().toJson(request)
//            );
        }

      //  soundPool.release();

        //初始化selfvideoplay  Videoplay
        Videoplay.close();

    }

    public boolean isClosed = false;        //是不是对方关闭的

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("xxxx", "onDestroy");
        close();
    }

    public void doCancle(View v) {
        finish();
    }

}