package com.ktsedu.kutingshuo.wxapi;

import android.content.Context;

import com.ktsedu.alipay.PayEntity;
import com.ktsedu.code.util.CheckUtil;
import com.ktsedu.utils.ToastUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by aaa on 2014/11/24 0024.
 */
public class WXPayUtil {
    private static Context context;
    private static WXPayUtil instance;
    private static final String TAG = "MicroMsg.SDKSample.PayActivity";
    private IWXAPI api;
    private String prepayId ="";

//    "sign_method":"sha1","timestamp":1421899015,"package":"bank_type=WX&body=%E8%B4%A6%E5%8F%B7%EF%BC%9A18888888888%0A%E4%BA%A4%E6%98%93%E7%9B%AE%E7%9A%84%3A%E5%85%85%E5%80%BC%0A%E8%AE%A2%E5%8D%95%E5%8F%B7%EF%BC%9A53&fee_type=1&input_charset=UTF-8&notify_url=http%3A%2F%2Fm.meitoday.com%2Fnotify%2Fwx&out_trade_no=53&partner=1228341501&spbill_create_ip=115.29.163.56&total_fee=100&sign=DACA9A5D82D9095812619EFE4851D23D","noncestr":"142536b9b535b78e681c11b0195d962f","appid":"wxec02438505b31be1","app_signature":"884db45fecdb68bb14e0ee8803198733650b96e9","traceid":"crestxu_1421899015"


    public IWXAPI getApi() {
        return api;
    }

    public void setApi(IWXAPI api) {
        this.api = api;
    }

    private static enum LocalRetCode {
        ERR_OK, ERR_HTTP, ERR_JSON, ERR_OTHER
    }

    public static WXPayUtil getInstance(Context mContext) {
        if (CheckUtil.isEmpty(instance)) {
            instance = new WXPayUtil();
        }
        context = mContext;
        return instance;
    }
//wechatPayOrder{"appid":"wxcc93600f99d00401","noncestr":"xfgjXSA0Hy6jl6py","package":"Sign=WXPay","partnerid":"1434257802","prepayid":"wx20170711103015233355ce2a0687173870","timestamp":1499740215}
public void pay(Context co,PayEntity payEntity){
    PayReq req = new PayReq();
    req.appId = payEntity.appid;
    req.nonceStr = payEntity.noncestr;//nonce_str;
    req.partnerId = payEntity.partnerid;// "1332357601";
    req.prepayId = payEntity.prepayid;//prepay_id;
//        req.extData="show msg";
    prepayId = req.partnerId;
    req.packageValue = payEntity.packagesign;//"Sign=WXPay";//Sign=WXPay
    req.timeStamp = payEntity.timestamp;//String.valueOf(genTimeStamp());
    req.extData			= payEntity.out_trade_no;// "out_trade_no"; // out_trade_no
//        req.timeStamp = String.valueOf(genTimeStamp());
    req.sign = paySign(req);//payEntity.sign;
    req.signType="MD5";
//        req.sign = payEntity.sign;
//    api = WXAPIFactory.createWXAPI(co, req.appId);  //创建微信支付连接
    api = WXAPIFactory.createWXAPI(co, null);  //创建微信支付连接
    if(!api.isWXAppInstalled() && !api.isWXAppSupportAPI()){
        ToastUtil.toast("微信未安装");
    }
    api.registerApp(req.appId); //把App注册到微信
    api.sendReq(req); //启动微信支付接口
}

    //    public void pay(Context co,PayEntity payEntity){
//        PayReq req = new PayReq();
//        req.appId = payEntity.appid;
//        req.nonceStr = payEntity.noncestr;//nonce_str;
//        req.partnerId = payEntity.partnerid;// "1332357601";
//        req.prepayId = payEntity.prepayid;//prepay_id;
////        req.extData="show msg";
//        prepayId = req.partnerId;
//        req.packageValue = payEntity.packagesign;//"Sign=WXPay";//Sign=WXPay
//        req.timeStamp = payEntity.timestamp;//String.valueOf(genTimeStamp());
//        req.extData			= payEntity.out_trade_no;// "out_trade_no"; // out_trade_no
////        req.timeStamp = String.valueOf(genTimeStamp());
//        req.sign = paySign(req);//payEntity.sign;
//        req.signType="MD5";
////        req.sign = payEntity.sign;
//        api = WXAPIFactory.createWXAPI(co, req.appId);  //创建微信支付连接
//        if(!api.isWXAppInstalled() && !api.isWXAppSupportAPI()){
//            ToastUtil.toast("微信未安装");
//        }
//        api.registerApp(req.appId); //把App注册到微信
//        api.sendReq(req); //启动微信支付接口
//    }
//    public void pay1(Context co,PayEntity payEntity){
//        PayReq req = new PayReq();
//        req.appId = payEntity.appid;
//        req.nonceStr = payEntity.noncestr;//nonce_str;
//        req.partnerId = payEntity.partnerid;// "1332357601";
//        req.prepayId = payEntity.prepayid;//prepay_id;
//        prepayId = req.partnerId;
//        req.packageValue = payEntity.packagesign;//"Sign=WXPay";//Sign=WXPay
//        req.extData			= payEntity.out_trade_no;// "out_trade_no"; // out_trade_no
//        req.timeStamp = payEntity.timestamp;//String.valueOf(genTimeStamp());
////        req.sign = paySign(req);//payEntity.sign;
//        req.signType="MD5";
//        req.sign = payEntity.sign;
//        api = WXAPIFactory.createWXAPI(co, req.appId);  //创建微信支付连接
//        api.registerApp(req.appId); //把App注册到微信
//        api.sendReq(req); //启动微信支付接口
//    }
    private String paySign(PayReq req){
        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        return genSign(signParams);
    }

    private String genSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append("KuTingShuo518wwwktseducomktseduK");   //prepayId   Config.WEIXIN_APPID

//        sb.append("sign str\n"+sb.toString()+"\n\n");
        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
//        Log.e("orion",appSign);
        return appSign;
    }
    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }
}
