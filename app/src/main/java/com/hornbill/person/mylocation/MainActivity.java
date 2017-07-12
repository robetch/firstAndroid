package com.hornbill.person.mylocation;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    String msg = "";
    private int INTERVEL = 1000;
    MockLocationProvider mock;
    LocationManager locationManager;
    Location mLocation;
    TextView mainText3;
    TextView mainText2;
    String providerStr = LocationManager.GPS_PROVIDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*//////进行电话的拨号
        Uri uri = Uri.parse("tel:10086");
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);

        */

        /*测试用到的语句*/
        boolean hasAddTestProvider = false;
        boolean canMockPosition = (Settings.Secure.getInt(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0)
                || Build.VERSION.SDK_INT > 22;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        if (canMockPosition && hasAddTestProvider == false) {
            try {

                LocationProvider provider = locationManager.getProvider(providerStr);
                if (provider != null) {
                    locationManager.addTestProvider(
                            provider.getName()
                            , provider.requiresNetwork()
                            , provider.requiresSatellite()
                            , provider.requiresCell()
                            , provider.hasMonetaryCost()
                            , provider.supportsAltitude()
                            , provider.supportsSpeed()
                            , provider.supportsBearing()
                            , provider.getPowerRequirement()
                            , provider.getAccuracy());
                } else {
                    locationManager.addTestProvider(
                            providerStr
                            , true, true, false, false, true, true, true
                            , Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                }
                locationManager.setTestProviderEnabled(providerStr, true);
                locationManager.setTestProviderStatus(providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis());

                // 模拟位置可用
                hasAddTestProvider = true;
                canMockPosition = true;
            } catch (SecurityException e) {
                canMockPosition = false;
                msg += e.toString();
                Log.e("GPS", e.toString());
            }
        }


        /*
        结束
         */


        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        mainText3 = (TextView) findViewById(R.id.textView3);
        //mainText3.setText(R.string.hello);
        if (hasAddTestProvider && canMockPosition) {
            mainText3.setText("可以虚拟定位");
        } else {
            mainText3.setText("不能虚拟定位" + msg);
        }
        mainText2 = (TextView) findViewById(R.id.textView2);

        //Log.d(msg, "The onCreate() event");

        /*
        Button bt1 = (Button) findViewById(R.id.buttonCommit);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //RunnableMockLocation loc = new RunnableMockLocation();
                //loc.run();

                TextView mainText3 = (TextView) findViewById(R.id.textView3);
                mainText3.setText("tmp");
                //LocSubclass loc = new LocSubclass();
                //mainText3.setText(loc.asynTaskUpdateCallBack());
            }
        });
        Button bt2 = (Button) findViewById(R.id.buttonReset);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView mainText3 = (TextView) findViewById(R.id.textView3);
                mainText3.setText("结束");
            }
        });
        */

        if (!isGpsAble(locationManager)) {
            msg += "open GPS";
            openGPS2();
        }
        //从GPS获取最近的定位信息
        try{
            //Location lc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //updateShow(lc);
            updateShow(mLocation);
        //设置间隔两秒获得一次GPS定位信息
            mainText2.setText("test");
            locationManager.requestLocationUpdates(providerStr, 2000, 8, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // 当GPS定位信息发生改变时，更新定位
                    msg += "locathin";
                    updateShow(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    msg += "ejijfea";
                }

                @Override
                public void onProviderEnabled(String provider) {
                    // 当GPS LocationProvider可用时，更新定位
                    msg += "isProvider";
                    try{
                        updateShow(locationManager.getLastKnownLocation(provider));
                    }catch (SecurityException e){
                        msg += e.toString();
                    }
                }

                @Override
                public void onProviderDisabled(String provider) {
                    msg += "noProvider";
                    updateShow(null);
                }
            });

        }catch (SecurityException e){
            msg += e.toString();
            mainText2.setText(e.toString());
        }finally {
            msg += "end;";
        }

    }


    //定义一个更新显示的方法
    private void updateShow(Location location) {
        if (location != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("当前的位置信息：\n");
            sb.append("精度：" + location.getLongitude() + "\n");
            sb.append("纬度：" + location.getLatitude() + "\n");
            sb.append("高度：" + location.getAltitude() + "\n");
            sb.append("速度：" + location.getSpeed() + "\n");
            sb.append("方向：" + location.getBearing() + "\n");
            sb.append("定位精度：" + location.getAccuracy() + "\n");
            mainText2.setText(sb.toString());
        } //else mainText2.setText("No Gps"+msg);
    }


    private boolean isGpsAble(LocationManager lm) {
        return lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ? true : false;
    }


    //打开设置页面让用户自己设置
    private void openGPS2() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0);
    }




    /*设置位置的另一种方法*/

    protected void onDestroy() {
        mock.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
