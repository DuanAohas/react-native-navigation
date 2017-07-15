package com.reactnativenavigation.controllers;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.ktsedu.alipay.AuthResult;
import com.ktsedu.alipay.PayEntity;
import com.ktsedu.alipay.PayResult;
import com.ktsedu.alipay.util.OrderInfoUtil2_0;
import com.ktsedu.base.AppConfig;
import com.ktsedu.base.BaseReactContextBaseJavaModule;
import com.ktsedu.utils.ToastUtil;
import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.R;
import com.reactnativenavigation.bridge.BundleConverter;
import com.reactnativenavigation.events.Event;
import com.reactnativenavigation.events.EventBus;
import com.reactnativenavigation.events.JsDevReloadEvent;
import com.reactnativenavigation.events.ModalDismissedEvent;
import com.reactnativenavigation.events.Subscriber;
import com.reactnativenavigation.layouts.BottomTabsLayout;
import com.reactnativenavigation.layouts.Layout;
import com.reactnativenavigation.layouts.LayoutFactory;
import com.reactnativenavigation.params.ActivityParams;
import com.reactnativenavigation.params.AppStyle;
import com.reactnativenavigation.params.ContextualMenuParams;
import com.reactnativenavigation.params.FabParams;
import com.reactnativenavigation.params.LightBoxParams;
import com.reactnativenavigation.params.Orientation;
import com.reactnativenavigation.params.ScreenParams;
import com.reactnativenavigation.params.SlidingOverlayParams;
import com.reactnativenavigation.params.SnackbarParams;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.react.ReactGateway;
import com.reactnativenavigation.screens.Screen;
import com.reactnativenavigation.utils.OrientationHelper;
import com.reactnativenavigation.views.SideMenu.Side;

import java.util.List;
import java.util.Map;
import com.ktsedu.utils.JSONHelper;
//import com.notrace.systembar.StatusBarCompat;
import com.umeng.socialize.UMShareAPI;
public class NavigationActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler, Subscriber, PermissionAwareActivity {

    /**
     * Although we start multiple activities, we make sure to pass Intent.CLEAR_TASK | Intent.NEW_TASK
     * So that we actually have only 1 instance of the activity running at one time.
     * We hold the currentActivity (resume->pause) so we know when we need to destroy the javascript context
     * (when currentActivity is null, ie pause and destroy was called without resume).
     * This is somewhat weird, and in the future we better use a single activity with changing contentView similar to ReactNative impl.
     * Along with that, we should handle commands from the bridge using onNewIntent
     */
    static NavigationActivity currentActivity;

