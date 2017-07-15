package com.ktsedu.kutingshuo.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ktsedu.alipay.PayEntity;
import com.reactnativenavigation.R;
import com.ktsedu.base.AppConfig;
import com.ktsedu.code.util.Log;
import com.ktsedu.utils.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, AppConfig.WEIXIN_APPID);//wx409c18dee368f38a
        api.registerApp(AppConfig.WEIXIN_APPID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                PayEntity.isSuccess_pay = PayEntity.PAY_STATE_PAY_SUCCESS;
                ToastUtil.toast("付款成功");
            } else if (resp.errCode == -2) {
                PayEntity.isSuccess_pay = PayEntity.PAY_STATE_PAY_FAILURE;
                ToastUtil.toast("支付取消");
            } else if (resp.errCode == -3) {
                PayEntity.isSuccess_pay = PayEntity.PAY_STATE_PAY_FAILURE;
                ToastUtil.toast("请求失败");
            } else {
                PayEntity.isSuccess_pay = PayEntity.PAY_STATE_PAY_FAILURE;
                ToastUtil.toast("支付失败,errCode:" + resp.errCode + resp.errStr);
                Log.e("tg", "=====weixin.========支付失败,errCode:" + resp.errCode + resp.toString());
            }
        }
        finish();
    }
}