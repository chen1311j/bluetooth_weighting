package com.xing.library.helper.annotation;

import static com.xing.library.helper.annotation.CodeType.ANIMATION;
import static com.xing.library.helper.annotation.CodeType.AUDIO;
import static com.xing.library.helper.annotation.CodeType.BUTTON;
import static com.xing.library.helper.annotation.CodeType.CALENDAR;
import static com.xing.library.helper.annotation.CodeType.CAMERA;
import static com.xing.library.helper.annotation.CodeType.CANVAS;
import static com.xing.library.helper.annotation.CodeType.CHART;
import static com.xing.library.helper.annotation.CodeType.CORE_MOTION;
import static com.xing.library.helper.annotation.CodeType.DATABASE;
import static com.xing.library.helper.annotation.CodeType.DIALOG;
import static com.xing.library.helper.annotation.CodeType.EBOOK;
import static com.xing.library.helper.annotation.CodeType.EDITTEXT;
import static com.xing.library.helper.annotation.CodeType.GAME;
import static com.xing.library.helper.annotation.CodeType.GESTURE;
import static com.xing.library.helper.annotation.CodeType.GRIDVIEW;
import static com.xing.library.helper.annotation.CodeType.GUIDE_VIEW;
import static com.xing.library.helper.annotation.CodeType.HUD;
import static com.xing.library.helper.annotation.CodeType.IMAGE;
import static com.xing.library.helper.annotation.CodeType.LISTVIEW;
import static com.xing.library.helper.annotation.CodeType.MAP;
import static com.xing.library.helper.annotation.CodeType.MENU;
import static com.xing.library.helper.annotation.CodeType.NETWORKING;
import static com.xing.library.helper.annotation.CodeType.OTHERS;
import static com.xing.library.helper.annotation.CodeType.PICKER;
import static com.xing.library.helper.annotation.CodeType.POPUP;
import static com.xing.library.helper.annotation.CodeType.PROGRESSBAR;
import static com.xing.library.helper.annotation.CodeType.SCROLLVIEW;
import static com.xing.library.helper.annotation.CodeType.SEGMENT;
import static com.xing.library.helper.annotation.CodeType.SHARE;
import static com.xing.library.helper.annotation.CodeType.SLIDER;
import static com.xing.library.helper.annotation.CodeType.SWITCH;
import static com.xing.library.helper.annotation.CodeType.SYNC;
import static com.xing.library.helper.annotation.CodeType.TAB_BAR;
import static com.xing.library.helper.annotation.CodeType.TEXTVIEW;
import static com.xing.library.helper.annotation.CodeType.TIP;
import static com.xing.library.helper.annotation.CodeType.TOOLBAR;
import static com.xing.library.helper.annotation.CodeType.VIEW_EFFECT;
import static com.xing.library.helper.annotation.CodeType.VIEW_LAYOUT;
import static com.xing.library.helper.annotation.CodeType.VIEW_TRANSITION;
import static com.xing.library.helper.annotation.CodeType.WEBVIEW;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 页面描述：CodeType 代码类型
 *
 * Created by ditclear on 2017/10/17.
 */

@IntDef({CodeType.TIP, CodeType.DIALOG, CodeType.BUTTON, CodeType.CALENDAR, CodeType.CAMERA
        , CodeType.HUD, ArticleType.RECOMMAND, ArticleType.DAILY, CodeType.ANIMATION, CodeType.AUDIO, CodeType.CANVAS
        , CodeType.IMAGE, CodeType.SYNC, CodeType.MAP, CodeType.MENU, CodeType.TOOLBAR, CodeType.POPUP, CodeType.PICKER, CodeType.PROGRESSBAR, CodeType.SCROLLVIEW
        , CodeType.SEGMENT, CodeType.SLIDER, CodeType.GRIDVIEW, CodeType.SWITCH, CodeType.TAB_BAR, CodeType.LISTVIEW, CodeType.EDITTEXT, CodeType.TEXTVIEW
        , CodeType.WEBVIEW, CodeType.CHART, CodeType.GAME, CodeType.CORE_MOTION, CodeType.DATABASE, CodeType.EBOOK, CodeType.GESTURE, CodeType.GUIDE_VIEW
        , CodeType.NETWORKING, CodeType.SHARE, CodeType.VIEW_EFFECT, CodeType.VIEW_LAYOUT, CodeType.VIEW_TRANSITION, CodeType.OTHERS})
@Retention(RetentionPolicy.SOURCE)
public @interface CodeType {
    int TIP=500;
    int DIALOG=1000;
    int BUTTON=1500;
    int CALENDAR=2000;
    int CAMERA=2500;
    int HUD=3000;
    int IMAGE=3500;
    int FILE=4000;
    int SYNC=4500;
    int MAP=5000;
    int MENU=5500;
    int TOOLBAR=6000;
    int PICKER=6500;
    int PROGRESSBAR=7000;
    int SCROLLVIEW=7500;
    int SEGMENT=8000;
    int SLIDER=8500;
    int GRIDVIEW=9000;
    int SWITCH=9500;
    int TAB_BAR=10000;
    int LISTVIEW=10500;
    int EDITTEXT=11000;
    int TEXTVIEW=11500;
    int WEBVIEW=12000;
    int ANIMATION=12500;
    int AUDIO=13000;
    int CHART=13500;
    int GAME=14000;
    int CORE_MOTION=14500;
    int DATABASE=15000;
    int CANVAS=15500;
    int EBOOK=16000;
    int GESTURE=16500;
    int GUIDE_VIEW=17000;
    int NETWORKING=17500;
    int POPUP=18000;
    int SHARE=18500;
    int VIEW_EFFECT=19000;
    int VIEW_LAYOUT=19500;
    int VIEW_TRANSITION=20000;
    int OTHERS=20500;
}
