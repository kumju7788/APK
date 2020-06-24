package andhook.test;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLStreamHandler;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import andhook.lib.HookHelper;

import static andhook.test.BytesUtil.serialize;


public final class AppHooking {

    private static final int HOOK_APP_HOOKING_CLASS         = 0;
    private static int requestIndex = 0;
    private static int responseIndex = 0;
    static Class<?>[] rClasses;
    static int rClassCount;
    public static boolean isSlidingPanOpened = false;
    private static final String TAG = HookInit.TAG;

    public static Class<?> SlidingPaneLayout;
    private static final Object critical = new Object();

    private static boolean logStart = false;
    public static String logPath;

    public static void init() {
        Log.d(TAG, "Java <AppHooking>..");
        AppHooking.logPath = createLogPath();
        Log.d(TAG, "log path = " + logPath);

    }

    public static void onRelationClassLoad(Class<?>[] relationClasses, int length) {
        synchronized (critical) {
            rClasses = relationClasses;
            rClassCount = length;
        }
//        for (int i = 0; i < length; i++) {
//            Log.d(TAG, "[setRelationClass] Relation Class : " + rClasses[i].getName());
//        }
    }

    public static synchronized Class<?> getRelationClass(String clsName) {
        synchronized (critical) {
            for (int i = 0; i < rClassCount; i++) {
                if (rClasses[i].getName().equals(clsName)) {
                    Log.d(TAG, "[Relation class] : " + clsName);
                    return rClasses[i];
                }
            }
        }
        return null;
    }

    public static void onHook(Class<?> clazz, int index){
        HookThread hkt = new HookThread(clazz, index);
        //hkt.setRelationClass(rClasses, rClassCount);
        hkt.start();
        Log.d(TAG, "[onHook] Java hook : " + clazz.getName());
    }

    public static Object myFromJSon(final Class<?> clazz, String str, Class<?> paramCls){
        //Log.d(TAG, "Gson:(From)JSon ==> cls=" + paramCls.getName() + ", str=" + str);
        return HookHelper.invokeObjectOrigin(clazz, str, paramCls);
    }

