package andhook.test;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import andhook.lib.HookHelper;


public final class AppHooking {

    private static final int HOOK_APP_HOOKING_CLASS         = 0;

    static Class<?>[] rClasses;
    static int rClassCount;
    public static boolean isSlidingPanOpened = false;
    private static final String TAG = HookInit.TAG;

    public static Class<?> SlidingPaneLayout;

    public static void init()
    {
        Log.d(TAG, "Java <AppHooking>..");
    }

    public static void setRelationClass(Class<?>[] relationClasses, int length) {
        rClasses = relationClasses;
        rClassCount = length;
        for (int i = 0; i < length; i++) {
            Log.d(TAG, "[setRelationClass] Relation Class : " + rClasses[i].getName());
        }
    }


    public static void awemeHook(Class<?> clazz, int index){
        HookThread hkt = new HookThread(clazz, index);
        hkt.setRelationClass(rClasses, rClassCount);
        hkt.start();
        Log.d(TAG, "[awemeHook] Java hook : " + clazz.getName());
    }

    public static void aFunc1(final Class<?> clazz, Class<?> paramCls, String str){
        Log.d(TAG, "a(Class, String) callee ------");
        HookHelper.invokeVoidOrigin(clazz, paramCls, str);
    }

    public static void myCheckFullScreen(final Class<?> clazz, boolean enable, int flag){
        Log.d(TAG, "myCheckFullScreen : enable = " + enable + ", flag = " + flag);
        HookHelper.invokeVoidOrigin(clazz, enable, flag);
        settFullScreenMode(enable, flag);
    }

    public static void myCrashEvent(final Class<?> clazz) throws NoSuchFieldException, IllegalAccessException {
        Log.d(TAG, "HOOK_CRASH_EVENT : a() ------");
        HookHelper.invokeVoidOrigin(clazz);
    }

    public static boolean myTTNetCheck(final Class<?> clazz){
        Log.d(TAG, ">> myTTNetCheck : a() callee ------");

        boolean ret = HookHelper.invokeBooleanOrigin(clazz);
        if(ret)
            Log.d(TAG, ">> Depend : a() : true");
        else {
            ret = true;
            Log.d(TAG, ">> Depend : a() : false ==> true");
        }
        new Throwable().printStackTrace();
        return ret;
    }

    public static boolean LivePlaza_Click(final Class<?> clazz, MotionEvent motion) {
        Log.d(TAG, "+++++++ myGetHomeActivityClass +++++++");
        Log.d(TAG, "HomeActivity : n is " + motion.toString() );
        return HookHelper.invokeBooleanOrigin(clazz, motion);
    }

    public static boolean CheckSlidingPanOpened(final Class<?> clazz, View view, int n) {
        Log.d(TAG, "+++++++ CheckSlidingPanOpened +++++++");
//        new Throwable().printStackTrace();
        isSlidingPanOpened = HookHelper.invokeBooleanOrigin(clazz, view, n);
        setScreenModeIconPos();
        Log.d(TAG, "isSlidingPanOpened = " + isSlidingPanOpened );
        return isSlidingPanOpened;
    }

    public static boolean CheckSlidingPanClosed(final Class<?> clazz, View view, int n) {
        boolean ret;
        Log.d(TAG, "+++++++ CheckSlidingPanClosed +++++++");
//        new Throwable().printStackTrace();
        ret = HookHelper.invokeBooleanOrigin(clazz, view, n);
        isSlidingPanOpened = !ret;
        Log.d(TAG, "isSlidingPanOpened = " + isSlidingPanOpened );
        return isSlidingPanOpened;
    }

    public static void my_logDbg(final Class<?> clazz, String str1, String str2) {
        boolean ret;
        Log.d(TAG, "DBG : " + str1 + " => " + str2);
        HookHelper.invokeVoidOrigin(clazz, str1, str2);
    }

    public static void my_logInfo(final Class<?> clazz, String str1, String str2) {
        boolean ret;
        Log.d(TAG, "INFO : " + str1 + " => " + str2);
        HookHelper.invokeVoidOrigin(clazz, str1, str2);
    }

    public static void my_logVerb(final Class<?> clazz, String str1, String str2) {
        boolean ret;
        Log.d(TAG, "VERBOSE : " + str1 + " => " + str2);
        HookHelper.invokeVoidOrigin(clazz, str1, str2);
    }


    public static JSONObject my_JsonObjectPut(final Class<?> clazz, String key, Object value){
        boolean find = false;

        // 메소드 Trace정보를 보기위한 조작
//        if(key.equals("activity") && value.equals("SF2020")) {
//            Log.d(TAG, ">>> JSONObject Trace.... \n");
//            new Throwable().printStackTrace();
//        }
        Log.d(TAG, "JSONObject put :" + key + " = " + value + "\n");
        return HookHelper.invokeObjectOrigin(clazz, key, value);
    }

