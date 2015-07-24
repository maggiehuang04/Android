package com.xidian.maggie.weather.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xidian.maggie.weather.model.City;
import com.xidian.maggie.weather.model.CoolWeatherDB;
import com.xidian.maggie.weather.model.County;
import com.xidian.maggie.weather.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.net.*;

import javax.xml.parsers.SAXParserFactory;

/**
 * Created by maggie on 2015/7/23.
 */
public class ParseUtil {

    public synchronized static boolean parseWithSAX(Context context, String response) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader reader = factory.newSAXParser().getXMLReader();
            MyContentHandler handler = new MyContentHandler(context);
            reader.setContentHandler(handler);
            reader.parse(new InputSource(new StringReader(response)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
