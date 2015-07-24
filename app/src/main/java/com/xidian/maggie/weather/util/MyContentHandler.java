package com.xidian.maggie.weather.util;

import android.content.Context;
import android.util.Log;

import com.xidian.maggie.weather.model.City;
import com.xidian.maggie.weather.model.CoolWeatherDB;
import com.xidian.maggie.weather.model.County;
import com.xidian.maggie.weather.model.Province;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by maggie on 2015/7/24.
 */
public class MyContentHandler extends DefaultHandler {
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String countyCode;
    private String countyName;
    private String weatherCode;
    private int provinceId;
    private int cityId;

    private Context mContext;

    private String tagName;
    private int provinceCount = 0;
    private int cityCount = 0;

    Province province = new Province();
    City city = new City();
    County county = new County();


    public MyContentHandler(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tagName = localName;

        if (tagName.equals("province")) {
            //set provinces
            for (int i = 0; i < attributes.getLength(); i++) {
                switch (attributes.getLocalName(i)) {
                    case "id":
                        provinceCode = attributes.getValue(i);
                        province.setProvinceCode(provinceCode);
                        Log.d("Weather", "ContentHandler--> provinceCode " + provinceCode);
                        provinceId = Integer.parseInt(provinceCode);
                        Log.d("Weather", "ContentHandler--> provinceId " + provinceId);
                        provinceCount++;
                        break;
                    case "name":
                        provinceName = attributes.getValue(i);
                        province.setProvinceName(provinceName);
                        break;
                    default:
                        break;
                }
            }
        }else if (tagName.equals("city")) {
        //set citis
            for (int i = 0; i < attributes.getLength(); i++) {
                switch (attributes.getLocalName(i)) {
                    case "id":
                        cityCode = attributes.getValue(i);
                        city.setCityCode(cityCode);
                        city.setProvinceId(provinceCount);
                        cityId = Integer.parseInt(cityCode);
                        cityCount++;
                        break;
                    case "name":
                        cityName = attributes.getValue(i);
                        city.setCityName(cityName);
                        break;
                    default:
                        break;
                }
            }
        }else if (tagName.equals("county")) {
            //set counties
            for (int i = 0; i < attributes.getLength(); i++) {
                switch (attributes.getLocalName(i)) {
                    case "id":
                        countyCode = attributes.getValue(i);
                        county.setCountyCode(countyCode);
                        county.setCityId(cityCount);
                        break;
                    case "name":
                        countyName = attributes.getValue(i);
                        county.setCountyName(countyName);
                        break;
                    case "weatherCode":
                        weatherCode = attributes.getValue(i);
                        county.setWeatherCode(weatherCode);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(mContext);
        if (localName.equals("province")) {
            coolWeatherDB.saveProvince(province);
            Log.d("Weather", "MyContentHandler-->saveProvince:" + province.getProvinceName());
        }else if (localName.equals("city")) {
            coolWeatherDB.saveCity(city);
            Log.d("Weather", "MyContentHandler-->saveCity: " + city.getCityName());
        }else if (localName.equals("county")) {
            coolWeatherDB.saveCounty(county);
            Log.d("Weather", "MyContentHandler-->saveCounty: " + county.getCountyName());
        }else if (localName.equals("china")) {
            Log.d("Weather", "MyContentHandler-->completed!");
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
    }
}
