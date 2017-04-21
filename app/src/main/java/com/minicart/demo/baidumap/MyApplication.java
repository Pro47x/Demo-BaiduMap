package com.minicart.demo.baidumap;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.minicart.demo.baidumap.service.LocationService;

/**
 * @类名：MyApplication
 * @描述：
 * @创建人：54506
 * @创建时间：2017/4/19 16:14
 * @版本：
 */

public class MyApplication extends Application {

    public static LocationService locationService;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        locationService = new LocationService(this);
    }
}
