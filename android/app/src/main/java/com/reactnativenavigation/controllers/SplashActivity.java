package com.reactnativenavigation.controllers;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ktsedu.base.AppConfig;
import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.react.ReactDevPermission;
import com.reactnativenavigation.R;

public abstract class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setSplashLayout();// 此处增加自己的布局界面
        setContentView(R.layout.nv_splash);  // 使用自定义布局，显示背景的界面和风格
//        StatusBarCompat.translucentStatusBar(this,true);
        IntentDataHandler.saveIntentData(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NavigationApplication.instance.getReactGateway().hasStartedCreatingContext()) {
            finish();
            return;
        }

        if (ReactDevPermission.shouldAskPermission()) {
            ReactDevPermission.askPermission(this);
            return;
        }

        if (NavigationApplication.instance.isReactContextInitialized()) {
            finish();
            return;
        }
        startReactNative(); //此处启动  RN

    }

    //主层RN 下一个界面
    protected void startReactNative(){
        // TODO I'm starting to think this entire flow is incorrect and should be done in Application
        NavigationApplication.instance.startReactContextOnceInBackgroundAndExecuteJS();
    }

    private void setSplashLayout() {
        final int splashLayout = getSplashLayout();
        if (splashLayout > 0) {
            setContentView(splashLayout);
        } else {
            setContentView(createSplashLayout());
        }
    }

    /**
     * @return xml layout res id
     */
    @LayoutRes
    public int getSplashLayout() {
        return 0;
    }

    /**
     * @return the layout you would like to show while react's js context loads
     */
    public View createSplashLayout() {
        View view = new View(this);
        view.setBackgroundColor(Color.WHITE);
        return view;
    }
}
