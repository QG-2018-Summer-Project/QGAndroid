package com.mobile.qg.qgtaxi.route;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.mobile.qg.qgtaxi.R;

import java.util.ArrayList;
import java.util.List;

public class RouteOverlay {
    private List<Marker> stationMarkers = new ArrayList<>();
    private List<Polyline> allPolyLines = new ArrayList<>();
    Marker startMarker;
    Marker endMarker;
    LatLng startPoint;
    LatLng endPoint;
    AMap mAMap;
    private Bitmap startBit, endBit, busBit, walkBit, driveBit;
    boolean nodeIconVisible = true;

    RouteOverlay() {
    }

    /**
     * 去掉BusRouteOverlay上所有的Marker。
     *
     * @since V2.1.0
     */
    public void removeFromMap() {
        if (startMarker != null) {
            startMarker.remove();
        }
        if (endMarker != null) {
            endMarker.remove();
        }
        for (Marker marker : stationMarkers) {
            marker.remove();
        }
        for (Polyline line : allPolyLines) {
            line.remove();
        }
        destroyBit();
    }

    private void destroyBit() {
        if (startBit != null) {
            startBit.recycle();
            startBit = null;
        }
        if (endBit != null) {
            endBit.recycle();
            endBit = null;
        }
        if (busBit != null) {
            busBit.recycle();
            busBit = null;
        }
        if (walkBit != null) {
            walkBit.recycle();
            walkBit = null;
        }
        if (driveBit != null) {
            driveBit.recycle();
            driveBit = null;
        }
    }

    /**
     * 给起点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     *
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    private BitmapDescriptor getStartBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_start);
    }

    /**
     * 给终点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     *
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    private BitmapDescriptor getEndBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_end);
    }


    BitmapDescriptor getDriveBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_car);
    }

    void addStartAndEndMarker() {
        startMarker = mAMap.addMarker((new MarkerOptions())
                .position(startPoint).icon(getStartBitmapDescriptor())
                .title("\u8D77\u70B9"));
        endMarker = mAMap.addMarker((new MarkerOptions()).position(endPoint)
                .icon(getEndBitmapDescriptor()).title("\u7EC8\u70B9"));
    }

    /**
     * 移动镜头到当前的视角。
     *
     * @since V2.1.0
     */
    public void zoomToSpan() {
        if (startPoint != null) {
            if (mAMap == null) {
                return;
            }
            try {
                LatLngBounds bounds = getLatLngBounds();
                mAMap.animateCamera(CameraUpdateFactory
                        .newLatLngBounds(bounds, 100));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    protected LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(startPoint.latitude, startPoint.longitude));
        b.include(new LatLng(endPoint.latitude, endPoint.longitude));
        return b.build();
    }

    /**
     * 路段节点图标控制显示接口。
     *
     * @param visible true为显示节点图标，false为不显示。
     * @since V2.3.1
     */
    public void setNodeIconVisibility(boolean visible) {
        try {
            nodeIconVisible = visible;
            if (this.stationMarkers != null && this.stationMarkers.size() > 0) {
                for (int i = 0; i < this.stationMarkers.size(); i++) {
                    this.stationMarkers.get(i).setVisible(visible);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    void addStationMarker(MarkerOptions options) {
        if (options == null) {
            return;
        }
        Marker marker = mAMap.addMarker(options);
        if (marker != null) {
            stationMarkers.add(marker);
        }

    }

    void addPolyLine(PolylineOptions options) {
        if (options == null) {
            return;
        }
        Polyline polyline = mAMap.addPolyline(options);
        if (polyline != null) {
            allPolyLines.add(polyline);
        }
    }

    protected float getRouteWidth() {
        return 18f;
    }

    int getDriveColor() {
        return Color.parseColor("#537edc");
    }

}