    public static String myToJSon(final Class<?> clazz, Object paramCls){
        String jsonStr = HookHelper.invokeObjectOrigin(clazz, paramCls);
        //Log.d(TAG, "Gson:(To)JSon <== cls=" + paramCls.getClass().getName() + ", str=" + jsonStr);
        if(jsonStr.contains("pokeConditionId")) {
            logStart = true;
        }
        //new Throwable().printStackTrace();
        return jsonStr;
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

    public static Object myURLOpenConnection(final Class<?> clazz){
        Log.d(TAG, ">> myURLOpenConnection : ");
        //new Throwable().printStackTrace();
        return HookHelper.invokeObjectOrigin(clazz);
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
        Log.d(TAG, "VERBOSE : " + str1 + " => " + str2);
        HookHelper.invokeVoidOrigin(clazz, str1, str2);
    }

    public static Object myOkhttp3_HttpProceed(final Class<?> clazz, Object request, Class<?> fVar, Class<?> cVar, Class<?> aVar){
        if(logStart) {
            Log.d("HTTP", ">>>" + request.hashCode() + "-[S:--myOkhttp3_HttpProceed()  start" + request.toString());
            //getResponseInfo(request, HookThread.HOOK_OKHTTP3_HTTP_PROCEED, request.hashCode());
        }
        Object obj = HookHelper.invokeObjectOrigin(clazz, request, fVar, cVar, aVar);
        //Log.d("HTTP", ">>>-[S:--myOkhttp3_HttpProceed()  end");
        return obj;
    }

    public static Object myRetrofitExecute(final Class<?> clazz){
        Log.d(TAG, "<<<-[R:" + String.format("%04d", responseIndex) + "[---myRetrofitExecute build() start");
        Object response = HookHelper.invokeObjectOrigin(clazz);
//        if(logStart) {
            getResponseInfo(response, HookThread.HOOK_RETROFIT_RESPONSE, response.hashCode());
//        }
        Log.d(TAG, "<<<-" + response.hashCode() + "-[R:" + String.format("%04d", responseIndex) + "[---myRetrofitExecute build() end");
        responseIndex++;
        return response;
    }

    public static Object myOkhttpExecute(final Class<?> clazz) {
        Log.d(TAG, "<<<-[R:" + String.format("%04d", responseIndex) + "]---myOkhttpExecute _start ");
        Object response = HookHelper.invokeObjectOrigin(clazz);
//        if(logStart) {
            getResponseInfo(response, HookThread.HOOK_OKHTTP3_RESPONSE_BUILDER, response.hashCode());
//        }
        Log.d(TAG, "<<<-" + response.hashCode() + "-[R:" + String.format("%04d", responseIndex) + "]---myOkhttpExecute end  ");
        responseIndex ++;
        return response;
    }

    public static Object myRetrofitResponseConstructor(final Class<?> clazz, Class<?>response, Class<?>body, Class<?>errBody) {
        Log.d(TAG, "<<<-[R:" + String.format("%04d", responseIndex) + "]---myRetrofitResponseConstructor _start ");
        Object objResponse = HookHelper.invokeObjectOrigin(clazz, response, body, errBody);
        //getResponseInfo(cloneResponse, HookThread.HOOK_RETROFIT_RESPONSE_ASYNC, cloneResponse.hashCode());
        Log.d(TAG, "<<<-" + objResponse.hashCode() + "-[R:" + String.format("%04d", responseIndex) + "]---myRetrofitResponseConstructor end  ");
        responseIndex ++;
        return objResponse;
    }

    public static Object myRequestBuilder_builder(final Class<?> clazz) {
        Log.d(TAG, ">>>-[S:" + String.format("%04d", requestIndex) + "[---myRequestBuilder build() start");
        Object request = HookHelper.invokeObjectOrigin(clazz);
//        if(logStart) {
//            RequestInfo requestInfo = new RequestInfo(request);
//            requestInfo.start();
//        }
        Log.d(TAG, ">>>-[S:" + String.format("%04d", requestIndex) + "]---myRequestBuilder build() end");
        requestIndex ++;
        return request;
    }

    private static void getResponseInfo(Object response, int responseType, int identify){
        if(responseType == HookThread.HOOK_OKHTTP3_RESPONSE_BUILDER) {
//            Log.d("HTTP", "\t" + identify + "-[" + "OKHTTP" + "-RESPONSE] : " + response.toString());
            ResponseInfo responseInfo = new ResponseInfo(response, identify, "OKHTTP");
            responseInfo.start();
        }
        else if(responseType == HookThread.HOOK_RETROFIT_RESPONSE) {
//            Log.d("HTTP", "\t" + identify + "-[" + "RETROFIT" + "-RESPONSE] : " + response.toString());
            Retrofit retrofitInfo = new Retrofit(response, identify, "RETROFIT");
            retrofitInfo.start();
        }
        else if(responseType == HookThread.HOOK_RETROFIT_RESPONSE_ASYNC) {
//            Retrofit retrofitInfo = new Retrofit(response, identify, "RETROFIT_ASYNC");
//            retrofitInfo.start();
        }
        else if(responseType == HookThread.HOOK_OKHTTP3_HTTP_PROCEED) {
            ResponseInfo requestInfo = new ResponseInfo(response, identify, "OKHTTP_PROCEES");
            requestInfo.setOnlyRequest(true);
            requestInfo.start();
        }
    }

    public static JSONObject my_JsonObjectPut(final Class<?> clazz, String key, Object value){
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
        Log.d(TAG, "Methos : find");
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


    public static String createLogPath() {
        try {
//            ApplicationInfo ai = AppInfo.GetAppInfo();
//            String logPath = ai.dataDir + "/log";
            @SuppressLint("SdCardPath")
            String logPath = "/data/user/0/com.smile.gifmaker/log";
            Log.d(TAG, "path = " + logPath);
            File dir = new File(logPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File[] files = dir.listFiles();
            for (File f: files) {
                if(f.getName().contains("-1") || f.getName().contains("-2")) {
                    f.delete();
                }
            }
            Log.d(TAG, "folder create success : " + logPath);
            return logPath;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return "/data/local/tmp";
    }


}


