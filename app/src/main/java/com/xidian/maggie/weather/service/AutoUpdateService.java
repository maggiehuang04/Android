package com.xidian.maggie.weather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.xidian.maggie.weather.R;
import com.xidian.maggie.weather.activity.WeatherActivity;
import com.xidian.maggie.weather.receiver.AutoUpdateReceiver;
import com.xidian.maggie.weather.util.HttpCallbackListener;
import com.xidian.maggie.weather.util.HttpUtil;
import com.xidian.maggie.weather.util.ParseUtil;

import java.util.Date;

/**
 * Created by maggie on 2015/7/26.
 */
public class AutoUpdateService extends Service {

    private String address;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("Weather", "AutoUpdateService");
                updateWeather();
                alertMessage();
            }
        }).start();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final int eightHours = 8 * 60 * 60 * 1000;
        long triggerTime = SystemClock.elapsedRealtime() + eightHours;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateWeather() {
        Log.d("Weather", "update weather");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code", "");
        if (!TextUtils.isEmpty(weatherCode)) {
            address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
            HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    ParseUtil.handleWeatherResponse(AutoUpdateService.this, response);
                }

                @Override
                public void onError(Exception e) {
                }
            });
        }
    }

    public void alertMessage() {
        Notification notification = new Notification(R.drawable.message,
                "Weather message comes", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, WeatherActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setLatestEventInfo(this, "Weather", "completed auto updating", pendingIntent);
        startForeground(1, notification);
        Log.d("Weather", "alert message---!");
    }
}
