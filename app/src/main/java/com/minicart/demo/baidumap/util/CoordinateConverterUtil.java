package com.minicart.demo.baidumap.util;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

/**
 * 坐标转换的工具类
 *
 * @author 54506
 * @version 1
 * @createTime：2017/4/20 14:51
 * @see <a href="http://lbsyun.baidu.com/index.php?title=androidsdk/guide/coordtrans">官方文档：坐标转换</a>
 */

public class CoordinateConverterUtil {
    private CoordinateConverterUtil() {

    }

    public static LatLng other2BaiduMapLatLng(LatLng sourceLatLng) {
        // 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    public static LatLng gps2BaiduMapLatLng(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

}
