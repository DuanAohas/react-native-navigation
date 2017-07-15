package com.reactnativenavigation.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.reactnativenavigation.R;
import com.reactnativenavigation.animation.VisibilityAnimator;
import com.reactnativenavigation.params.AppStyle;
import com.reactnativenavigation.params.ScreenParams;
import com.reactnativenavigation.params.StyleParams;
import com.reactnativenavigation.utils.ViewUtils;
import com.reactnativenavigation.views.utils.Constants;

import java.util.List;

public class BottomTabs extends AHBottomNavigation {

    private VisibilityAnimator visibilityAnimator;

    public BottomTabs(Context context) {
        super(context);
        setForceTint(true);
        setId(ViewUtils.generateViewId());
        createVisibilityAnimator();
        this.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);// 增加显示标题  *********
        setBehaviorTranslationEnabled(false);
        setTranslucentNavigationEnabled(true);
        setStyle();
        setFontFamily();
//        setClipToPadding(true);

//                //此片为了增加 底部栏上边的一条线
//        LinearLayout bottomLayout = new LinearLayout(context);
//        bottomLayout.setOrientation(LinearLayout.VERTICAL);
//        bottomLayout.setGravity(Gravity.CENTER);

//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WRAP_CONTENT);

//        LinearLayout lineLayout = new LinearLayout(context);
//        lineLayout.setOrientation(LinearLayout.VERTICAL);
//        lineLayout.setGravity(Gravity.CENTER);
//        lineLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//        LayoutParams layoutlineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5);

//        bottomLayout.setBackgroundColor(getResources().getColor(R.color.main_nv_bg_color));
//        bottomLayout.addView(lineLayout,layoutlineParams);
//        this.addView(lineLayout,layoutlineParams);
//        bottomLayout.addView(bottomTabs,lp);

        //        addView(bottomLayout, layoutParams);

//        setClipChildren(false);
//        this.setBackground(getResources().getDrawable(R.drawable.main_nv_bottom_bg_topline));
        //创建Drawable
//        GradientDrawable whiteDrawable = getGraDra(getActivity(), R.color.main_nv_bg_color,2);
//        GradientDrawable grayDrawable =getGraDra(getActivity(),R.color.colorAccent,2);
//        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{grayDrawable,whiteDrawable});
//        //设置padding
//        layerDrawable.setLayerInset(1,0,3,0,0);
//        //设置drawable为背景
//        ViewCompat.setBackground(this,layerDrawable);
    }
//    public static GradientDrawable getGraDra(Context context, int colID, int dp) {
//        GradientDrawable drawable = new GradientDrawable();
//        drawable.setColor(context.getResources().getColor(colID));
//        drawable.setCornerRadius(dp);
//        return drawable;
//    }
    public void addTabs(List<ScreenParams> params, OnTabSelectedListener onTabSelectedListener) {
        for (ScreenParams screenParams : params) {
            AHBottomNavigationItem item = new AHBottomNavigationItem(screenParams.tabLabel, screenParams.tabIcon,
                    Color.GRAY);
            addItem(item);
            setOnTabSelectedListener(onTabSelectedListener);
        }
        setTitlesDisplayState();
    }

    public void setStyleFromScreen(StyleParams params) {
        if (params.bottomTabsColor.hasColor()) {
            setBackgroundColor(params.bottomTabsColor);
        }
        if (params.bottomTabsButtonColor.hasColor()) {
            if (getInactiveColor() != params.bottomTabsButtonColor.getColor()) {
                setInactiveColor(params.bottomTabsButtonColor.getColor());
            }
        }
        if (params.selectedBottomTabsButtonColor.hasColor()) {
            if (getAccentColor() != params.selectedBottomTabsButtonColor.getColor()) {
                setAccentColor(params.selectedBottomTabsButtonColor.getColor());
            }
        }

        setVisibility(params.bottomTabsHidden, true);
    }

    public void setTabButton(ScreenParams params, Integer index) {
        if (params.tabIcon != null) {
            AHBottomNavigationItem item = this.getItem(index);
            item.setDrawable(params.tabIcon);
            refresh();
        }
    }

    private void setTitlesDisplayState() {
        if (AppStyle.appStyle.forceTitlesDisplay) {
            setTitleState(TitleState.ALWAYS_SHOW);
        } else if (hasTabsWithLabels()) {
            setTitleState(TitleState.SHOW_WHEN_ACTIVE);
        } else {
            setTitleState(TitleState.ALWAYS_HIDE);
        }
    }

    private boolean hasTabsWithLabels() {
        for (int i = 0; i < getItemsCount(); i++) {
            String title = getItem(0).getTitle(getContext());
            if (!TextUtils.isEmpty(title)) {
                return true;
            }
        }
        return false;
    }

    public void setVisibility(boolean hidden, boolean animated) {
        if (visibilityAnimator != null) {
            visibilityAnimator.setVisible(!hidden, animated);
        } else {
            setVisibility(hidden);
        }
    }

    private void setBackgroundColor(StyleParams.Color bottomTabsColor) {
        if (bottomTabsColor.hasColor()) {
            if (bottomTabsColor.getColor() != getDefaultBackgroundColor()) {
                setDefaultBackgroundColor(bottomTabsColor.getColor());
            }
        } else if (Color.WHITE != getDefaultBackgroundColor()){
            setDefaultBackgroundColor(Color.WHITE);
        }
    }

    private void setVisibility(boolean bottomTabsHidden) {
        setVisibility(bottomTabsHidden ? GONE : VISIBLE);
    }

    private void createVisibilityAnimator() {
        visibilityAnimator = new VisibilityAnimator(BottomTabs.this,
                VisibilityAnimator.HideDirection.Down,
                Constants.BOTTOM_TABS_HEIGHT);
    }

    private void setStyle() {
        if (hasBadgeBackgroundColor()) {
            setNotificationBackgroundColor(AppStyle.appStyle.bottomTabBadgeBackgroundColor.getColor());
        }
        if (hasBadgeTextColor()) {
            setNotificationTextColor(AppStyle.appStyle.bottomTabBadgeTextColor.getColor());
        }
    }

    private boolean hasBadgeTextColor() {
        return AppStyle.appStyle.bottomTabBadgeTextColor != null &&
               AppStyle.appStyle.bottomTabBadgeTextColor.hasColor();
    }

    private boolean hasBadgeBackgroundColor() {
        return AppStyle.appStyle.bottomTabBadgeBackgroundColor != null &&
               AppStyle.appStyle.bottomTabBadgeBackgroundColor.hasColor();
    }

    private void setFontFamily() {
        if (AppStyle.appStyle.bottomTabFontFamily.hasFont()) {
            setTitleTypeface(AppStyle.appStyle.bottomTabFontFamily.get());
        }
    }
}
