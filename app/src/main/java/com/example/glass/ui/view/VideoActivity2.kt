package com.example.glass.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.example.glass.R
import com.example.glass.component.Config
import com.example.glass.component.video.CameraDrawer
import com.example.glass.component.video.drawerbean.*
import com.pedro.rtplibrary.rtmp.RtmpCamera1
import net.ossrs.rtmp.ConnectCheckerRtmp
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

class VideoActivity2 : AppCompatActivity(), ConnectCheckerRtmp, View.OnClickListener, SurfaceHolder.Callback {
    private var rtmpCamera1: RtmpCamera1? = null
    private var button: Button? = null
    private var currentDateAndTime = ""
    private var drawerThread : Thread? = null
    private var isCounting = false
    private var drawerConnection : HttpURLConnection? = null
    private var drawerUrl : URL = URL(Config.VIDEO_FETCH_PAINT)
    private lateinit var cameraDrawer : CameraDrawer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_vedio2)
        val surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        button = findViewById(R.id.b_start_stop)
        button!!.setOnClickListener(this)
        cameraDrawer = findViewById(R.id.cameraDrawer)
        rtmpCamera1 = RtmpCamera1(surfaceView, this)
        rtmpCamera1!!.setReTries(10)
        surfaceView.holder.addCallback(this)
        rtmpCamera1!!.switchCamera()


        drawerThread = Thread{
            while (true){
                var lastTime = System.currentTimeMillis()
                while (isCounting){
                    if (System.currentTimeMillis() - lastTime >= Config.DRAWER_TIME){
                        lastTime = System.currentTimeMillis()
                        //????????????????????????
                        Log.e("Drawer", "onCreate: isCounting $isCounting" )
                        val result = requestDrawer()
                        if (result != "" && isCounting){
                            postDraw(result)
                        }
                    }
                }
            }
        }

        drawerThread?.start()



    }


    override fun onConnectionSuccessRtmp() {
        runOnUiThread {
            Toast.makeText(
                this@VideoActivity2,
                "Connection success",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onConnectionFailedRtmp(reason: String) {
        runOnUiThread {
            if (rtmpCamera1!!.reTry(5000, reason)) {
                Toast.makeText(this@VideoActivity2, "??????", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this@VideoActivity2,
                    "????????????. $reason",
                    Toast.LENGTH_SHORT
                ).show()
                rtmpCamera1!!.stopStream()
                button!!.setText("????????????")

            }

        }
    }

    override fun onNewBitrateRtmp(bitrate: Long) {}

    override fun onDisconnectRtmp() {
        runOnUiThread {
            Toast.makeText(this@VideoActivity2, "????????????", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAuthErrorRtmp() {
        runOnUiThread {
            Toast.makeText(this@VideoActivity2, "Auth error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAuthSuccessRtmp() {
        runOnUiThread {
            Toast.makeText(this@VideoActivity2, "Auth success", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.b_start_stop -> if (!rtmpCamera1!!.isStreaming) {
                if (rtmpCamera1!!.isRecording
                    || rtmpCamera1!!.prepareAudio() && rtmpCamera1!!.prepareVideo()
                ) {
                    button!!.setText("????????????")
                    isCounting = true
                    rtmpCamera1!!.startStream(Config.VIDEO_PUSH)
                } else {

                    Toast.makeText(
                        this, "Error preparing stream, This device cant do it",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                button!!.setText("????????????")
                isCounting = false
                rtmpCamera1!!.stopStream()
            }

            else -> {
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        rtmpCamera1!!.startPreview()
    }


    override fun surfaceDestroyed(holder: SurfaceHolder) {

        if (rtmpCamera1!!.isStreaming) {
            rtmpCamera1!!.stopStream()
            button!!.text ="????????????"
        }
        rtmpCamera1!!.stopPreview()
    }

    private fun requestDrawer() : String{
        var result: String? = ""
        var `in`: BufferedReader? = null
        try {
            drawerConnection = drawerUrl.openConnection() as HttpURLConnection
            // ???????????????????????????
            drawerConnection!!.requestMethod = "GET";
            // ?????????????????????
            drawerConnection!!.connect()
            // ???????????????????????????
            // ?????? BufferedReader??????????????????URL?????????
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
            println("??????GET?????????????????????$e")
            e.printStackTrace()
        } // ??????finally?????????????????????
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


    private fun postDraw(data : String){
        thread {
            //??????Data???????????????????????????
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
                    Toast.makeText(applicationContext, "?????????????????????????????????\n ??????:${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

}