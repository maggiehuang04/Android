package com.xidian.maggie.weather.util;

/**
 * Created by maggie on 2015/7/23.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);

}
