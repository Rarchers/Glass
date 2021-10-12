package com.example.glass.utils;

import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;

/**
 * Created by txiaozhe on 12/02/2017.
 */

public class GlobalConfig {

    public static int imgIndex;

    //heatimg图片保存路径
    public static final String IMAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "Heat";

    //work图片保存路径
    public static final String IMAGE_PATHWORK = Environment.getExternalStorageDirectory().toString() + "/";

    //网络状态全局变量
    public static final String MOBILE_CONNECTED = "网络已连接：移动数据";
    public static final String WIFI_CONNECTED = "网络已连接：WIFI";
    public static final String NO_NETWORK_CONNECTED = "网络未连接，将停止数据传输";

    //1 Web服务器IP地址:保定云hd505--118.230.232.221:6001 , 天津车辆tj-221.238.115.56:6001, 西安车辆xa-222.41.193.18, 太原ty , 保定移动211.143.78.218:7044
    public String SERVER_IP = "222.41.193.18:6001";

    // Web服务器IP地址:0- 保定 ，1- 西安， 2-天津，3-太原
    public String SERVER_IP_hdcs = "118.230.232.221:6001";
    public String SERVER_IP_xacl = "222.41.193.18:6001";
    public String SERVER_IP_tjcl = "221.238.115.56:6001";

    public String SERVER_IP_tycl = "199.111.111.18:6001";
    public String SERVER_IP_bdyd = "211.143.78.218:7044";

    public static int NET_TIMEOUT_MS  = 60000;
    public static String NAMESPACE = "http://tempuri.org/";

    /**2  注意：务必是Web服务器IP对应的web service 目录 --利用web service  pcj_yd_signal_ws*/
    public static String WEBSERVICE_URL = "http://211.143.78.218:7044//pcj_cloudtrain_ws//Service1.asmx";
    public static final String METHOD_NAME = "ReceiveHeatImageInfoWithGPS";

    //字段
    public static final String IS_UPLOAD = "isUpload"; //是否已上传
    public static final String PATH = "path"; //手机本地存储地址

    public static final String PHONE_TAG = "teleimei"; //手机串号
    public static final String NFC_TAG = "barcode"; //nfc标签
    public static final String IMAGE = "heatimage"; //图片
    public static final String IMAGE_NAME = "imagename"; //图片名称
    public static final String IMAGE_TIME = "imagetime"; //图片获取时间
    public static final String MAX_TEMP = "maxtemperature"; //最高温度
    public static final String MAX_TEMP_X = "maxtemplocalx"; //最高温度x坐标
    public static final String MAX_TEMP_Y = "maxtemplocaly"; //最高温度y坐标
    public static final String AVERAGE_TEMP = "meantemperature"; //平均温度
    public static final String TELELONG = "telelong";
    public static final String TELELAT = "telelat";
    public static final String IMAGE_INDEX = "photocount";

    //数据库heat_images_info.....................
    public static final String DB_NAME = "heat_images_info.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_COLUMN_NAMES = "isUpload TEXT, teleimei TEXT, barcode TEXT, " +
            "path TEXT, imagename TEXT, imagetime TEXT, maxtemperature TEXT, " +
            "maxtemplocalx TEXT, maxtemplocaly TEXT, meantemperature TEXT)";


    //work info  2018-01-21 /data/data/com.pcj_cloudtrain/pcj_trainheat_config.xml
    //3 note：system install create path= package name
    //public String config_file_nameold = "/data/com.pcj_cloudtrain/pcj_trainheat_config.xml";
    public String config_file_name = Environment.getExternalStorageDirectory().toString() + "/pcj_trainheat_config.xml";

    //4 查看与修改信息
    //1)城市
    public String local_name = "";

    //2)车次信息--发车日期yyyy-MM-dd
    public String train_number = "";
    public String departure_date = "";

    //3)列车供电类型信息TRAIN_POWERTYPE
    public String power_type = "";

    //4) 当前选择执行作业任务
    public String worktype_name = "";

    //5) 当前增换甩挂车辆信息:本车次作业有无，在出乘功能返回信息里判断有无，保存该信息，否则为空或无
    public String carriage_change = "";

