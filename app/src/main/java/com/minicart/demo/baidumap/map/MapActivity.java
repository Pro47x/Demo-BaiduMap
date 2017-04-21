package com.minicart.demo.baidumap.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.minicart.demo.baidumap.MyApplication;
import com.minicart.demo.baidumap.R;

/**
 * @类名：MapActivity
 * @描述：
 * @创建人：54506
 * @创建时间：2017/4/20 11:42
 * @版本：
 */

public class MapActivity extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    int mapType = 0;
    private BitmapDescriptor mCurrentMarker;

    private BDLocation BDLocation;

    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            BDLocation = location;
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            Log.i("onConnectHotSpotMessage", s);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setBuildingsEnabled(false);

        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setOverlookingGesturesEnabled(false);

        MyApplication.locationService.registerListener(myListener);
        initListener();
        MyApplication.locationService.start();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);


// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.component_album_bg_num);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
        mBaiduMap.setMyLocationConfiguration(config);
    }

    private void initListener() {
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("onMapClick", latLng.toString());
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });


        Button modifyMapType = (Button) findViewById(R.id.modifyMapType);

        modifyMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapType++;
                switch (mapType % 3) {
                    case 0:
                        //普通地图
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        break;

                    case 1:
                        //卫星地图
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                        break;

                    case 2:
                        //空白地图, 基础地图瓦片将不会被渲染。
                        // 在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。
                        // 使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
                        break;

                    default:
                        break;
                }
            }
        });

        CheckBox modifyTraffic = (CheckBox) findViewById(R.id.modifyTraffic);
        modifyTraffic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBaiduMap.setTrafficEnabled(isChecked);
            }
        });

        CheckBox modifyHeat = (CheckBox) findViewById(R.id.modifyHeat);
        modifyHeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBaiduMap.setBaiduHeatMapEnabled(isChecked);
            }
        });

        Button location = (Button) findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modify(BDLocation);
            }
        });


    }

    private void modify(BDLocation location) {
        if (location == null) {
            return;
        }
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        MapStatus mMapStatus = new MapStatus.Builder().target(latlng).zoom(19)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);


        GeoCoder geoCoder = GeoCoder.newInstance();
        ReverseGeoCodeOption options = new ReverseGeoCodeOption().location(latlng);
        // 发起反地理编码请求
        geoCoder.reverseGeoCode(options);
        //设置查询结果监听者
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                }
                if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {

                    //得到位置
                    System.out.println("得到位置" + result.getAddress());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        SharedPreferences sp = getSharedPreferences("location", Context.MODE_PRIVATE);
        if (sp.contains("longitude") && sp.contains("latitude")) {
            BDLocation location = new BDLocation();
            location.setLongitude(Double.parseDouble(sp.getString("longitude", "0")));
            location.setLatitude(Double.parseDouble(sp.getString("latitude", "0")));
            modify(location);
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (BDLocation != null) {
            SharedPreferences location = getSharedPreferences("location", Context.MODE_PRIVATE);
            location.edit()
                    .putString("longitude", BDLocation.getLongitude() + "")
                    .putString("latitude", BDLocation.getLatitude() + "")
                    .apply()
            ;//获取编辑器
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
}
