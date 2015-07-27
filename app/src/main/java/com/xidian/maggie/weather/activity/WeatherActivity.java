package com.xidian.maggie.weather.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xidian.maggie.weather.R;
import com.xidian.maggie.weather.db.CoolWeatherHelper;
import com.xidian.maggie.weather.model.CoolWeatherDB;
import com.xidian.maggie.weather.service.AutoUpdateService;
import com.xidian.maggie.weather.util.HttpCallbackListener;
import com.xidian.maggie.weather.util.HttpUtil;
import com.xidian.maggie.weather.util.ParseUtil;


/**
 * Created by maggie on 2015/7/25.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;

    private String cityName;
    private String temp1;
    private String temp2;
    private String weatherDesp;
    private String publishTime;
    private String currentDate;

    String weatherCode;
    SQLiteDatabase db;

    private Button switchCity;
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Weatehr", "WeatherActivity--> onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);

        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);

//      得到由ChooseActivity传入的countycode
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            publishText.setText("Loading...");
            weatherInfoLayout.setVisibility(View.VISIBLE);
            cityNameText.setVisibility(View.VISIBLE);
            queryWeatherCode(countyCode);
        }


    }

    @Override
    protected void onResume() {
        Log.d("Weather", "WeatherActivity --> onResume");
        super.onResume();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1);
        showWeather();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city:
                //用户点击切换按钮，进入ChooseActivity，并传入参数“from_weather_activity”
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("Refreshing...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
                    queryFromServer(address);
                }
                break;
            default:
                break;
        }
    }

//    根据countyCode在SQLite数据库中查询对应的weathercode
    private void queryWeatherCode(String countyCode) {
        CoolWeatherHelper dbHelper = new CoolWeatherHelper(this, "cool_weather", null, 1);
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("County", null, "county_code=?", new String[]{countyCode}, null, null, null);
        if (cursor.moveToFirst())
            do {
                weatherCode = cursor.getString(cursor.getColumnIndex("weather_code"));
            }
            while (cursor.moveToNext());
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address);
    }

//    根据网址到天气服务器获取天气信息
    public void queryFromServer(final String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if (!TextUtils.isEmpty(response)) {
                    ParseUtil.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("Failed to load");
                    }
                });

            }
        });
    }

//    从SharedReferences里取出天气信息，并进行显示
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        cityName = prefs.getString("city_name", "");
        temp1 = prefs.getString("temp1", "");
        temp2 = prefs.getString("temp2", "");
        weatherDesp = prefs.getString("weather_desp", "");
        publishTime = prefs.getString("publish_time", "");
        currentDate = prefs.getString("current_date", "");

        cityNameText.setText(cityName);
        temp1Text.setText(temp1);
        temp2Text.setText(temp2);
        weatherDespText.setText(weatherDesp);
        publishText.setText("Today " + publishTime + " updated");
        currentDateText.setText(currentDate);

        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

    }
}
