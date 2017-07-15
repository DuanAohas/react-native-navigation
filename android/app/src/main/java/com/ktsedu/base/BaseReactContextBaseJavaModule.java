package com.ktsedu.base;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
/**
 * Created by mac on 2017/7/5.
 */

public abstract  class BaseReactContextBaseJavaModule extends ReactContextBaseJavaModule {
    public BaseReactContextBaseJavaModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    // 存储支付结束后是否要跳转原生界面
    protected static boolean bOpenPayView = false;//
    protected static String jumpType = "";
    protected static String bookId = "";
    public static boolean isbOpenPayView(){
        return bOpenPayView;
    }
    public static void setbOpenPayView(boolean br){
        bOpenPayView = br;
    }
    public static boolean getJumpType(){
        if(jumpType == null || jumpType=="")
        {return false;}
        return true;
    }
    public static String getBookId(){
        return bookId;
    }

}
