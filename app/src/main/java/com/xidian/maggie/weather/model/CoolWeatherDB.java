package com.xidian.maggie.weather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xidian.maggie.weather.db.CoolWeatherHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maggie on 2015/7/22.
 */
public class CoolWeatherDB {
    public static final String DB_NAME = "cool_weather";

    public static final int VERSION = 1;

    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;

//    将构造方法私有化
    private CoolWeatherDB(Context context) {
        CoolWeatherHelper dbHelper = new CoolWeatherHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    //  获取一个CollWeatherDB实例
    public synchronized static CoolWeatherDB getInstance(Context context) {
        if (coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    //传入一个province对象，将该对象存入SQLite数据库
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    //查询SQLite数据库，将所有的province对象放入一个List作为返回
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }


    //传入一个city对象，将该对象存入SQLite数据库
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    //接收一个provinceId参数，根据该参数进行查询SQLite数据库，将所有city对象放入一个List作为返回
    public List<City> loadCities(int provinceId) {
        Log.d("Weather", "LoadCities with the provinceId of " + provinceId);
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id=?",
                new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    //传入一个county对象，将该对象存入SQLite数据库
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            values.put("weather_code", county.getWeatherCode());
            db.insert("County", null, values);
        }
    }

// 接收一个cityId参数，并根据该参数进行查询SQLite数据库，将得到的city对象放入List中作为返回
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_id=?",
                new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d("MyDebug", "LoadCounties--> Got Cursor");
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setWeatherCode(cursor.getString(cursor.getColumnIndex("weather_code")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
