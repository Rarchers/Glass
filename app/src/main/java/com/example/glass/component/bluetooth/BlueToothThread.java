package com.example.glass.component.bluetooth;

import static android.content.Context.BLUETOOTH_SERVICE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.BeaconManagerListener;
import com.sensoro.cloud.SensoroManager;

import java.util.ArrayList;

public class BlueToothThread {
    /** 5 Beacon Manager lister,use it to listen the appearence, disappearence and updating of the beacons.*/
    BeaconManagerListener beaconManagerListener;

    /** 6 Sensoro Manager*/
    SensoroManager sensoroManager;
    Handler handler;
    //7. 设备的蓝牙打开
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;

    //8. iBeacon 协议：SN,  Mac地址, 信号强度, 距离（米）, 范围（很远，附近，很近，未知）
    private String sensoroNumber = "";
    private String lastsensoroNumber = "";
    private int    getrssi = 0;
    private double getAccuracy = 0;

    //9. iBeacon协议中 电池电量, major, minor, 芯片温度
    private int getbatteryLevel = 0;

    //10.
    SimpleDateFormat dateformat ;
    private String curDateTime= "";


    public BlueToothThread(Context context){
//8. sensoroManager本身引用
        sensoroManager = SensoroManager.getInstance(context.getApplicationContext());
        sensoroManager.setCloudServiceEnable(false);
        try{
            sensoroManager.startService();
        }catch (Exception e){

        }

        //9. Sensoro定义
        initSensoroListener();
        sensoroManager.setBeaconManagerListener(beaconManagerListener);

        //10. bluetooth定义方法--1)ibeacon效果好定义， 与UUID有关   2)通用定义识别慢getDefaultAdapter
        bluetoothManager = (BluetoothManager)context.getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }


    //蓝牙打开
    private boolean isBlueEnable() {

        boolean status = bluetoothAdapter.isEnabled();

        //2. ok
        if (status) {
            return status;
        }

        //3. 蓝牙打开
        //3.1 询问用户选择--允许YES
        ////Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ////startActivity(bluetoothIntent);

        //3.2 区别：enable()开启无需询问用户，需要权限BLUETOOH_ADMIN
        bluetoothAdapter.enable();
        return status;
    }


    //通过实现并设置 BeaconManagerListener 接口，来检测 Beacon 的出现,显示以及更新
    //接口中，传感器信息更新频率为 1 秒；发现一个新的传感器后，如果在 8 秒内没有再次扫描到这个设备，则会回调传感器消失
    private void initSensoroListener() {
        beaconManagerListener = new BeaconManagerListener() {

            public void onUpdateBeacon(final ArrayList<Beacon> arg0) {
                // 传感器信息更新

            }

            public void onNewBeacon(Beacon arg0) {
                // 发现一个新的传感器

                //SN，设备唯一标识 = 0117C59A6CF
                sensoroNumber = arg0.getSerialNumber();

                //iBeacon协议中的信号强度 信息 -30
                getrssi = arg0.getRssi();

                //iBeacon协议中的距离（米）0.5-70m
                getAccuracy = arg0.getAccuracy();

                //iBeacon协议中的电池电量
                getbatteryLevel = arg0.getBatteryLevel();

                //10.当前屏幕显示
               // handler.post(runnableUi_display);
            }

            public void onGoneBeacon(Beacon arg0) {
                // 一个传感器消失
                //sensoroNumber = "消失...";

               // handler.post(runnableUi_clear);
            }
        };
    }

    /** Start sensoro service. */
    private void startSensoroService() {
        // set a tBeaconManagerListener.
        sensoroManager.setBeaconManagerListener(beaconManagerListener);
        try {
            sensoroManager.startService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
