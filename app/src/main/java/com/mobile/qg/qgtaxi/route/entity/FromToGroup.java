package com.mobile.qg.qgtaxi.route.entity;

import com.amap.api.services.core.LatLonPoint;

/**
 * Created by 11234 on 2018/8/17.
 */
public class FromToGroup {

    private LatLonPoint startPoint;
    private LatLonPoint endPoint;

    private String startName;
    private String endName;

    public FromToGroup start(LatLonPoint startPoint, String startName) {
        this.startName = startName;
        this.startPoint = startPoint;
        return this;
    }

    public FromToGroup end(LatLonPoint endPoint, String endName) {
        this.endName = endName;
        this.endPoint = endPoint;
        return this;
    }

    public boolean available() {
        return startPoint != null && endPoint != null && startName != null && endName != null;
    }

    public void reverse() {
        if (!available()) {
            return;
        }
        LatLonPoint temp = startPoint;
        startPoint = endPoint;
        endPoint = temp;

        String tmp = startName;
        startName = endName;
        endName = tmp;
    }

    public LatLonPoint getStartPoint() {
        return startPoint;
    }

    public LatLonPoint getEndPoint() {
        return endPoint;
    }

    public String getStartName() {
        return startName;
    }

    public String getEndName() {
        return endName;
    }
}
