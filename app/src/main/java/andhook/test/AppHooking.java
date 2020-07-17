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
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
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
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLStreamHandler;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLSocket;
import javax.security.cert.CertificateEncodingException;
import javax.security.cert.X509Certificate;

import andhook.lib.HookHelper;

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
    private static String[] urls;

    public static void init() {
        Log.d(TAG, "Java <AppHooking>..");
        logPath = FileUtils.createLogPath();
        Log.d(TAG, "log path = " + logPath);
        urls = new String[] {
                "n/user/mobile/checker",
                "n/user/requestMobileCode",
                "n/user/login/mobileVerifyCode",
                "n/user/profile",
                "n/token/infra/getServiceToken",
                "n/live/authStatus",
                "n/key/refresh/contact",
                "infra/push/token/ks/bind/android",
        };


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
        Log.d(TAG, "Gson:(From)JSon ==> cls=" + paramCls.getName() + ", str=" + str);
        return HookHelper.invokeObjectOrigin(clazz, str, paramCls);
    }

    public static String myToJSon(final Class<?> clazz, Object paramCls){
        String jsonStr = HookHelper.invokeObjectOrigin(clazz, paramCls);
        Log.d(TAG, "Gson:(To)JSon <== cls=" + paramCls.getClass().getName() + ", str=" + jsonStr);
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

    public static JSONObject my_JsonObjectPut(final Class<?> clazz, String key, Object value){
        Log.d(TAG, "JSONObject put :" + key + " = " + value + "\n");
        return HookHelper.invokeObjectOrigin(clazz, key, value);
    }

    public static JSONObject my_JsonBooleanPut(final Class<?> clazz, String key, boolean value){
        Log.d(TAG, "JSONObject put :" + key + " = " + value + "\n");
        return HookHelper.invokeObjectOrigin(clazz, key, value);
    }

    public static Object myOkhttp3ProceedConst(final Class<?> clazz,
                                               List interceptors, Class fVar, Class cVar, Class cVar2, int i,
                                               Object request2, Class dVar, Class oVar, int i2, int i3, int i4){
        StringBuilder sb = new StringBuilder();
//        sb.append("[" + interceptors.hashCode() + "]-----------------------------------------------------\n");
//        sb.append(" " + interceptors.hashCode() + " " + i + " " + interceptors.get(i).getClass().getName() + "\n");
//        if(oVar != null)
//            sb.append("\t| eventListener=" + (oVar.getName().isEmpty() ? "" : oVar.getName() + "\n"));
//        if(cVar2 != null)
//            sb.append("\t| connection=" + cVar2.getName());
//        Log.d("HTTP", String.valueOf(sb));
        Object objRequest = request2;
        RequestInfo request = new RequestInfo(objRequest, objRequest.hashCode(), "PROCEED_REQUEST");
        Object obj = HookHelper.invokeObjectOrigin(clazz, interceptors, fVar, cVar, cVar2, i, request2, dVar, oVar, i2, i3, i4);

        return obj;
    }

    public static Object myOkhttp3_HttpProceed(final Class<?> clazz, Object request, Class<?> fVar, Class<?> cVar, Class<?> aVar){
 //       getRequestInfo(request,request.hashCode());
 //       new Throwable().printStackTrace();
        Object response = HookHelper.invokeObjectOrigin(clazz, request, fVar, cVar, aVar);
//        if(logStart) {
            //getResponseInfo(response, HookThread.HOOK_OKHTTP3_HTTP_PROCEED, response.hashCode());
//        }
//        Log.d("HTTP", "<<<" + request.hashCode() + "-[R:--myOkhttp3_HttpProceed()  end");
        return response;
    }

    public static Object myRetrofitExecute(final Class<?> clazz){
       // Log.d(TAG, "<<<-[R:" + String.format("%04d", responseIndex) + "[---myRetrofitExecute build() start");
        Object response = HookHelper.invokeObjectOrigin(clazz);
//        if(logStart) {
 //           getResponseInfo(response, HookThread.HOOK_RETROFIT_RESPONSE, response.hashCode());
//        }
//        Log.d(TAG, "<<<-" + response.hashCode() + "-[R:" + String.format("%04d", responseIndex) + "[---myRetrofitExecute build() end");
        return response;
    }

    public static Object myOkhttpExecute(final Class<?> clazz) {
//        Log.d(TAG, "<<<-[R:" + String.format("%04d", responseIndex) + "]---myOkhttpExecute _start ");

        Object response = HookHelper.invokeObjectOrigin(clazz);
//        if(logStart) {

        getResponseInfo(response, HookThread.HOOK_OKHTTP3_RESPONSE_BUILDER, response.hashCode());
//        }
//        Log.d(TAG, "<<<-" + response.hashCode() + "-[R:" + String.format("%04d", responseIndex) + "]---myOkhttpExecute end  ");
        responseIndex ++;
        return response;
    }

    public static Object mySetResponseBody(final Class<?> clazz, Class<?> contenTypet, long length, Class<?> content) {
 //       Log.d(TAG, "<<<<<<<<< mySetResponseBody : length " + length);
        Object objResponse = HookHelper.invokeObjectOrigin(clazz, contenTypet, length, content);
 //       Log.d(TAG, "<<<<<<<<< mySetResponseBody : body " + objResponse.hashCode() );
        //getResponseInfo(objResponse, HookThread.HOOK_SET_RESPONSE_BODY, objResponse.hashCode());
        return objResponse;
    }

    public static Object myRequestBuilder_builder(final Class<?> clazz) {
//        Log.d(TAG, ">>>-[S:" + String.format("%04d", requestIndex) + "[---myRequestBuilder build() start");
        Object request = HookHelper.invokeObjectOrigin(clazz);
//        if(logStart) {
           //RequestInfo requestInfo = new RequestInfo(request, request.hashCode());
           //requestInfo.start();
//        }
//        Log.d(TAG, ">>>-[S:" + String.format("%04d", requestIndex) + "]---myRequestBuilder build() end");
        //requestIndex ++;
        return request;
    }

    public static Pair<String, String> NStokensigParam(Class<?> clazz, Class<?> request, Map<String, String> map, Map<String, String> map2) {
        Log.d(TAG, ">>>>>>>>>>> NStokensigParam :");
        byte[] bytes = requestParam(map, map2);
        Pair<String, String> obj = HookHelper.invokeObjectOrigin(clazz, request, map, map2);
        return obj;
    }

    public static String NStokensigParam1(Class<?> clazz, byte[] bytes) {
        Log.d(TAG, ">>>>>>>>>>> NStokensigParam_1 :");
        if(bytes.length > 16){
            new Throwable().printStackTrace();
            DbgLog logHex = new DbgLog();
            logHex.LogByteArray(bytes, bytes.length);
        }
        String hashVal = HookHelper.invokeObjectOrigin(clazz, bytes);
        return hashVal;
    }

    public static Pair myNStokenHashVal(Class<?> clazz,Object cls, String str1, String str2) {
        Log.d(TAG, ">>>>>>>>>>> HASH input param before:" + cls.getClass().getName() + ", " + str1 + ", " + str2);
//        new Throwable().printStackTrace();
        Pair obj  = HookHelper.invokeObjectOrigin(clazz, cls, str1, str2);
        Log.d(TAG, ">>>>>>>>>>> HASH input return value :" + obj.first + "=" + obj.second);
        return obj;
    }

    //장치정보
    public static Object myFuncTest_1(Class<?> clazz, Map<String, String> map) {
        Log.d(TAG, ">> myFuncTest_1 start... ");
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for(Map.Entry<String, String>tmpEntry: entries) {
            Log.d(TAG, "device info : " + tmpEntry.getKey() + "=" + tmpEntry.getValue());
        }
        return HookHelper.invokeObjectOrigin(clazz, map);
    }

    public static String myFuncTest_2(Class<?> clazz, String str, String str2, Map<String, String> map, Map<String, String> map2, String str3) {
        Log.d(TAG, ">> myFuncTest_2 (request)start... ");
        //new Throwable().printStackTrace();
        //String ss = getClientSignParam(str, str2, map, map2, str3);
        String encode = HookHelper.invokeObjectOrigin(clazz, str, str2, map, map2, str3);
        Log.d(TAG, ">> __clientSign : " + encode);
        Log.d(TAG, ">> myFuncTest_2 (request)end... ");
        return encode;
    }

    public static Object myFuncTest_3(Class<?> clazz, byte[] key, String algorithm) {
        Log.d(TAG, ">> myFuncTest_3 start... ");
        DbgLog hexlog = new DbgLog();
        hexlog.LogByteArray(key, key.hashCode());
        Object obj = HookHelper.invokeObjectOrigin(clazz, key, algorithm);
        return obj;
    }


    public static byte[] myFuncTest_4(Class<?> clazz, byte[] bytes) {
        Log.d(TAG, ">> myFuncTest_4 start... ");
        String ss = new String(bytes);
        Log.d(TAG, "__clientSign param [" + bytes.hashCode() + "]\t" + ss);
        byte[] encode = HookHelper.invokeObjectOrigin(clazz, bytes);
        Log.d(TAG, "---------------------------");
        DbgLog hexlog = new DbgLog();
        hexlog.LogByteArray(encode, encode.hashCode());
        Log.d(TAG, "---------------------------");
        return encode;
    }
    public static void myFuncTest_5(Class<?> clazz, Class<?> cls, String str){
        Log.d(TAG, ">> myFuncTest_5 start... | " + str);
        new Throwable().printStackTrace();
        HookHelper.invokeVoidOrigin(clazz, cls, str);
    }

    public static Object myFuncTest_6(Class<?> clazz){
        Object cet = HookHelper.invokeObjectOrigin(clazz);
        Log.d(TAG, ">> myFuncTest_6 | " + cet.getClass().getName());
        return  cet;
    }


    private static void getRequestInfo(Object request, int identify) {
        RequestInfo info = new RequestInfo(request, identify);
        info.start();
    }

    private static void getResponseInfo(Object response, int responseType, int identify){
        if(responseType == HookThread.HOOK_OKHTTP3_RESPONSE_BUILDER) {
//            Log.d("HTTP", "\t" + identify + "-[" + "OKHTTP" + "-RESPONSE] : " + response.toString());
//            if(logStart || response.toString().contains("n/user/mobile/checker")) {
//                logStart = true;

                ResponseInfo responseInfo = new ResponseInfo(response, identify, "OKHTTP");
                //responseInfo.setOnlyRequest(true);
                responseInfo.start();
//            }
        }
        else if(responseType == HookThread.HOOK_RETROFIT_RESPONSE) {
//            Log.d("HTTP", "\t" + identify + "-[" + "RETROFIT" + "-RESPONSE] : " + response.toString());
            ResponseInfo responseInfo = new ResponseInfo(response, identify, "RETROFIT");
            responseInfo.start();
        }
        else if(responseType == HookThread.HOOK_OKHTTP3_HTTP_PROCEED) {
//            if(checkUrl(response.toString())) {
//            if(logStart || response.toString().contains("n/user/mobile/checker")) {
//                logStart = true;
                ResponseInfo responseInfo = new ResponseInfo(response, identify, "OKHTTP_PROCEED");
                responseInfo.start();

//            }
        }
        else {
            ResponseInfo requestInfo = new ResponseInfo(response, identify, "TEST");
            requestInfo.start();
        }

    }


    public static Object a(StringBuilder sb, String str, String str2, Map.Entry entry) {
        sb.append(str);
        sb.append(str2);
        return entry.getValue();
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static byte[] requestParam(Map<String, String> map, Map<String, String> map2) {
        String str;
        String str2;
        ArrayList arrayList = new ArrayList(map2.size() + map.size());
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (true) {
            str = "";
            if (!it.hasNext()) {
                break;
            }
            Map.Entry next = it.next();
            StringBuilder sb = new StringBuilder();
            if (a(sb, (String) next.getKey(), "=", next) != null) {
                str = (String) next.getValue();
            }
            sb.append(str);
            arrayList.add(sb.toString());
        }
        for (Map.Entry next2 : map2.entrySet()) {
            StringBuilder sb2 = new StringBuilder();
            if (a(sb2, (String) next2.getKey(), "=", next2) == null) {
                str2 = str;
            } else {
                str2 = (String) next2.getValue();
            }
            sb2.append(str2);
            arrayList.add(sb2.toString());
        }
        try {
            Collections.sort(arrayList);
            for (int i = 0; i < arrayList.size(); i++) {
                Log.d(TAG, "ArrList : " + arrayList.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TextUtils.join(str, arrayList).getBytes(StandardCharsets.UTF_8);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getClientSignParam(String str, String str2, Map<String, String> map, Map<String, String> map2, String str3) {
        byte[] bArr;
        if (TextUtils.isEmpty(str3)) {
            return "";
        }
        HashMap hashMap = new HashMap();
        if (map != null) {
            hashMap.putAll(map);
        }
        if (map2 != null) {
            hashMap.putAll(map2);
        }
        if (TextUtils.isEmpty(str3)) {
            return "";
        }
        Log.d(TAG, "Client Sine key value : " + str3);

        ArrayList arrayList = new ArrayList();
        arrayList.add(str.toUpperCase().trim());
        arrayList.add(str2.trim());
        ArrayList arrayList2 = new ArrayList();
        Set<Map.Entry<String, String>> entries = hashMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (!(entry.getKey()).startsWith("__")) {
                StringBuilder sb = new StringBuilder();
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue() != null ? entry.getValue() : "");
                arrayList2.add(sb.toString());
            }
        }
        try {
            Collections.sort(arrayList2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        arrayList.addAll(arrayList2);
        int tmVal = (int) (System.currentTimeMillis() / TimeUnit.MINUTES.toMillis(1));
        int rndVal = new Random().nextInt();
        Log.d(TAG, "Client Sine time value : " + Integer.toHexString(tmVal));
        Log.d(TAG, "Client Sine random value : " + Integer.toHexString(rndVal));
        long nextInt = ((long)rndVal << 32) | ((long)tmVal );
        Log.d(TAG, "Client Sine Random + time value : " + Long.toHexString(nextInt));
        arrayList.add(String.valueOf(nextInt).trim());
        for (int i = 0; i < arrayList.size(); i++) {
            Log.d(TAG, "Client Sine ArrList : " + arrayList.get(i));
        }
        String join = TextUtils.join("&", arrayList);
        byte[] decode = Base64.decode(str3, 0);
        byte[] bytes = join.getBytes(StandardCharsets.UTF_8);
        DbgLog hexlog = new DbgLog();
        hexlog.LogByteArray(decode, decode.hashCode());
        hexlog.LogByteArray(bytes, bytes.hashCode());
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(decode, "HmacSHA256");
            Mac instance = Mac.getInstance(secretKeySpec.getAlgorithm());
            instance.init(secretKeySpec);
            bArr = instance.doFinal(bytes);
        } catch (Exception unused) {
            bArr = new byte[0];
        }
        long j2 = nextInt;
        byte[] bArr2 = new byte[8];
        for (int i = 7; i >= 0; i--) {
            bArr2[i] = (byte) ((int) (255 & j2));
            j2 >>= 8;
        }
        byte[] bArr3 = new byte[(bArr.length + 8)];
        System.arraycopy(bArr2, 0, bArr3, 0, 8);
        System.arraycopy(bArr, 0, bArr3, 8, bArr.length);
        String ret = Base64.encodeToString(bArr3, 11);
        Log.d(TAG, "Client Sine Base64 encode value : " + ret);
        return ret;

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


    private static boolean checkUrl(String url) {
        for (String u: urls) {
            if(url.contains(u)) {
                return true;
            }
        }
        return false;
    }


}


