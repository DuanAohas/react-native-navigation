package com.ktsedu.alipay;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.google.gson.Gson;
import java.io.Serializable;

/**
 * Created by Aohas on 2016/4/15.
 */
public class PayEntity implements Serializable {
//    wechatPayOrder{"appid":"wxcc93600f99d00401","noncestr":"xfgjXSA0Hy6jl6py","package":"Sign=WXPay","partnerid":"1434257802","prepayid":"wx20170711103015233355ce2a0687173870","timestamp":1499740215}
    public int code = -1;
    public String msg = "";

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static final int PAY_STATE_NO_PAY = 0;
    public static final int PAY_STATE_PAY_SUCCESS = 1;
    public static final int PAY_STATE_PAY_FAILURE = 2;
    public static int isSuccess_pay = PAY_STATE_NO_PAY;

    public PayReq wechatPay = new PayReq();
    public PayEntity data = null;

//    public String out_trade_no = "";
    public String body = "";
    public String subject = "";
    public String total_amount = "";

    public String id;

//        "appid": "wxcc93600f99d00401",
//                "err_code_desc": "",
//                "noncestr": "pHFh2XZrvfBhSKw2",
//                "packagesign": "Sign=WXPay",
//                "partnerid": "1434257802",
//                "prepayid": "wx2017071110104857901c04d00789067790",
//                "timestamp": 1499739048,
//                "sign": "6FA1A9EB83C6A5CBBF613B1CAE3DA606",
//                "out_trade_no": "041d9d796fab894"
    public String appid = "";
    public String err_code_desc = "";
    public String noncestr = "";
    public String packagesign = "";
    public String partnerid = "";
    public String prepayid = "";
    public String timestamp = "";
    public String sign = "";
    public String out_trade_no = "";


//    public String appid = "";
//    public String mch_id = "";
////    public String device_info = "";
////    public String nonce_str = "";
//    public String noncestr="";
//    public String sign = "";
////    public String prepay_id = "";
//    public String prepayid="";
//    public String partnerid="";
//    public String timestamp="";
//    public String packagesign="";

    public String aliPayOrder = "";//
//    public AlipayEntity ali = new AlipayEntity();  {"code":0,"msg":"微信预支付订单创建成功","data":{
// "appid":"wxda203223a4d023b0","mch_id":"1332357601",
// "nonce_str":"foy6wAglPTMq9u4F",
// "prepay_id":"wx20160416143317dc4d79d8340154693709","sign":"B1FF4ECBBFF2977FDD85D900D5967477","out_trade_no":"C416883971952721"}}


    public PayReq getWechatPay() {
        return wechatPay;
    }

    public void setWechatPay(PayReq wechatPay) {
        this.wechatPay = wechatPay;
    }

    public PayEntity getData() {
        return data;
    }

    public void setData(PayEntity data) {
        this.data = data;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public boolean CheckCode() {
        switch (code) {
            case 0:
                return true;
        }
        return false;
    }

    public static boolean payALipayStatus = false;
    public static String payTracode = "";
    public static void setPayALipayStatus(boolean st) {
        payALipayStatus = st;
    }

    public static boolean isPayALipayStatus() {
        return payALipayStatus;
    }

    public static String getPayTracode() {
        return payTracode;
    }

    public static void setPayTracode(String tr) {
        payTracode = tr;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getErr_code_desc() {
        return err_code_desc;
    }

    public void setErr_code_desc(String err_code_desc) {
        this.err_code_desc = err_code_desc;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getPackagesign() {
        return packagesign;
    }

    public void setPackagesign(String packagesign) {
        this.packagesign = packagesign;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
