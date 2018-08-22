package com.mobile.qg.qgtaxi.search;

import com.amap.api.services.core.LatLonPoint;

import lombok.Builder;
import lombok.Data;

/**
 * Created by 11234 on 2018/8/17.
 */
@Data
@Builder
public class SearchEvent {

    public enum Purpose {
        START, END
    }

    private Purpose purpose;
    private String name;
    private LatLonPoint point;

}
