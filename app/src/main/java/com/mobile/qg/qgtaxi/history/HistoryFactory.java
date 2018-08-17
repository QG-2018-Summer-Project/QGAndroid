package com.mobile.qg.qgtaxi.history;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11234 on 2018/8/15.
 * 工厂类/转换Tip和History
 */
public class HistoryFactory {

    public static History formTip(Tip tip) {

        String id = tip.getPoiID();
        String name = tip.getName();
        String address = tip.getAddress();
        LatLonPoint point = tip.getPoint();
        double lat = point.getLatitude();
        double lng = point.getLongitude();

        return new History(id, name, address, lat, lng);

    }

    static List<Tip> fromHistory(List<History> histories) {

        List<Tip> tips = new ArrayList<>();
        for (History history : histories) {
            Tip tip = fromHistory(history);
            tips.add(tip);
        }

        return tips;
    }

    static Tip fromHistory(History history) {

        Tip tip = new Tip();
        tip.setID(history.getId());
        tip.setName(history.getName());
        tip.setAdcode(history.getAddress());
        tip.setPostion(new LatLonPoint(history.getLat(), history.getLng()));

        return tip;
    }

}