    //6)当前版本信息
    public String version = "3.0";


    //6 照片宽-高1280*768，800 * 600
    public int pic_width  = 1024;
    public int pic_height = 768;


    //7 全局移动端地图Key值：
    public String baidumap_keyvalue = "xwrTN5MCSVTxwnucugO7w5li";

    //8 全局作业提醒服务包名, 开始提醒时间----新版本改为 WorkWarnService.this(2018.01.25)
    public String warnservicename  = "com.pcj_cloudtrain.WorkWarnService";
    public String servicestarttime = "";

    //8 日常巡视手工设置: 始发首次作业时间，定时提醒第1次，第2次对应的分钟, 定时作业间隔 3小时
    public int inspectefirsttime = 30;
    public int inspectesettime1  = 30;
    public int inspectesettime2  = 10;
    public int inspectionperiod  = 3*60;


    //9 停靠站作业手工设置: GPS提醒第1次 ; 第2次对应的距离-公里
    public int gpsdistance1 = 30;
    public int gpsdistance2 = 10;
    public int traindelayminute = 0;//客车晚点分钟0-120

    //10. 后台WorkWarnService推送大站提醒, 传回内容用于语音播放 和手工 [确认](2018-12)
    //    用于返回确认 站名称@提醒第1-2次
    public String  stationworkwarncontent  = "";


    //20 内部传递保存--当前日常巡视作业提醒时间2014-09-22 12:05， 定时提醒第几次数2-1-0
    public String inspectewarntime = "";
    public int  inspectewarncount  = -255; //注意为-255，表明已卸载新安装系统


    //21 大站作业-----当前停靠站作业提醒，  当前作业站名称name，  当前作业站顺序order， 提醒次数count=255-2-1-0
    public int stationwarncount   = -255; //注意为-255，表明已卸载新安装系统；2-出乘成功开始GPS提醒判断；0-退乘或休息
    public String stationwarnname  = "";
    public String stationwarnorder = "";
    public String inspectewarntimeGPS = "";//定时上传时间
    public String stationwarngpstype  = "";//T时间和GPS定位

    //21.1.当前到达站次（5Km范围内），与前方特殊作业预提醒站次stationwarnorder（5Km - 10Km 范围内）
    //     当前到达站次和站名称 - 上次暂存过站次和站名称
    public int curstationorder  = -1;
    public int laststationorder = -1;
    public int nextstationorder = -1;

    //22.2 站名称
    public String curstationname  = "";
    public String laststationname = "";
    public String nextstationname = "";


    //23.1语音提醒播报  30s/一次数 ，2次/分钟 20次/10分钟(2019-01-28 )
    public int warnplaycount_max  = 20 * 6 ;
    public int warnplaycount       = 0 ;

    //23.2 出乘情况(时间,结果) , 测酒情况 (时间,结果) (2019-01-28 )
    public String arrivalreportinfo = "";
    public String alcoholtestinfo   = "";


    //21.4 完成/预计下一日常巡视时间
    public String currmidwaypatroltime = "";
    public String nextmidwaypatroltime = "";

    //23.5 完成开柜检查时间---调用InspectionFastLocalSave
    public String curropencabinettime = "";
    public String nextopencabinettime = "";

    //24.1 当前扫描NFC，暂停执行作业；最近作业车厢号便于下拉选择故障
    public String worksave_nfccode = "";
    public String curcarriagecode  = "";

    //24.2 采样频率:200-500-1000ms,  采样时长:1-3-5minute , 传感速度:普通-慢-最慢
    public int simplerate = 500;
    public int simpletimelen = 3;
    public String sensespeed = "普通";

    //24.3 曲线显示控制
    public double axis_max = 15;
    public double axis_min = -15;

    //24.4 直流V600示波显示控制
    public int v600_max = 650;
    public int v600_min = 580;

    //25 上传照片质量:标清-高清--超清,压缩比 limitKB= 200k，400k，800k
    public int upimagecompress = 160;
    public int[] upimage_arrint = {160,400,900};

