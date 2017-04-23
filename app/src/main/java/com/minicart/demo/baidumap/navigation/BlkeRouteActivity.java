package com.minicart.demo.baidumap.navigation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.minicart.demo.baidumap.R;
import com.minicart.demo.baidumap.util.BikingRouteOverlay;
import com.minicart.demo.baidumap.util.OverlayManager;

public class BlkeRouteActivity extends AppCompatActivity {

    MapView mMapView;
    BaiduMap mBaidumap;

    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    String city = "长沙";
    String startNodeStr = "他城";
    String endNodeStr = "汽车南站";

    BikingRouteResult nowResultbike = null;

    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    RouteLine route = null;
    OverlayManager routeOverlay = null;

    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        // 初始化地图
        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaidumap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult result) {
                //步行路线规划
                Log.d(TAG, "onGetWalkingRouteResult");
            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult result) {
                //换乘路线规划
                Log.d(TAG, "onGetTransitRouteResult");
            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
                //跨城路线规划
                Log.d(TAG, "onGetMassTransitRouteResult");
            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                //驾车路线规划
                Log.d(TAG, "onGetDrivingRouteResult");
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
                //室内路线规划
                Log.d(TAG, "onGetIndoorRouteResult");
            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult result) {
                //自行车路线规划
                Log.d(TAG, "onGetBikingRouteResult");
                if (result == null) {
                    return;
                }
                if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    route = result.getRouteLines().get(0);
                    if (route == null) {
                        return;
                    }
                    nodeIndex = -1;
                    nowResultbike = result;
                    BikingRouteOverlay overlay = new BikingRouteOverlay(mBaidumap);
                    routeOverlay = overlay;
                    mBaidumap.setOnMarkerClickListener(overlay);
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
            }
        });
    }

    public void bikePlan(View view) {
        // 处理搜索按钮响应
        // 设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = PlanNode.withCityNameAndPlaceName(city, startNodeStr);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName(city, endNodeStr);

        BikingRoutePlanOption from = (new BikingRoutePlanOption())
                .from(stNode);
        mSearch.bikingSearch(from.to(enNode)
        );
    }
}
