package com.mobile.qg.qgtaxi.wxapi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.mobile.qg.qgtaxi.R;
import com.mobile.qg.qgtaxi.share.WeChatConstant;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/*微信分享回调的Activity*/

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXEntryActivity";
    private IWXAPI wxAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        wxAPI = WXAPIFactory.createWXAPI(this, WeChatConstant.APP_ID,true);
        wxAPI.registerApp(WeChatConstant.APP_ID);
        wxAPI.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.e(TAG, "onReq: "+baseReq );
    }

    /*发送到微信请求的响应结果将回调到onResp方法*/
    @Override
    public void onResp(BaseResp baseResp) {
        //分享
        if (baseResp.getType()== ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX){
            Log.e(TAG, "onResp:微信分享回调： "+baseResp );
            switch (baseResp.errCode){
                case BaseResp.ErrCode.ERR_OK:
                    Log.i(TAG, "分享成功");
                    Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    Log.i(TAG,"分享取消");
                    Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT).show();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    Log.i(TAG,"分享被拒绝");
                    Toast.makeText(this, "分享被拒绝", Toast.LENGTH_SHORT).show();
                    break;
             }
        }
        finish();
    }
}