    public static JSONObject my_JsonBooleanPut(final Class<?> clazz, String key, boolean value){
        Log.d(TAG, "JSONObject put :" + key + " = " + value + "\n");
        return HookHelper.invokeObjectOrigin(clazz, key, value);
    }


    public static void appClassTest(Class<?> clazz, int reserved)
    {
        Log.d(TAG, "----------------------------------------------------");
        Log.d(TAG, "Class : " + clazz.getName());
        StringBuffer sb = new StringBuffer();
        Method[] methods = clazz.getMethods();
        for (Method md: methods) {
            sb.append(md.getName());
            Class<?>[] argTypes = md.getParameterTypes();
            sb.append("(");
            int size = argTypes.length;
            for( Class<?> argType : argTypes ){
                String argName = argType.getName();
                sb.append(argName);
                if( --size != 0 ){
                    sb.append(", ");
                }
            }
            sb.append(")");
            Class<?> retType = md.getReturnType();
            sb.append(" : " + retType.getName());
            Log.d(TAG, "Test class method : " + sb + "\n");
            sb.delete(0, sb.toString().length());
        }

        methods = clazz.getDeclaredMethods();
        for (Method md: methods) {
            sb.append(md.getName());
            Class<?>[] argTypes = md.getParameterTypes();
            sb.append("(");
            int size = argTypes.length;
            for( Class<?> argType : argTypes ){
                String argName = argType.getName();
                sb.append(argName);
                if( --size != 0 ){
                    sb.append(", ");
                }
            }
            sb.append(")");
            Class<?> retType = md.getReturnType();
            sb.append(" : " + retType.getName());
            Log.d(TAG, "Test class declared method : " + sb + "\n");
            sb.delete(0, sb.toString().length());
        }

        Field[] fields = clazz.getFields();
        for (Field fd: fields) {
            Log.d(TAG, "[*]Test class field : " + fd.getName());
        }
        fields = clazz.getDeclaredFields();
        for (Field fd: fields) {
            Log.d(TAG, "[*]Test class field : " + fd.getName());
        }

        Log.d(TAG, "----------------------------------------------------");

    }

    public static void findChildren(View v, ArrayList<View> chVArray) {
        ViewGroup viewGroup = (ViewGroup)v;
        for (int i=0; i<viewGroup.getChildCount(); i++) {
            View chV = viewGroup.getChildAt(i);
            if (chV instanceof ViewGroup) {
                findChildren(chV, chVArray);
                //Log.d(TAG, "*** Group View " + chV.toString() + " ID is: " + Integer.toString(chV.getId()));
            } else {
                String viewClassName = chV.getClass().getName();
                //Log.d(TAG, "*** Single View " + viewClassName + " ID is: " + Integer.toString(chV.getId()));
            }
            chVArray.add(chV);
        }
    }

    public static View findViewContainID(String id) {
        Activity topActivity;
        topActivity = GetActivity.getRunningActivity();
        ViewGroup viewGroup = (ViewGroup) topActivity.getWindow().getDecorView();
        ArrayList<View> lv = new ArrayList<>();
        findChildren(viewGroup, lv);
        for(int i = 0; i < lv.size(); i++) {
            View tmv = lv.get(i);
            if( tmv instanceof TextView) {
                TextView tv = (TextView)tmv;
                int[] loc = EventAction.getViewLocation(tv);
                //Log.d(TAG, "TextView " + tv.getText() + ", x=" + loc[0] + " y=" + loc[1]);
                if(tv.getText().equals(id))
                    return tmv;
            }
            if(tmv.toString().contains(id)) {
                //Log.d(TAG, "FindView " + tmv.toString());
                return tmv;
            }
            //int[] tt = EventAction.getViewLocation(tmv);
            //Log.d(TAG, "TextView " + tmv.toString() + ", x=" + tt[0] + " y=" + tt[1]);

        }
        return null;
    }

    static void settFullScreenMode(boolean enable, int flag) {
        synchronized (AutoEngine.class) {
            if(enable && flag == 2) {
                AutoEngine.isScreenModeChanged = true;
            }
            else if(enable && flag == 4)
                AutoEngine.isScreenModeChanged = false;
        }
    }

    static void setScreenModeIconPos() {
        View v = findViewContainID("browse_settings_notify");
        if(v != null) {
            synchronized (AutoEngine.class) {
                AutoEngine.posScreenModeIcon = EventAction.getViewLocation(v);
            }
        }
    }

    static void setMainMenuIconPos() {
        View v = findViewContainID("home_sliding_menu_layout");
        if(v != null) {
            synchronized (AutoEngine.class) {
                AutoEngine.posMainMenuIcon = EventAction.getViewLocation(v);
            }
        }
    }

}


