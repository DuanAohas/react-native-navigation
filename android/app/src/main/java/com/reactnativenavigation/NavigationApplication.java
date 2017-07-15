package com.reactnativenavigation;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.ktsedu.UmengShare.ShareData;
import com.reactnativenavigation.bridge.EventEmitter;
import com.reactnativenavigation.controllers.ActivityCallbacks;
import com.reactnativenavigation.react.NavigationReactGateway;
import com.reactnativenavigation.react.ReactGateway;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.List;
import java.util.Map;

public abstract class NavigationApplication extends Application implements ReactApplication {

    public static NavigationApplication instance;

    private NavigationReactGateway reactGateway;
    private EventEmitter eventEmitter;
    private Handler handler;
    private ActivityCallbacks activityCallbacks;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        handler = new Handler(getMainLooper());
        reactGateway = new NavigationReactGateway();
        eventEmitter = new EventEmitter(reactGateway);
        activityCallbacks = new ActivityCallbacks();
    }

    @Override
    public void startActivity(Intent intent) {
        String animationType = intent.getStringExtra("animationType");
        if (animationType != null && animationType.equals("fade")) {
            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(),
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            ).toBundle();
            super.startActivity(intent, bundle);
        } else {
            super.startActivity(intent);
        }
    }

    public void startReactContextOnceInBackgroundAndExecuteJS() {
        reactGateway.startReactContextOnceInBackgroundAndExecuteJS();
    }

    public void runOnMainThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void runOnMainThread(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }

    public ReactGateway getReactGateway() {
        return reactGateway;
    }

    public ActivityCallbacks getActivityCallbacks() {
        return activityCallbacks;
    }

    protected void setActivityCallbacks(ActivityCallbacks activityLifecycleCallbacks) {
        this.activityCallbacks = activityLifecycleCallbacks;
    }

    public boolean isReactContextInitialized() {
        return reactGateway.isInitialized();
    }

    public void onReactInitialized(ReactContext reactContext) {
        // nothing
    }

    @Override
    public ReactNativeHost getReactNativeHost() {
        return reactGateway.getReactNativeHost();
    }

    public EventEmitter getEventEmitter() {
        return eventEmitter;
    }

    /**
     * @see ReactNativeHost#getJSMainModuleName()
     */
    @Nullable
    public String getJSMainModuleName() {
        return null;
    }

    /**
     * @see ReactNativeHost#getJSBundleFile()
     */
    @Nullable
    public String getJSBundleFile() {
        return null;
    }

    /**
     * @see ReactNativeHost#getBundleAssetName()
     */
    @Nullable
    public String getBundleAssetName() {
        return null;
    }

    public abstract boolean isDebug();

    @Nullable
    public abstract List<ReactPackage> createAdditionalReactPackages();

    {

        PlatformConfig.setWeixin("wxcc93600f99d00401", "dbe1cc2215574428a4ed4e44857cb96f");
        PlatformConfig.setQQZone("1105740567", "wzvPT3RUqb0b0qFE");
        PlatformConfig.setSinaWeibo("3164693074", "6058fa1311f55cbec9dfcbc3edda5945", "http://www.sharesdk.cn");
    }

    // 测试使用放在这里，后期需要单独拉出文件
        public void umengInit() {

        }

        public void umengShareActivityResult(Activity act, int requestCode, int resultCode, Intent data) {
            UMShareAPI.get(act).onActivityResult(requestCode, resultCode, data);
        }

        public void umengShareStartShare(Activity act, ShareData shareData,final Callback callback) {

            //{"platformName":"weibo","title":"123","descr":"456","thumbURL":"http://k1.jsqq.net/uploads/allimg/1612/140F5A32-6.jpg","webpageUrl":"https://www.baidu.com"}
            try {
                new ShareAction(act).withMedia(new UMWeb(shareData.getWebpageUrl(),shareData.getTitle(),shareData.getDescr(),new UMImage(act,shareData.getThumbURL())))

                        .setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA,  SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_FAVORITE,SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(new UMShareListener() {
                            @Override
                            public void onStart(SHARE_MEDIA share_media) {//开始分享

                            }

                            @Override
                            public void onResult(SHARE_MEDIA share_media) {
                                //                        acallback.invoke(null,share_media.toString());
                            }

                            @Override
                            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                //                        acallback.invoke("share error",share_media.toString());
                            }

                            @Override
                            public void onCancel(SHARE_MEDIA share_media) {
                                //                        acallback.invoke("cancel",share_media.toString());
                            }
                        }).open();
            }catch (Exception   e){
                e.printStackTrace();
            }catch (NoClassDefFoundError re){
                re.printStackTrace();
            }
        }

    public boolean umengShareIsInstall(Activity act, String loginType){
        SHARE_MEDIA shloginmid = SHARE_MEDIA.QQ;
        switch (loginType){
            case "qq":
                shloginmid = SHARE_MEDIA.QQ;
                break;
            case "wechat":
                shloginmid = SHARE_MEDIA.WEIXIN;
                break;
            case "weibo":
                shloginmid = SHARE_MEDIA.SINA;
                break;
            default:
                return false;
        }
        try {
            return UMShareAPI.get(act).isInstall(act, shloginmid);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public void umengShareStartSingleShare(Activity act, ShareData shareData,final Callback callback) {
//SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA,  SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_FAVORITE,SHARE_MEDIA.WEIXIN_CIRCLE
        SHARE_MEDIA share_media = SHARE_MEDIA.QQ;
        switch (shareData.getPlatformName()){
            case "qq":
                share_media = SHARE_MEDIA.QQ;
                break;
            case "qq_qzone":
                share_media = SHARE_MEDIA.QZONE;
                break;
            case "weibo":
                share_media = SHARE_MEDIA.SINA;
                break;
            case "wechat":
                share_media = SHARE_MEDIA.WEIXIN;
                break;
            case "wechat_timeline":
                share_media = SHARE_MEDIA.WEIXIN_CIRCLE;
                break;
            case "wechat_favorite":
                share_media = SHARE_MEDIA.WEIXIN_FAVORITE;
                break;
            default:
                return;
        }
        //{"platformName":"weibo","title":"123","descr":"456","thumbURL":"http://k1.jsqq.net/uploads/allimg/1612/140F5A32-6.jpg","webpageUrl":"https://www.baidu.com"}
        try {
            new ShareAction(act).withMedia(new UMWeb(shareData.getWebpageUrl(),shareData.getTitle(),shareData.getDescr(),new UMImage(act,shareData.getThumbURL())))
                    .setPlatform(share_media)
                    .setCallback(new UMShareListener() {
                        @Override
                        public void onStart(SHARE_MEDIA share_media) {//开始分享

                        }

                        @Override
                        public void onResult(SHARE_MEDIA share_media) {
                            //                        acallback.invoke(null,share_media.toString());
                        }

                        @Override
                        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                            //                        acallback.invoke("share error",share_media.toString());
                        }

                        @Override
                        public void onCancel(SHARE_MEDIA share_media) {
                            //                        acallback.invoke("cancel",share_media.toString());
                        }
                    }).share();
        }catch (Exception e){
            e.printStackTrace();
        }catch (NoClassDefFoundError re){
            re.printStackTrace();
        }
    }
        public void umengShareLogin(Activity act, String loginType,final Callback callback){//第三方登
            SHARE_MEDIA shloginmid = SHARE_MEDIA.QQ;
            switch (loginType){
                case "qq":
                    shloginmid = SHARE_MEDIA.QQ;
                    break;
                case "wechat":
                    shloginmid = SHARE_MEDIA.WEIXIN;
                    break;
                    default:
                        return;
            }

            UMShareAPI.get(act).getPlatformInfo(act, shloginmid, new UMAuthListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {

                }

                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                    callback.invoke(null,map);
                }

                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

                }

                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {

                }
            });
        }

}