    private ActivityParams activityParams;
    private ModalController modalController;
    private Layout layout;
    @Nullable private PermissionListener mPermissionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!NavigationApplication.instance.isReactContextInitialized()) {
            NavigationApplication.instance.startReactContextOnceInBackgroundAndExecuteJS();
            return;
        }
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//SCREEN_ORIENTATION_PORTRAIT    SCREEN_ORIENTATION_LANDSCAPE
        activityParams = NavigationCommandsHandler.parseActivityParams(getIntent());
        disableActivityShowAnimationIfNeeded();
        setOrientation();
        createModalController();
        createLayout();
        NavigationApplication.instance.getActivityCallbacks().onActivityCreated(this, savedInstanceState);
    }

    private void setOrientation() {
        //Orientation
//        OrientationHelper.setOrientation(this, Orientation.Portrait);// 禁止屏幕翻转
        OrientationHelper.setOrientation(this, AppStyle.appStyle.orientation);
    }

    private void disableActivityShowAnimationIfNeeded() {
        if (!activityParams.animateShow) {
            overridePendingTransition(0, 0);
        }
    }

    private void createModalController() {
        modalController = new ModalController(this);
    }

    private void createLayout() {// ******* 
        View mViewBase =   LayoutInflater.from(this).inflate(R.layout.nv_navigation, null);
        Log.d("NavigationActivity 000",activityParams.toString());

        layout = LayoutFactory.create(this, activityParams);
        if (hasBackgroundColor()) {
            layout.asView().setBackgroundColor(AppStyle.appStyle.screenBackgroundColor.getColor());
            mViewBase.setBackgroundColor(AppStyle.appStyle.screenBackgroundColor.getColor());
        }
//        layout.asView().setFitsSystemWindows(true);// 顶部的 状态条设置为背景透明 clipToPadding
        RelativeLayout relativeLayout = (RelativeLayout) mViewBase.findViewById(R.id.nv_nagitation_rlayout);

        relativeLayout.addView(layout.asView());
        setContentView(mViewBase);
    }

    private boolean hasBackgroundColor() {
        return AppStyle.appStyle.screenBackgroundColor != null &&
               AppStyle.appStyle.screenBackgroundColor.hasColor();
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavigationApplication.instance.getActivityCallbacks().onActivityStarted(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isFinishing() || !NavigationApplication.instance.isReactContextInitialized()) {
            return;
        }

        currentActivity = this;
        IntentDataHandler.onResume(getIntent());
        getReactGateway().onResumeActivity(this, this);
        NavigationApplication.instance.getActivityCallbacks().onActivityResumed(this);
        EventBus.instance.register(this);
        IntentDataHandler.onPostResume(getIntent());
//sendEvent   sendNavigatorEvent
//        NavigationApplication.instance.getEventEmitter().sendNavigatorEvent("choosetopay", "choosetopay");
//        NavigationApplication.instance.getEventEmitter().sendEvent("choosetopay");
//        ToastUtil.toast("choosetopay");
        if(BaseReactContextBaseJavaModule.getJumpType() && BaseReactContextBaseJavaModule.isbOpenPayView()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BaseReactContextBaseJavaModule.setbOpenPayView(false);
//                    try {
////                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                        NavigationApplication.instance.getEventEmitter().sendScreenChangedEvent("choosetopay", "choosetopay");
                            Screen previousScreen = layout.getCurrentScreen();
                            NavigationApplication.instance.getEventEmitter().sendScreenChangedEvent("choosetopay"+BaseReactContextBaseJavaModule.getBookId(), previousScreen.getNavigatorEventId());

//                            NavigationApplication.instance.getEventEmitter().sendScreenChangedEvent("choosetopay", previousScreen.getNavigatorEventId());
//                            ToastUtil.toast("choosetopayThread");
                        }
                    });

                }
            }).start();
        }

    }
//    private Bundle bundletemp=null;//
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getReactGateway().onNewIntent(intent);
        NavigationApplication.instance.getActivityCallbacks().onNewIntent(intent);
//        if(requestCode == 30){
            currentActivity = this;
//            bundletemp = intent.getBundleExtra(NavigationCommandsHandler.ACTIVITY_PARAMS_BUNDLE);
//            if(bundletemp != null) {
//                Log.d("onActivityResult30", bundletemp.toString());
//                NavigationCommandsHandler.showModal(bundletemp);
//            }
//            NavigationCommandsHandler.push(bundletemp);

