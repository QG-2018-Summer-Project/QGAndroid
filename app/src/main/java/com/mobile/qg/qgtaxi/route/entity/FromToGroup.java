package com.mobile.qg.qgtaxi.route.entity;

import com.amap.api.services.core.LatLonPoint;

import lombok.Data;

/**
 * Created by 11234 on 2018/8/17.
 */
@Data
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

    public boolean reverse() {
        if (!available()) {
            return false;
        }
        LatLonPoint temp = startPoint;
        startPoint = endPoint;
        endPoint = temp;

        String tmp = startName;
        startName = endName;
        endName = tmp;

        return true;
    }

}
