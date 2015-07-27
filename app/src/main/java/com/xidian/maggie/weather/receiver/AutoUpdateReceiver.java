package com.xidian.maggie.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xidian.maggie.weather.service.AutoUpdateService;

/**
 * Created by maggie on 2015/7/26.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        启动Service，与之形成一个定时的无限循环调用
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
