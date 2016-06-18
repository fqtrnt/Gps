package com.example.fqtrnt.gps2;

import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static android.location.LocationManager.GPS_PROVIDER;

public class MainActivity extends AppCompatActivity {

    private TextView tv_satellites;
    private TextView tv_gps;
    private Button bt_Quit;
    private LocationManager locationManager;
    private StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_satellites = (TextView) this.findViewById(R.id.satellites);
        tv_gps = (TextView) this.findViewById(R.id.gps);
        bt_Quit = (Button) this.findViewById(R.id.quit);
        openGPSSettings();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        tv_gps.setText(updateMsg(location));

        LocationListener ll = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                String locInfo = updateMsg(location);
                tv_gps.setText(null);
                tv_gps.setText(locInfo);
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };

        bt_Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });
        try {
            locationManager.addGpsStatusListener(statusListener);
            locationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 0,  ll, Looper.getMainLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private String updateMsg(Location loc) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sb = null;
        sb = new StringBuilder(sdf.format(new Date())).append("位置信息\n");
        if (loc != null) {
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();

            sb.append("纬度" + lat + "\n经度" + lng);

            if (loc.hasAccuracy()) {
                sb.append("\n精度" + loc.getAccuracy());
            }

            if (loc.hasAltitude()) {
                sb.append("\n海拔" + loc.getAltitude() + "m");
            }

            if (loc.hasBearing()) {
                sb.append("\n方向" + loc.getBearing());
            }

            if (loc.hasSpeed()) {
                if (loc.getSpeed() * 3.6 < 5) {
                    sb.append("\n速度0.0km/h");
                } else {
                    sb.append("\n速度" + loc.getSpeed() * 3.6 + "km/h");
                }

            }
        } else {
            sb.append("没有位置信息!");
        }

        return sb.toString();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gps, menu);
        return true;
    }

    private void openGPSSettings() {
        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS模块正常", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        startActivityForResult(intent, 0);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            MainActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    /** 卫星状态监听器 */
    private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>();
    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) { // GPS状态变化时的回调，如卫星数
            LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
            GpsStatus status = locationManager.getGpsStatus(null); // 取当前状态
            String satelliteInfo = updateGpsStatus(event, status);
            tv_satellites.setText(null);
            tv_satellites.setText(satelliteInfo);
        }
    };

    private String updateGpsStatus(int event, GpsStatus status) {
        StringBuilder sb2 = new StringBuilder("");
        if (status == null) {
            sb2.append("搜索到卫星个数" +0);
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            int maxSatellites = status.getMaxSatellites();
            Iterator<GpsSatellite> it = status.getSatellites().iterator();
            numSatelliteList.clear();
            int count = 0;
            while (it.hasNext() && count <= maxSatellites) {
                GpsSatellite s = it.next();
                numSatelliteList.add(s);
                count++;
            }
            sb2.append("搜索到卫星个数" + numSatelliteList.size());
        }

        return sb2.toString();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
