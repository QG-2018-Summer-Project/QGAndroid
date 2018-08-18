package com.mobile.qg.qgtaxi.share;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.io.ByteArrayOutputStream;

/**
 * Created by 93922 on 2018/8/18.
 * 描述：微信分享工具类
 */

public class ShareUtil {

    private static final String TAG = "ShareUtil";
    private static final int THUMB_SIZE = 150;
    private static int mTargetScene = SendMessageToWX.Req.WXSceneSession;

    /*由View进行微信分享*/
    public static void shareByView(View view, IWXAPI api) {
        Log.e(TAG, "shareByView: ");
        /*获取bitmap*/
        Bitmap bmp = createViewBitmap(view);
        share(bmp, api);
    }

    /*由Bitmap进行微信分享*/
    public static void shareByBitmap(Bitmap bmp, IWXAPI api) {
        Log.e(TAG, "shareByBitmap: ");
        share(bmp, api);
    }

    private static void share(Bitmap bmp, IWXAPI api) {
        Log.e(TAG, "shareViewToWeChat: ");
        /*初始化*/
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        /*设置缩略图*/
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = bmpToByteArray(thumbBmp, true);
        /*构造一个Rep*/
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");//transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = mTargetScene;//设置了分享给好友，暂时没弄分享给朋友圈
        /*调用api接口发送数据到微信*/
        api.sendReq(req);
    }

    /*由view生成Bitmap*/
    private static Bitmap createViewBitmap(View v) {
        Log.e(TAG, "createViewBitmap: ");
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private static String buildTransaction(String type) {
        Log.e(TAG, "buildTransaction: " + type);
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        Log.e(TAG, "bmpToByteArray: " + bmp.toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