    //26.本次车厢编号最多20节,出乘时下载，退乘时清空
    public String  mulcarriage = " ; ; ; ; ; ; ; ; ;  ; ; ; ; ; ; ; ; ; ;  ; ; ; ; ; ; ; ; ; ; ";

    //30.百度AI注册，与包名有关pcj_cloudtrain
    public String baiduAI_Apikey    = "y0PHkkkzT8BqboI9BxrMtR7N";
    public String baiduAI_Secretkey = "d9qzsW3ORz5N8Lv0enAkLujpbGQBPFoz";



    //-------main public class--------------------------------------------
    public GlobalConfig() {
        //特别注意： /data/data/com.pcj_cloudtrain/pcj_trainheat_config.xml
        //  文件路径名称 与 package com.pcj_cloudtrain 一致，不可任意更改
        //config_file_name = Environment.getExternalStorageDirectory().toString() + "/pcj_trainheat_config.xml";

    }

    public void readConfig() {
        InputStream iStream = null;
        try {
            File file = new File(config_file_name);

            iStream = new FileInputStream(file);
            // iStream = assetManager.open("my_lic.xml");
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(iStream, "utf-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                String typename = parser.getName();
                switch (type) {
                    case XmlPullParser.START_DOCUMENT:
                        // System.out.println("Start parser");
                        break;
                    case XmlPullParser.START_TAG:

                        //read web SERVER_IP
                        if (typename.equals("SERVER_IP")) {
                            SERVER_IP = parser.nextText();
                        }

                        //read NET_TIMEOUT_MS
                        if (typename.equals("NET_TIMEOUT_MS")) {
                            NET_TIMEOUT_MS = Integer.parseInt(parser.nextText());
                        }

                        //read NAMESPACE
                        if (typename.equals("NAMESPACE")) {
                            NAMESPACE = parser.nextText();
                        }

                        //read webservice_url
                        if (typename.equals("WEBSERVICE_URL")) {
                            WEBSERVICE_URL = parser.nextText();
                        }

                        //read  pic_height， pic_width
                        if (typename.equals("pic_height")) {
                            pic_height = Integer.parseInt(parser.nextText());
                        }

                        if (typename.equals("pic_width")) {
                            pic_width = Integer.parseInt(parser.nextText());
                        }

                        //read cur version
                        if (typename.equals("version")) {
                            version = parser.nextText();
                        }

                        //read cur 本机信息
                        if (typename.equals("local_name")) {
                            local_name = parser.nextText();
                        }

                        //read cur 车次信息--发车日期
                        if (typename.equals("train_number")) {
                            train_number = parser.nextText();
                        }
                        if (typename.equals("departure_date")) {
                            departure_date = parser.nextText();
                        }

                        //read cur 列车供电类型信息
                        if (typename.equals("power_type")) {
                            power_type = parser.nextText();
                        }

                        //read cur 作业类型信息
                        if (typename.equals("worktype_name")) {
                            worktype_name = parser.nextText();
                        }

                        //read cur 本车次作业有无增换甩挂车辆，在出乘功能返回信息里判断有无，保存该信息，否则为空或无
                        if (typename.equals("carriage_change")) {
                            carriage_change = parser.nextText();
                        }

                        //read cur 下一作业定时时长，如30分钟后， 3小时
                        if (typename.equals("inspectionperiod")) {
                            inspectionperiod = Integer.parseInt(parser.nextText());
                        }

                        //read cur 服务开始时间
                        if (typename.equals("servicestarttime")) {
                            servicestarttime = parser.nextText();
                        }

                        //read cur 日常巡视手工设置: 始发首次巡视分钟
                        if (typename.equals("inspectefirsttime")) {
                            inspectefirsttime = Integer.parseInt(parser.nextText());
                        }
                        //read cur 日常巡视手工设置: 定时提醒第1次，第2次对应的分钟
                        if (typename.equals("inspectesettime1")) {
                            inspectesettime1 = Integer.parseInt(parser.nextText());
                        }
                        if (typename.equals("inspectesettime2")) {
                            inspectesettime2 = Integer.parseInt(parser.nextText());
                        }

                        //read cur 停靠站作业手工设置: GPS提醒第1次，第2次对应的距离-公里
                        if (typename.equals("gpsdistance1")) {
                            gpsdistance1 = Integer.parseInt(parser.nextText());
                        }
                        if (typename.equals("gpsdistance2")) {
                            gpsdistance2 = Integer.parseInt(parser.nextText());
                        }

                        //read cur 停靠站晚点时长
                        if (typename.equals("traindelayminute")) {
                            traindelayminute = Integer.parseInt(parser.nextText());
                        }

                        //read cur 内部传递保存--日常巡视的作业提醒时间2014-09-22 10:23， 定时提醒次数2-1-0
                        if (typename.equals("inspectewarntime")) {
                            inspectewarntime = parser.nextText();
                        }
                        if (typename.equals("inspectewarncount")) {
                            inspectewarncount = Integer.parseInt(parser.nextText());
                        }

                        //read cur 内部传递保存--停靠站作业 作业提醒，  当前作业站名称，  当前作业站顺序， 提醒次数2-1-0
                        if (typename.equals("stationwarncount")) {
                            stationwarncount = Integer.parseInt(parser.nextText());
                        }
                        if (typename.equals("stationwarnname")) {
                            stationwarnname = parser.nextText();
                        }
                        if (typename.equals("stationwarnorder")) {
                            stationwarnorder = parser.nextText();
                        }
                        if (typename.equals("inspectewarntimeGPS")) {
                            inspectewarntimeGPS = parser.nextText();
                        }
                        if (typename.equals("stationwarngpstype")) {
                            stationwarngpstype = parser.nextText();
                        }
                        if (typename.equals("stationworkwarncontent")) {
                            stationworkwarncontent = parser.nextText();
                        }

                        //当前暂停执行作业任务2014-10-29
                        if (typename.equals("worksave_nfccode")) {
                            worksave_nfccode = parser.nextText();
                        }

                        //22//采样频率:100-500-1000ms
                        if (typename.equals("simplerate")) {
                            simplerate = Integer.parseInt(parser.nextText());
                        }

                        //23 采样时长:1-3-5
                        if (typename.equals("simpletimelen")) {
                            simpletimelen = Integer.parseInt(parser.nextText());
                        }

                        //24 传感速度:普通-慢
                        if (typename.equals("sensespeed")) {
                            sensespeed = parser.nextText();
                        }

                        //25 曲线显示坐标高低
                        if (typename.equals("axis_max")) {
                            axis_max = Double.parseDouble(parser.nextText());
                        }
                        if (typename.equals("axis_min")) {
                            axis_min = Double.parseDouble(parser.nextText());
                        }

                        //26 V600显示坐标高低
                        if (typename.equals("v600_max")) {
                            v600_max = Integer.parseInt(parser.nextText());
                        }
                        if (typename.equals("v600_min")) {
                            v600_min = Integer.parseInt(parser.nextText());
                        }

                        //27 上传照片质量:普通-标清-高清
                        if (typename.equals("upimagecompress")) {
                            upimagecompress = Integer.parseInt(parser.nextText());
                        }

                        //28 多个车厢号
                        if (typename.equals("mulcarriage")) {
                            mulcarriage = parser.nextText();
                        }

                        //29 最近作业车厢编号
                        if (typename.equals("curcarriagecode")) {
                            curcarriagecode = parser.nextText();
                        }

                        //30 语音提醒播报  30s/一次数 ，2次/分钟 20次/10分钟
                        if (typename.equals("warnplaycount")) {
                            warnplaycount = Integer.parseInt(parser.nextText());
                        }

                        //30.1 出乘情况(时间,结果)
                        if (typename.equals("arrivalreportinfo")) {
                            arrivalreportinfo = parser.nextText();
                        }
                        //30.2 测酒情况 (时间,结果)
                        if (typename.equals("alcoholtestinfo")) {
                            alcoholtestinfo = parser.nextText();
                        }
                        //30.3 当前到达站次
                        if (typename.equals("curstationorder")) {
                            curstationorder = Integer.parseInt(parser.nextText());
                        }
                        if (typename.equals("curstationname")) {
                            curstationname = parser.nextText();
                        }

                        //30.4前次暂存站次
                        if (typename.equals("laststationorder")) {
                            laststationorder = Integer.parseInt(parser.nextText());
                        }
                        if (typename.equals("laststationname")) {
                            laststationname = parser.nextText();
                        }
                        //30.5下一次暂存站次
                        if (typename.equals("nextstationorder")) {
                            nextstationorder = Integer.parseInt(parser.nextText());
                        }
                        if (typename.equals("nextstationname")) {
                            nextstationname = parser.nextText();
                        }

                        //30.4完成下一日常巡视时间
                        if (typename.equals("currmidwaypatroltime")) {
                            currmidwaypatroltime = parser.nextText();
                        }
                        //30.5预计下一日常巡视时间
                        if (typename.equals("nextmidwaypatroltime")) {
                            nextmidwaypatroltime = parser.nextText();
                        }
                        //30.6完成开柜检查时间
                        if (typename.equals("curropencabinettime")) {
                            curropencabinettime = parser.nextText();
                        }
                        //30.7完成开柜检查时间
                        if (typename.equals("nextopencabinettime")) {
                            nextopencabinettime = parser.nextText();
                        }

                        break;
                    case XmlPullParser.END_TAG:

                        break;

                    default:
                        break;
                }

                type = parser.next();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
    }

    public void saveConfig() {

        /** 注意：务必是Web服务器IP对应的wev service 目录 = 利用pcj_cloudtrain_ws  */
        WEBSERVICE_URL = "http://" + SERVER_IP + "//pcj_cloudtrain_ws//Service1.asmx";

        Writer writer = null;
        try {
            File file = new File(config_file_name);
            if (!file.exists()) {

                file.createNewFile();
            }
            writer = new FileWriter(file);


            //2 得到XmlSerializer对象
            XmlSerializer serializer = Xml.newSerializer();
            //2.1 通过写入器接口
            serializer.setOutput(writer);
            //2.2 开始文档标记
            serializer.startDocument("UTF-8", true);
            //2.3 开始标签persons
            serializer.startTag(null, "aConfig");


            //4 开始迭代
            //5 开始标签server_ip
            serializer.startTag(null, "SERVER_IP");
            serializer.text(SERVER_IP);
            serializer.endTag(null, "SERVER_IP");

            //6. 开始标签NET_TIME_OUT_MS
            serializer.startTag(null, "NET_TIMEOUT_MS");
            serializer.text(Integer.toString(NET_TIMEOUT_MS));
            serializer.endTag(null, "NET_TIMEOUT_MS");

            //7. namespace
            serializer.startTag(null, "NAMESPACE");
            serializer.text(NAMESPACE);
            serializer.endTag(null, "NAMESPACE");

            //8. webservice_url
            serializer.startTag(null, "WEBSERVICE_URL");
            serializer.text(WEBSERVICE_URL);
            serializer.endTag(null, "WEBSERVICE_URL");

            //9.1 pic_width
            serializer.startTag(null, "pic_width");
            serializer.text(Integer.toString(pic_width));
            serializer.endTag(null, "pic_width");

            //9.2 pic_height
            serializer.startTag(null, "pic_height");
            serializer.text(Integer.toString(pic_height));
            serializer.endTag(null, "pic_height");

            //10 version
            serializer.startTag(null, "version");
            serializer.text(version);
            serializer.endTag(null, "version");

            //11 本机信息
            serializer.startTag(null, "local_name");
            serializer.text(local_name);
            serializer.endTag(null, "local_name");

            //12 车次信息
            serializer.startTag(null, "train_number");
            serializer.text(train_number);
            serializer.endTag(null, "train_number");

            //13.1 列车供电类型信息
            serializer.startTag(null, "power_type");
            serializer.text(power_type);
            serializer.endTag(null, "power_type");

            //13.2 列车作业类型
            serializer.startTag(null, "worktype_name");
            serializer.text(worktype_name);
            serializer.endTag(null, "worktype_name");

            //14 本车次作业有无增换甩挂车辆，在出乘功能返回信息里判断有无，保存该信息，否则为空或无
            serializer.startTag(null, "carriage_change");
            serializer.text(carriage_change);
            serializer.endTag(null, "carriage_change");

            //15.1  始发首次作业时间30分钟后
            serializer.startTag(null, "inspectefirsttime");
            serializer.text(Integer.toString(inspectefirsttime));
            serializer.endTag(null, "inspectefirsttime");

            //15.2  服务提醒开始时间
            serializer.startTag(null, "servicestarttime");
            serializer.text(servicestarttime);
            serializer.endTag(null, "servicestarttime");

            //15.3 下一作业定时周期时长，如30分钟后， 3小时
            serializer.startTag(null, "inspectionperiod");
            serializer.text(Integer.toString(inspectionperiod));
            serializer.endTag(null, "inspectionperiod");

            //16.1  日常巡视手工设置: 定时提醒第1次
            serializer.startTag(null, "inspectesettime1");
            serializer.text(Integer.toString(inspectesettime1));
            serializer.endTag(null, "inspectesettime1");

            //16.2 日常巡视手工设置: 定时提醒第2次对应的分钟
            serializer.startTag(null, "inspectesettime2");
            serializer.text(Integer.toString(inspectesettime2));
            serializer.endTag(null, "inspectesettime2");

            //17.1 停靠站作业手工设置: GPS提醒第1次
            serializer.startTag(null, "gpsdistance1");
            serializer.text(Integer.toString(gpsdistance1));
            serializer.endTag(null, "gpsdistance1");

            //17.2 停靠站作业手工设置: GPS提醒第2次对应的距离-公里
            serializer.startTag(null, "gpsdistance2");
            serializer.text(Integer.toString(gpsdistance2));
            serializer.endTag(null, "gpsdistance2");

            //18.1 内部传递保存--日常巡视的作业提醒时间2014-09-22 10:23
            serializer.startTag(null, "inspectewarntime");
            serializer.text(inspectewarntime);
            serializer.endTag(null, "inspectewarntime");

            //18.2 内部传递保存--日常巡视的作业提醒次数2-1-0
            serializer.startTag(null, "inspectewarncount");
            serializer.text(Integer.toString(inspectewarncount));
            serializer.endTag(null, "inspectewarncount");

            //19.1 内部传递保存--停靠站作业 作业提醒，  当前作业站名称，  当前作业站顺序， 提醒次数2-1-0
            serializer.startTag(null, "stationwarncount");
            serializer.text(Integer.toString(stationwarncount));
            serializer.endTag(null, "stationwarncount");

            //19.2  内部传递保存--停靠站作业当前站名称
            serializer.startTag(null, "stationwarnname");
            serializer.text(stationwarnname);
            serializer.endTag(null, "stationwarnname");

            //19.3 内部传递保存--停靠站作业当前站顺序
            serializer.startTag(null, "stationwarnorder");
            serializer.text(stationwarnorder);
            serializer.endTag(null, "stationwarnorder");

            //19.5 内部传递保存--gps或时间提醒时间
            serializer.startTag(null, "stationwarngpstype");
            serializer.text(stationwarngpstype);
            serializer.endTag(null, "stationwarngpstype");

            //19.6 内部传递保存--gps的作业提醒时间2014-09-22 10:23
            serializer.startTag(null, "inspectewarntimeGPS");
            serializer.text(inspectewarntimeGPS);
            serializer.endTag(null, "inspectewarntimeGPS");


            //19.4 后台WorkWarnService推送大站提醒, 传回内容用于语音播放 和手工 [确认]
            serializer.startTag(null, "stationworkwarncontent");
            serializer.text(stationworkwarncontent);
            serializer.endTag(null, "stationworkwarncontent");

            //21  当前暂停执行作业任务2014-10-29
            serializer.startTag(null, "worksave_nfccode");
            serializer.text(worksave_nfccode);
            serializer.endTag(null, "worksave_nfccode");


            //22 采样频率:100-500-1000ms
            serializer.startTag(null, "simplerate");
            serializer.text(Integer.toString(simplerate));
            serializer.endTag(null, "simplerate");

            //23 采样时长:1-3-5minute
            serializer.startTag(null, "simpletimelen");
            serializer.text(Integer.toString(simpletimelen));
            serializer.endTag(null, "simpletimelen");

            //24 传感速度:普通-慢-最慢
            serializer.startTag(null, "sensespeed");
            serializer.text(sensespeed);
            serializer.endTag(null, "sensespeed");

            //25 曲线显示控制max
            serializer.startTag(null, "axis_max");
            serializer.text(Double.toString(axis_max));
            serializer.endTag(null, "axis_max");

            //26 曲线显示控制min
            serializer.startTag(null, "axis_min");
            serializer.text(Double.toString(axis_min));
            serializer.endTag(null, "axis_min");

            //27 V600显示控制max
            serializer.startTag(null, "v600_max");
            serializer.text(Integer.toString(v600_max));
            serializer.endTag(null, "v600_max");

            //28 V600显示控制min
            serializer.startTag(null, "v600_min");
            serializer.text(Integer.toString(v600_min));
            serializer.endTag(null, "v600_min");

            //29 上传照片质量:普通-标清-高清
            serializer.startTag(null, "upimagecompress");
            serializer.text(Integer.toString(upimagecompress));
            serializer.endTag(null, "upimagecompress");

            //30 多个车厢号
            serializer.startTag(null, "mulcarriage");
            serializer.text(mulcarriage);
            serializer.endTag(null, "mulcarriage");

            //31 最近作业车厢编号
            serializer.startTag(null, "curcarriagecode");
            serializer.text(curcarriagecode);
            serializer.endTag(null, "curcarriagecode");

            //32 语音提醒播报  30s/一次数 ，2次/分钟 20次/10分钟
            serializer.startTag(null, "warnplaycount");
            serializer.text(Integer.toString(warnplaycount));
            serializer.endTag(null, "warnplaycount");

            //40 出乘情况(时间,结果)
            serializer.startTag(null, "arrivalreportinfo");
            serializer.text(arrivalreportinfo);
            serializer.endTag(null, "arrivalreportinfo");

            //41  测酒情况 (时间,结果)
            serializer.startTag(null, "alcoholtestinfo");
            serializer.text(alcoholtestinfo);
            serializer.endTag(null, "alcoholtestinfo");

            //42  当前到达站次--站名称
            serializer.startTag(null, "curstationorder");
            serializer.text(Integer.toString(curstationorder));
            serializer.endTag(null, "curstationorder");

            serializer.startTag(null, "curstationname");
            serializer.text(curstationname);
            serializer.endTag(null, "curstationname");

            //44  前次暂存站次--站名称
            serializer.startTag(null, "laststationorder");
            serializer.text(Integer.toString(laststationorder));
            serializer.endTag(null, "laststationorder");

            serializer.startTag(null, "laststationname");
            serializer.text(laststationname);
            serializer.endTag(null, "laststationname");

            //44.1  下一次暂存站次--站名称
            serializer.startTag(null, "nextstationorder");
            serializer.text(Integer.toString(nextstationorder));
            serializer.endTag(null, "nextstationorder");

            //44.2
            serializer.startTag(null, "nextstationname");
            serializer.text(nextstationname);
            serializer.endTag(null, "nextstationname");

            //43 预计下一日常巡视时间
            serializer.startTag(null, "currmidwaypatroltime");
            serializer.text(currmidwaypatroltime);
            serializer.endTag(null, "currmidwaypatroltime");

            //44 预计下一日常巡视时间
            serializer.startTag(null, "nextmidwaypatroltime");
            serializer.text(nextmidwaypatroltime);
            serializer.endTag(null, "nextmidwaypatroltime");

            //43 完成开柜检查时间
            serializer.startTag(null, "curropencabinettime");
            serializer.text(curropencabinettime);
            serializer.endTag(null, "curropencabinettime");

            //44 完成开柜检查时间
            serializer.startTag(null, "nextopencabinettime");
            serializer.text(nextopencabinettime);
            serializer.endTag(null, "nextopencabinettime");


            //99  全部结束文档标记end
            serializer.endDocument();

            //100 close
            writer.flush();
            writer.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}