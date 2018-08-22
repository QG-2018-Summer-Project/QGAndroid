package com.mobile.qg.qgtaxi.heatmap;

import android.graphics.Color;

import com.amap.api.maps.model.Gradient;
import com.amap.api.maps.model.HeatmapTileProvider;
import com.amap.api.maps.model.TileOverlayOptions;
import com.amap.api.maps.model.WeightedLatLng;

import java.util.List;

/**
 * Created by 11234 on 2018/8/12.
 * 热力图瓦片工厂
 */
public class HeatMapOverlay {

    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),
            Color.argb(255 / 3 * 2, 0, 255, 0),
            Color.rgb(125, 191, 0),
            Color.rgb(185, 71, 0),
            Color.rgb(255, 0, 0)
    };

    private static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {0.0f,
            0.10f, 0.20f, 0.60f, 1.0f};

    private static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(
            ALT_HEATMAP_GRADIENT_COLORS, ALT_HEATMAP_GRADIENT_START_POINTS);

    public static TileOverlayOptions getHeatMapOverlay(List<HeatMapLatLng> heatMapLatLngList) {

        List<WeightedLatLng> weightedLatLngList = WeightedLatLngFactory.INSTANCE.getWeightedList(heatMapLatLngList);

        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .weightedData(weightedLatLngList)
                .gradient(ALT_HEATMAP_GRADIENT)
                .build();

        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
        tileOverlayOptions.tileProvider(provider);
        return tileOverlayOptions;

    }


}
