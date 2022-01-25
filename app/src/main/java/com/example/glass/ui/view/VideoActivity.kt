package com.example.glass.ui.view


import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.glass.component.Config
import com.takusemba.rtmppublisher.Publisher
import com.takusemba.rtmppublisher.PublisherListener
import com.example.glass.R
import com.example.glass.component.video.CameraDrawer
import com.example.glass.component.video.CameraTestString
import com.example.glass.component.video.drawerbean.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

class VideoActivity : AppCompatActivity(), PublisherListener {

    private lateinit var publisher: Publisher
    private lateinit var glView: GLSurfaceView
    private lateinit var container: RelativeLayout
    private lateinit var publishButton: Button
    private lateinit var cameraButton: ImageView
    private lateinit var label: TextView
    private lateinit var cameraDrawer : CameraDrawer

    private val url = Config.VIDEO_PUSH
    private val handler = Handler()
    private var thread: Thread? = null
    private var drawerThread : Thread? = null
    private var isCounting = false
    private var drawerUrl : URL = URL(Config.VIDEO_FETCH_PAINT)
    private var drawerConnection : HttpURLConnection? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vedio)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        glView = findViewById(R.id.surface_view)
        container = findViewById(R.id.container)
        publishButton = findViewById(R.id.toggle_publish)
        cameraButton = findViewById(R.id.toggle_camera)
        label = findViewById(R.id.live_label)
        cameraDrawer = findViewById(R.id.cameraDrawer)


        //尝试用反射来Hook一下Camera的参数设置
        var cameraClient : Class<*>? = null
        try {
            cameraClient = Class.forName("com.takusemba.rtmppublisher.CameraClient")
            val methods = cameraClient.declaredMethods
            for (i in methods){
                Log.e("CameraClient Method", "onCreate: $i", )
            }



        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }









        if (url == "") {
            Toast.makeText(this, R.string.error_empty_url, Toast.LENGTH_SHORT)
                .apply { setGravity(Gravity.CENTER, 0, 0) }
                .run { show() }
        } else {
            publisher = Publisher.Builder(this)
                .setGlView(glView)
                .setUrl(url)
                .setSize(Publisher.Builder.DEFAULT_HEIGHT, Publisher.Builder.DEFAULT_WIDTH)
                .setAudioBitrate(Publisher.Builder.DEFAULT_AUDIO_BITRATE)
                .setVideoBitrate(Publisher.Builder.DEFAULT_VIDEO_BITRATE)
                .setCameraMode(Publisher.Builder.DEFAULT_MODE)
                .setListener(this)
                .build()

            publishButton.setOnClickListener {
                if (publisher.isPublishing) {
                    publisher.stopPublishing()
                    Toast.makeText(applicationContext, "当前状态：停止推流", Toast.LENGTH_SHORT).show()
                } else {
                    publisher.startPublishing()
                    Toast.makeText(applicationContext, "当前状态：开始推流", Toast.LENGTH_SHORT).show()
                }
            }

            cameraButton.setOnClickListener {
                publisher.switchCamera()
                Toast.makeText(applicationContext, "切换摄像头", Toast.LENGTH_SHORT).show()
            }
        }

        label.text = getString(R.string.publishing_label, 0L.format(), 0L.format())

      //  startCounting()


        //TODO:测试代码
      //  postDraw(CameraTestString.data)

    }
    override fun onResume() {
        super.onResume()
        if (url.isNotBlank()) {
            updateControls()
        }
    }

    override fun onStarted() {
        Toast.makeText(this, R.string.started_publishing, Toast.LENGTH_SHORT)
            .apply { setGravity(Gravity.CENTER, 0, 0) }
            .run { show() }
        updateControls()
        startCounting()
    }

    override fun onStopped() {
        Toast.makeText(this, R.string.stopped_publishing, Toast.LENGTH_SHORT)
            .apply { setGravity(Gravity.CENTER, 0, 0) }
            .run { show() }
        updateControls()
        stopCounting()
    }

    override fun onDisconnected() {
        Toast.makeText(this, R.string.disconnected_publishing, Toast.LENGTH_SHORT)
            .apply { setGravity(Gravity.CENTER, 0, 0) }
            .run { show() }
        updateControls()
        stopCounting()
    }

    override fun onFailedToConnect() {
        Toast.makeText(this, R.string.failed_publishing, Toast.LENGTH_SHORT)
            .apply { setGravity(Gravity.CENTER, 0, 0) }
            .run { show() }
        updateControls()
        stopCounting()
    }

    private fun updateControls() {
        publishButton.text = getString(if (publisher.isPublishing) R.string.stop_publishing else R.string.start_publishing)
    }

    private fun startCounting() {
        isCounting = true
        label.text = getString(R.string.publishing_label, 0L.format(), 0L.format())
        label.visibility = View.VISIBLE
        val startedAt = System.currentTimeMillis()
        var updatedAt = System.currentTimeMillis()
        thread = Thread {
            while (isCounting) {
                if (System.currentTimeMillis() - updatedAt > 1000) {
                    updatedAt = System.currentTimeMillis()
                    handler.post {
                        val diff = System.currentTimeMillis() - startedAt
                        val second = diff / 1000 % 60
                        val min = diff / 1000 / 60
                        label.text = getString(R.string.publishing_label, min.format(), second.format())
                    }
                }
            }
        }
        thread?.start()

        drawerThread = Thread{
            var lastTime = System.currentTimeMillis()
            while (isCounting){
                if (System.currentTimeMillis() - lastTime >= Config.DRAWER_TIME){
                    lastTime = System.currentTimeMillis()
                    //网络请求获取数据
                    val result = requestDrawer()
                    if (result != ""){
                        postDraw(result)
                    }
                }
            }
        }

        drawerThread?.start()



    }


    private fun requestDrawer() : String{
        var result: String? = ""
        var `in`: BufferedReader? = null
        try {
            drawerConnection = drawerUrl.openConnection() as HttpURLConnection
            // 设置通用的请求属性
            drawerConnection!!.requestMethod = "GET";
            // 建立实际的连接
            drawerConnection!!.connect()
            // 获取所有响应头字段
            // 定义 BufferedReader输入流来读取URL的响应
            `in` = BufferedReader(
                InputStreamReader(
                    drawerConnection!!.inputStream
                )
            )
            var line: String?
            while (`in`.readLine().also { line = it } != null) {
                result += line
            }
        } catch (e: Exception) {
            println("发送GET请求出现异常！$e")
            e.printStackTrace()
        } // 使用finally块来关闭输入流
        finally {
            try {
                `in`?.close()
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
        Log.e("Get Info", "requestDrawer: $result")



        return result!!
    }

    private fun stopCounting() {
        isCounting = false
        label.text = ""
        label.visibility = View.GONE
        thread?.interrupt()
        drawerThread?.interrupt()
    }

    private fun Long.format(): String {
        return String.format("%02d", this)
    }


    private fun postDraw(data : String){
        thread {
            //请求Data数据，更新绘制图案
            try {
                val jsonData = JSONObject(data)
                Log.e("JSON DATA", "postDraw: $jsonData")
                val success = jsonData.getString("success")
                if (success != "true")
                    return@thread
                val datas = jsonData.getJSONObject("data")
                val list = datas.getString("list")
                val jsonLists = JSONArray(list)
                val dataArray = ArrayList<DataBean>()
                for (i in 0 until jsonLists.length()){
                    val item = jsonLists.getJSONObject(i)
                    when(item.getString("type")){
                        "rect" ->{
                            val rect = RectBean("rect").also {
                                it.maxX = item.getLong("maxX")
                                it.maxY = item.getLong("maxY")
                                it.minX = item.getLong("minX")
                                it.minY = item.getLong("minY")
                            }
                            dataArray.add(rect)
                        }
                        "circle" ->{
                            val circle = CircleBean("circle").also{
                                it.radius = item.getLong("radius")
                                it.x = item.getLong("x")
                                it.y = item.getLong("y")
                            }
                            dataArray.add(circle)
                        }

                        "textbox" ->{
                            val text = TextBean("textbox").also {
                                it.text = item.getString("text")
                                it.x = item.getLong("x")
                                it.y = item.getLong("y")
                            }
                            dataArray.add(text)
                        }

                        "triangle" ->{
                            val triangleBean = TriangleBean("triangle").also{
                                val tlJSON = item.getJSONObject("tl")
                                val blJSON = item.getJSONObject("bl")
                                val brJSON = item.getJSONObject("br")
                                it.blx = blJSON.getLong("x")
                                it.bly = blJSON.getLong("y")

                                it.brx = brJSON.getLong("x")
                                it.bry = brJSON.getLong("y")

                                it.tlx = tlJSON.getLong("x")
                                it.tly = tlJSON.getLong("y")
                            }

                            dataArray.add(triangleBean)

                        }
                    }
                }
                cameraDrawer.updateData(dataArray)
            }catch (e : Exception){
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(applicationContext, "解析服务器绘制数据异常\n 错误:${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }




}




