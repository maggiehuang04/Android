package com.xidian.maggie.weather.model;

/**
 * Created by maggie on 2015/7/22.
 */
public class County {
    private int id;
    private String countyName;
    private String countyCode;
    private String weatherCode;



    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }
    public String getWeatherCode() { return weatherCode;    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
