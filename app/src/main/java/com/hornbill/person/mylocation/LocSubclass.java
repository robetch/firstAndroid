package com.hornbill.person.mylocation;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/6/29.
 */

public class LocSubclass extends AppCompatActivity {
    private LocationManager locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
    boolean mbUpdate = false;
    private Location mlocation;

    //启动模拟位置服务
    public boolean initLocation() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        try {
            //如果未开启模拟位置服务，则添加模拟位置服务
            locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, false, false, false, true, true, true, 0, 5);
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
            //asynTaskUpdateCallBack();
        } catch (Exception e) {
            return false;
        } finally{
            stopMockLocation();
        }
        return true;
    }

    //停止模拟位置服务
    public void stopMockLocation() {
        mbUpdate = false;
        if (locationManager != null) {
            try {
                locationManager.clearTestProviderEnabled(LocationManager.GPS_PROVIDER);
                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                Log.e("GPS", e.toString());
            }
        }
    }

    private Bundle bundle = new Bundle();
    //double testData = 0.0;
    double latitude = 22;
    double longitude = 113;

    public String asynTaskUpdateCallBack() {
        mbUpdate = true;
        String msg = "";
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (mbUpdate) {
                    //测试的location数据
                    mlocation.setLongitude(latitude);   // 维度（度）
                    mlocation.setLatitude(longitude);   // 经度（度）
                    mlocation.setAltitude(30);          // 高程（米）
                    mlocation.setTime(System.currentTimeMillis());   // 本地时间
                    mlocation.setBearing((float) 1.2);  // 方向（度）
                    mlocation.setSpeed((float) 1.2);    // 速度（米/秒）
                    mlocation.setAccuracy((float) 1.2); // 精度（米）

                    //额外的自定义数据，使用bundle来传递
                    bundle.putString("test1", "666");
                    bundle.putString("test2", "66666");
                    mlocation.setExtras(bundle);
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= 17)
                            mlocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                        locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER, 100, bundle, System.currentTimeMillis());
                        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mlocation);

                        Thread.sleep(1000);
                    } catch (Exception e) {
                        msg = e.toString();
                        //return;
                    }
 //               }
 //           }
 //       }).start();
        return msg;
    }

}