//            ScreenParams screenParams = new ScreenParams();
//            screenParams.screenId = "kts.BookPayView";
//            screenParams.title = "支付功能";
//            showModalMap(ReadableMap("{              title: '支付功能',              screen: 'kts.BookPayView',            }"));
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentActivity = null;
        IntentDataHandler.onPause(getIntent());
        getReactGateway().onPauseActivity();
        NavigationApplication.instance.getActivityCallbacks().onActivityPaused(this);
        EventBus.instance.unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        NavigationApplication.instance.getActivityCallbacks().onActivityStopped(this);
    }

    @Override
    protected void onDestroy() {
        destroyLayouts();
        destroyJsIfNeeded();
        NavigationApplication.instance.getActivityCallbacks().onActivityDestroyed(this);
        super.onDestroy();
    }

    private void destroyLayouts() {
        if (modalController != null) {
            modalController.destroy();
        }
        if (layout != null) {
            layout.destroy();
            layout = null;
        }
    }

    private void destroyJsIfNeeded() {
        if (currentActivity == null || currentActivity.isFinishing()) {
            getReactGateway().onDestroyApp();
        }
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (layout != null && !layout.onBackPressed()) {
            getReactGateway().onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getReactGateway().onActivityResult(requestCode, resultCode, data);
		NavigationApplication.instance.umengShareActivityResult(this,requestCode, resultCode, data);
        NavigationApplication.instance.getActivityCallbacks().onActivityResult(requestCode, resultCode, data);
//        if(Config.RNJUMP_ITEM.DEMO)
//         Log.d("onActivityResult", requestCode+ NavigationCommandsHandler.ACTIVITY_PARAMS_BUNDLE);
//         if(requestCode == 50){//
//             currentActivity = this;
//             bundletemp = data.getBundleExtra(NavigationCommandsHandler.ACTIVITY_PARAMS_BUNDLE);
//             Log.d("onActivityResult30", bundletemp.toString());
//             NavigationCommandsHandler.showModal(bundletemp);
// //            NavigationCommandsHandler.push(bundletemp);

// //            ScreenParams screenParams = new ScreenParams();
// //            screenParams.screenId = "kts.BookPayView";
// //            screenParams.title = "支付功能";
// //            showModalMap(ReadableMap("{              title: '支付功能',              screen: 'kts.BookPayView',            }"));
//         }
    }

//    public void showModalMap( ReadableMap params) {
////        Log.d("showModal1", JSONHelper.toJSON(params));
//        Log.d("showModal1", params.toString());
//        NavigationCommandsHandler.showModal(BundleConverter.toBundle(params));
//    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return getReactGateway().onKeyUp(getCurrentFocus(), keyCode) || super.onKeyUp(keyCode, event);
    }

    public ReactGateway getReactGateway() {
        return NavigationApplication.instance.getReactGateway();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        OrientationHelper.onConfigurationChanged(newConfig);// 禁止屏幕翻转
        NavigationApplication.instance.getActivityCallbacks().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    void push(ScreenParams params) {
        if (modalController.containsNavigator(params.getNavigatorId())) {
            modalController.push(params);
        } else {
            layout.push(params);
        }
    }

    void pop(ScreenParams params) {
        if (modalController.containsNavigator(params.getNavigatorId())) {
            modalController.pop(params);
        } else {
            layout.pop(params);
        }
    }

    void popToRoot(ScreenParams params) {
        if (modalController.containsNavigator(params.getNavigatorId())) {
            modalController.popToRoot(params);
        } else {
            layout.popToRoot(params);
        }
    }

    void newStack(ScreenParams params) {
        if (modalController.containsNavigator(params.getNavigatorId())) {
            modalController.newStack(params);
        } else {
            layout.newStack(params);
        }
    }

    void showModal(ScreenParams screenParams) {
        Screen previousScreen = layout.getCurrentScreen();
        NavigationApplication.instance.getEventEmitter().sendScreenChangedEvent("willDisappear", previousScreen.getNavigatorEventId());
        NavigationApplication.instance.getEventEmitter().sendScreenChangedEvent("didDisappear", previousScreen.getNavigatorEventId());
        modalController.showModal(screenParams);
    }

    void dismissTopModal() {
        modalController.dismissTopModal();
        Screen previousScreen = layout.getCurrentScreen();
        NavigationApplication.instance.getEventEmitter().sendScreenChangedEvent("willAppear", previousScreen.getNavigatorEventId());
        NavigationApplication.instance.getEventEmitter().sendScreenChangedEvent("didAppear", previousScreen.getNavigatorEventId());
    }

    void dismissAllModals() {
        modalController.dismissAllModals();
        Screen previousScreen = layout.getCurrentScreen();
        NavigationApplication.instance.getEventEmitter().sendScreenChangedEvent("willAppear", previousScreen.getNavigatorEventId());
        NavigationApplication.instance.getEventEmitter().sendScreenChangedEvent("didAppear", previousScreen.getNavigatorEventId());
    }

    public void showLightBox(LightBoxParams params) {
        layout.showLightBox(params);
    }

    public void dismissLightBox() {
        layout.dismissLightBox();
    }

    //TODO all these setters should be combined to something like setStyle
    void setTopBarVisible(String screenInstanceId, boolean hidden, boolean animated) {
        layout.setTopBarVisible(screenInstanceId, hidden, animated);
        modalController.setTopBarVisible(screenInstanceId, hidden, animated);
    }

    void setBottomTabsVisible(boolean hidden, boolean animated) {
        if (layout instanceof BottomTabsLayout) {
            ((BottomTabsLayout) layout).setBottomTabsVisible(hidden, animated);
        }
    }

    void setTitleBarTitle(String screenInstanceId, String title) {
        layout.setTitleBarTitle(screenInstanceId, title);
        modalController.setTitleBarTitle(screenInstanceId, title);
    }

    public void setTitleBarSubtitle(String screenInstanceId, String subtitle) {
        layout.setTitleBarSubtitle(screenInstanceId, subtitle);
        modalController.setTitleBarSubtitle(screenInstanceId, subtitle);
    }

    void setTitleBarButtons(String screenInstanceId, String navigatorEventId, List<TitleBarButtonParams> titleBarButtons) {
        layout.setTitleBarRightButtons(screenInstanceId, navigatorEventId, titleBarButtons);
        modalController.setTitleBarRightButtons(screenInstanceId, navigatorEventId, titleBarButtons);
    }

    void setTitleBarLeftButton(String screenInstanceId, String navigatorEventId, TitleBarLeftButtonParams titleBarLeftButton) {
        layout.setTitleBarLeftButton(screenInstanceId, navigatorEventId, titleBarLeftButton);
        modalController.setTitleBarLeftButton(screenInstanceId, navigatorEventId, titleBarLeftButton);
    }

    void setScreenFab(String screenInstanceId, String navigatorEventId, FabParams fab) {
        layout.setFab(screenInstanceId, navigatorEventId, fab);
        modalController.setFab(screenInstanceId, navigatorEventId, fab);
    }

    public void setScreenStyle(String screenInstanceId, Bundle styleParams) {
        layout.updateScreenStyle(screenInstanceId, styleParams);
        modalController.updateScreenStyle(screenInstanceId, styleParams);
    }

    public void toggleSideMenuVisible(boolean animated, Side side) {
        layout.toggleSideMenuVisible(animated, side);
    }

    public void setSideMenuVisible(boolean animated, boolean visible, Side side) {
        layout.setSideMenuVisible(animated, visible, side);
    }

    public void setSideMenuEnabled(boolean enabled, Side side) {
        layout.setSideMenuEnabled(enabled, side);
    }

    public void selectTopTabByTabIndex(String screenInstanceId, int index) {
        layout.selectTopTabByTabIndex(screenInstanceId, index);
        modalController.selectTopTabByTabIndex(screenInstanceId, index);
    }

    public void selectTopTabByScreen(String screenInstanceId) {
        layout.selectTopTabByScreen(screenInstanceId);
        modalController.selectTopTabByScreen(screenInstanceId);
    }

    public void selectBottomTabByTabIndex(Integer index) {
        if (layout instanceof BottomTabsLayout) {
            ((BottomTabsLayout) layout).selectBottomTabByTabIndex(index);
        }
    }

    public void selectBottomTabByNavigatorId(String navigatorId) {
        if (layout instanceof BottomTabsLayout) {
            ((BottomTabsLayout) layout).selectBottomTabByNavigatorId(navigatorId);
        }
    }

    public void setBottomTabBadgeByIndex(Integer index, String badge) {
        if (layout instanceof BottomTabsLayout) {
            ((BottomTabsLayout) layout).setBottomTabBadgeByIndex(index, badge);
        }
    }

    public void setBottomTabBadgeByNavigatorId(String navigatorId, String badge) {
        if (layout instanceof BottomTabsLayout) {
            ((BottomTabsLayout) layout).setBottomTabBadgeByNavigatorId(navigatorId, badge);
        }
    }

    public void setBottomTabButtonByIndex(Integer index, ScreenParams params) {
        if (layout instanceof BottomTabsLayout) {
            ((BottomTabsLayout) layout).setBottomTabButtonByIndex(index, params);
        }
    }

    public void setBottomTabButtonByNavigatorId(String navigatorId, ScreenParams params) {
        if (layout instanceof BottomTabsLayout) {
            ((BottomTabsLayout) layout).setBottomTabButtonByNavigatorId(navigatorId, params);
        }
    }

    public void showSlidingOverlay(SlidingOverlayParams params) {
        if (modalController.isShowing()) {
            modalController.showSlidingOverlay(params);
        } else {
            layout.showSlidingOverlay(params);
        }
    }

    public void hideSlidingOverlay() {
        if (modalController.isShowing()) {
            modalController.hideSlidingOverlay();
        } else {
            layout.hideSlidingOverlay();
        }
    }

    public void showSnackbar(SnackbarParams params) {
        layout.showSnackbar(params);
    }

    public void dismissSnackbar() {
        layout.dismissSnackbar();
    }

    public void showContextualMenu(String screenInstanceId, ContextualMenuParams params, Callback onButtonClicked) {
        if (modalController.isShowing()) {
            modalController.showContextualMenu(screenInstanceId, params, onButtonClicked);
        } else
        {
            layout.showContextualMenu(screenInstanceId, params, onButtonClicked);
        }
    }

    public void dismissContextualMenu(String screenInstanceId) {
        if (modalController.isShowing()) {
            modalController.dismissContextualMenu(screenInstanceId);
        } else {
            layout.dismissContextualMenu(screenInstanceId);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType().equals(ModalDismissedEvent.TYPE)) {
            handleModalDismissedEvent();
        } else if (event.getType().equals(JsDevReloadEvent.TYPE)) {
            postHandleJsDevReloadEvent();
        }
    }

    private void handleModalDismissedEvent() {
        if (!modalController.isShowing()) {
            layout.onModalDismissed();
//            OrientationHelper.setOrientation(this, AppStyle.appStyle.orientation);
        }
    }

    public Window getScreenWindow() {
        return modalController.isShowing() ? modalController.getWindow() : getWindow();
    }

    private void postHandleJsDevReloadEvent() {
        NavigationApplication.instance.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                layout.destroy();
                modalController.destroy();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        mPermissionListener = listener;
        requestPermissions(permissions, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        NavigationApplication.instance.getActivityCallbacks().onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionListener != null && mPermissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            mPermissionListener = null;
        }
    }

    //  add alipay
    private boolean alyPayClick = false;//
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
//                    AliPayResult payResult = new AliPayResult((String) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    alyPayClick =false;
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        PayEntity.isSuccess_pay  = PayEntity.PAY_STATE_PAY_SUCCESS;
                        Toast.makeText(NavigationActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        // 退出当前窗体， 返回到上个页面并刷新，
                        // 增加支付再回调接口，
//                        NavigationActivity.this.finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        PayEntity.isSuccess_pay = PayEntity.PAY_STATE_PAY_FAILURE;
                        Toast.makeText(NavigationActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();
                    alyPayClick =false;
                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(NavigationActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(NavigationActivity.this,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    public void aliPay(PayEntity payEntity) {
        if (TextUtils.isEmpty(AppConfig.ALIPAY_APPID) || TextUtils.isEmpty(AppConfig.ALIPAY_RASKEY)) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
//                            finish();
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(AppConfig.ALIPAY_APPID,payEntity);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        String sign = OrderInfoUtil2_0.getSign(params, AppConfig.ALIPAY_RASKEY);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {

                PayTask alipay = new PayTask(NavigationActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);

            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}
