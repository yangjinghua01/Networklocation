package com.rgsc.iot.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {

    private TextView text;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);

        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.LOCATION_ACTION);
        this.registerReceiver(new LocationBroadcastReceiver(), filter);

        // 启动服务
        Intent intent = new Intent();
        intent.setClass(this, LocationSvc.class);
        startService(intent);

        // 等待提示
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在定位...");
        dialog.setCancelable(true);
        dialog.show();
        LocationManager locationManager =  (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> allProviders = locationManager.getAllProviders();
        Log.e("TAG", "onCreate: "+allProviders.toString() );
        for (int i = 0; i < allProviders.size(); i++) {
            String s = allProviders.get(i);
        }
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.LOCATION_ACTION))
                return;
            String locationInfo = intent.getStringExtra(Common.LOCATION);
            double latitude = Double                 //截取经纬度转换为double型
                    .parseDouble(locationInfo.substring(17, 26));
            double longitude = Double.parseDouble(locationInfo
                    .substring(27, 37));
            Log.e("yjh", "经度: "+latitude+"维度"+longitude );
            Toast.makeText(MainActivity.this, "经度: "+latitude+"维度"+longitude, Toast.LENGTH_SHORT).show();
            text.setText(getaddress(latitude, longitude));
            dialog.dismiss();
            MainActivity.this.unregisterReceiver(this);// 不需要时注销
        }

        public String getaddress(double latitude, double longitude) {
            String cityName = "";
            List<Address> addList = null;
            Geocoder ge = new Geocoder(MainActivity.this);
            try {
                addList = ge.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addList != null && addList.size() > 0) {
                for (int i = 0; i < addList.size(); i++) {
                    Address ad = addList.get(i);
                    cityName += ad.getCountryName() + ";" + ad.getLocality();
                }
            }
            Log.i("yjh", "city:" + cityName);
            return cityName;
        }
    }
}