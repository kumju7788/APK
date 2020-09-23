package andhook.test;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.JsonWriter;
import android.util.Log;
import android.util.LruCache;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLStreamHandler;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
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

    public static Class<?> clsTest1 = null;
    public static Class<?> clsTest2 = null;
    public static Class<?> clsTest3 = null;

    public static void init() {
        Log.d(TAG, "Java <AppHooking>..");
        logPath = FileUtils.createLogPath();
        Log.d(TAG, "log path = " + logPath);
        urls = new String[] {
//                "n/user/mobile/checker",
//                "n/user/requestMobileCode",
//                "n/user/login/mobileVerifyCode",
//                "n/user/profile",
//                "n/token/infra/getServiceToken",
//                "n/live/authStatus",
//                "n/key/refresh/contact",
//                "infra/push/token/ks/bind/android",
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
//            getResponseInfo(response, HookThread.HOOK_OKHTTP3_HTTP_PROCEED, response.hashCode());
//        }
//        Log.d("HTTP", "<<<" + request.hashCode() + "-[R:--myOkhttp3_HttpProceed()  end");
        return response;
    }

    public static Object myRetrofitExecute(final Class<?> clazz){
       // Log.d(TAG, "<<<-[R:" + String.format("%04d", responseIndex) + "[---myRetrofitExecute build() start");
        Object response = HookHelper.invokeObjectOrigin(clazz);
//        if(logStart) {
//            getResponseInfo(response, HookThread.HOOK_RETROFIT_RESPONSE, response.hashCode());
//        }
//        Log.d(TAG, "<<<-" + response.hashCode() + "-[R:" + String.format("%04d", responseIndex) + "[---myRetrofitExecute build() end");
        return response;
    }

    public static Object myOkhttpExecute(final Class<?> clazz) {
//        Log.d(TAG, "<<<-[R:" + String.format("%04d", responseIndex) + "]---myOkhttpExecute _start ");

        Object response = HookHelper.invokeObjectOrigin(clazz);
//        if(logStart) {

        //getResponseInfo(response, HookThread.HOOK_OKHTTP3_RESPONSE_BUILDER, response.hashCode());
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
//        Log.d(TAG, ">>>>>>>>>>> NStokensigParam :");
//        byte[] bytes = requestParam(map, map2);
        Pair<String, String> obj = HookHelper.invokeObjectOrigin(clazz, request, map, map2);
        return obj;
    }


    public static String NStokensigParam1(Class<?> clazz, byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        //Log.d(TAG, "------------- NStokensigParam_1 :" + "\n");
        boolean is = false;
        //if(bytes.length > 16){
        //    String s = new String(bytes);
        //Log.d(TAG,  "\tNStokensigParam_1 | input=" + s + "\n");
            is = true;
        //}
        String hashVal = HookHelper.invokeObjectOrigin(clazz, bytes);
        //if(is)
            //Log.d(TAG, "\tNStokensigParam_1 | output=" + hashVal + "\n");

        //Log.d(TAG, sb.toString());
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
    public static Object myDeviceInfoParam(Class<?> clazz, Map<String, String> map) {
        int count = 1;
        Log.d(TAG, ">> myFuncTest_1 start... ");
//        for(count = 1; count <= 102; count++) {
//            String k = "k" + count;
//            Log.d(TAG, "device info : " + k + "=" + map.get(k));
//        }
        return HookHelper.invokeObjectOrigin(clazz, map);
    }

    //CarryInfo 파라메터
    public static int myCarryInfoParam(Class<?> clazz, String str, String str2, boolean z, Bundle bundle) {
        int count = 1;
        Log.d(TAG, ">> myCarryInfoParam start... ");
        Log.d(TAG, ">> myCarryInfoParam | str2=" + str2);
        Log.d(TAG, ">> myCarryInfoParam | str=" + str);
        return HookHelper.invokeObjectOrigin(clazz, str, str2, z, bundle);
    }

    public static byte[] atlasEncrypt(Class<?> clazz, String str, String str2, int i, byte[] bytes) {
        Log.d(TAG, ">> atlasEncrypt (request)start...|length = " + bytes.length);
        new Throwable().printStackTrace();
        if(bytes[0] == '[') {
            try {
                JSONArray testV=new JSONArray(new String(bytes));
                FileWriter file = new FileWriter("/data/data/com.smile.gifmaker/log/carry.json");
                file.write(String.valueOf(testV));
                file.flush();
                file.close();
                Log.d(TAG, "/data/data/com.smile.gifmaker/log/carry.json file created.");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, ">> device info param | input param length =" + bytes.length);
            DbgLog hexLog = new DbgLog();
            hexLog.LogByteArray(bytes, bytes.hashCode());
        }
        byte[] encode = HookHelper.invokeObjectOrigin(clazz, str, str2, i, bytes);
        Log.d(TAG, ">> device info param | output bytes length =" + encode.length);
        Log.d(TAG, ">> atlasEncrypt (request)end... ");
        return encode;
    }

    public static void myFuncTest_3(Class<?> clazz, Object obj){
        Log.d(TAG, ">> myFuncTest_3(Add) start... " + obj.getClass().getName());

        new Throwable().printStackTrace();
        HookHelper.invokeObjectOrigin(clazz, obj);
        Log.d(TAG, ">> myFuncTest_3 end... ");
    }

    public static String myFuncTest_8(Class<?> clazz) {
        Log.d(TAG, ">> myFuncTest_8 start... ");
        if(clsTest1 != null && clsTest2 != null) {
            Method method = HookHelper.findMethodHierarchically(clsTest1,"a", Class.class);
            if(method != null) {
                Object b2 = null;
                try {
                    b2 = method.invoke(null, clsTest2);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if(b2 != null) {
                    Class<?> cls = b2.getClass();
                    Method f = HookHelper.findMethodHierarchically(cls, "f");
                    if(f != null) {
                        Object y0 = null;
                        try {
                            y0 = f.invoke(b2);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        if(y0 != null) {
                            Field field = HookHelper.findFieldHierarchically(y0.getClass(), "c");
                            if(field != null) {
                                List cList = null;
                                try {
                                    cList = (List) field.get(y0);
                                    Log.d(TAG, ">> myFuncTest_8(list) size... " + cList.size());
                                    for(int i = 0; i < cList.size(); i++) {
                                        Object X0 = cList.get(i);
                                        Field r = HookHelper.findFieldHierarchically(X0.getClass(), "R");
                                        if(r != null) {
                                            Object R = r.get(X0);
                                            if(R != null) {
                                                Field w = HookHelper.findFieldHierarchically(R.getClass(), "w");
                                                if(w != null) {
                                                    Object expTran = w.get(R);
                                                    if(expTran != null) {
                                                        Field tag = HookHelper.findFieldHierarchically(expTran.getClass(), "serverExpTag");
                                                        String serverExpTag = (String) tag.get(expTran);

                                                        tag = HookHelper.findFieldHierarchically(expTran.getClass(), "clientExpTag");
                                                        String clientExpTag = (String) tag.get(expTran);

                                                        tag = HookHelper.findFieldHierarchically(expTran.getClass(), "cachedSize");
                                                        int cachedSize = (int) tag.get(expTran);
                                                        Log.d(TAG, ">> myFuncTest_8(list) serverExpTag=" + serverExpTag + ", clientExpTag=" + clientExpTag + ", cachedSize=" + cachedSize);
                                                    } else {
                                                        Log.d(TAG, ">> [expTran] object is null ");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(TAG, ">> [c] field is null ");
                            }
                        } else {
                            Log.d(TAG, ">> [x0] object is null ");
                        }
                    } else {
                        Log.d(TAG, ">> [f] method is null ");
                    }
                } else {
                    Log.d(TAG, ">> [b2] object is null ");
                }
            } else {
                Log.d(TAG, ">> [a] method is null ");
            }
        } else {
            Log.d(TAG, ">> [clsTest1, clsTest2] class is null ");
        }
        logStart = true;
        new Throwable().printStackTrace();
        String retStr = HookHelper.invokeObjectOrigin(clazz);
        Log.d(TAG, ">> myFuncTest_8 end... " + retStr);
        return retStr;
    }

    public static void myFuncTest_2(Class<?> clazz, Activity activity, Bundle bundle) {
        Log.d(TAG, ">> onActivityCreated start... " + activity.getClass().getName() + ", taskId=" + activity.getTaskId());
        new Throwable().printStackTrace();
        HookHelper.invokeVoidOrigin(clazz, activity, bundle);
        Log.d(TAG, ">> onActivityCreated end... ");
    }

    public static void myFuncTest_2_1(Class<?> clazz, Activity activity) throws IllegalAccessException {
        Log.d(TAG, ">> onActivityResumed... " + activity.getClass().getName() + ", taskId=" + activity.getTaskId());
        Class<?> cls = activity.getClass();
        Field fld = HookHelper.findFieldHierarchically(cls, "mIdent");
        int a = (int) fld.get(activity);
        Log.d(TAG, ">> onActivityResumed mIdent... " + a);
        HookHelper.invokeVoidOrigin(clazz, activity);

    }

    public static void myFuncTest_5(Class<?> clazz, Object obj) throws IllegalAccessException, InvocationTargetException {
        Log.d(TAG, ">> myFuncTest_5 call.. ");
        byteArrayView(obj);
        new Throwable().printStackTrace();
        HookHelper.invokeVoidOrigin(clazz, obj);
        Log.d(TAG, ">> ------------------ ");
    }

    public static void byteArrayView(Object obj) throws InvocationTargetException, IllegalAccessException {
        if(NativeRespose.mMessageNano != null)
        {
            Method method = HookHelper.findMethodHierarchicallyForString(NativeRespose.mMessageNano, "toByteArray", "com.google.protobuf.nano.MessageNano");
            if(method != null)
            {
                byte[] bytes = (byte[])method.invoke(null, obj);
                if(bytes.length > 0) {
                    DbgLog hexLog = new DbgLog();
                    hexLog.LogByteArray(bytes, bytes.hashCode());
                }
            }
        }
    }
    public static void myFuncTest_5_1(Class<?> clazz, Object obj) throws IllegalAccessException {
        Log.d(TAG, ">> myFuncTest_5_1 call.. ");
        getParameter(obj);
        HookHelper.invokeVoidOrigin(clazz, obj);
        Log.d(TAG, ">> ------------------ ");
    }

    private static void getParameter(Object obj) throws IllegalAccessException {
        Field field = HookHelper.findFieldHierarchically(obj.getClass(), "a");
        String sessionID = (String)field.get(obj);
        Log.d(TAG, "\t| sessionID = " + sessionID);
        field = HookHelper.findFieldHierarchically(obj.getClass(), "d");
        String str = (String)field.get(obj);
        Log.d(TAG, "\t| d = " + str);
        field = HookHelper.findFieldHierarchically(obj.getClass(), "c");
        Object cVar = (Object)field.get(obj);
        Log.d(TAG, "\t| cVar name = " + cVar.getClass().getName());
        {
            field = HookHelper.findFieldHierarchically(cVar.getClass(), "a");
            int intVal = (int)field.get(cVar);
            Log.d(TAG, "\t| cVar-a = " + intVal);

            field = HookHelper.findFieldHierarchically(cVar.getClass(), "b");
            String str1 = (String)field.get(cVar);
            Log.d(TAG, "\t| cVar-b = " + str1);

            field = HookHelper.findFieldHierarchically(cVar.getClass(), "c");
            intVal = (int)field.get(cVar);
            Log.d(TAG, "\t| cVar-c = " + intVal);

            field = HookHelper.findFieldHierarchically(cVar.getClass(), "d");
            str1 = (String)field.get(cVar);
            Log.d(TAG, "\t| cVar-d = " + str1);

            field = HookHelper.findFieldHierarchically(cVar.getClass(), "e");
            str1 = (String)field.get(cVar);
            Log.d(TAG, "\t| cVar-e = " + str1);

            field = HookHelper.findFieldHierarchically(cVar.getClass(), "f");
            intVal = (int)field.get(cVar);
            Log.d(TAG, "\t| cVar-f = " + intVal);

            field = HookHelper.findFieldHierarchically(cVar.getClass(), "g");
            boolean bool = (boolean)field.get(cVar);
            Log.d(TAG, "\t| cVar-g = " + bool);

            field = HookHelper.findFieldHierarchically(cVar.getClass(), "h");
            str1 = (String)field.get(cVar);
            Log.d(TAG, "\t| cVar-h = " + str1);

            field = HookHelper.findFieldHierarchically(cVar.getClass(), "i");
            str1 = (String)field.get(cVar);
            Log.d(TAG, "\t| cVar-i = " + str1);
        }

    }

    public static void myFuncTest_6(Class<?> clazz, Object obj, Object obj2) {
        Log.d(TAG, ">> myFuncTest_6.... " + obj.getClass().getName());
       // new Throwable().printStackTrace();
        HookHelper.invokeVoidOrigin(clazz, obj, obj2);
        Log.d(TAG, ">> ------------------ ");
    }

    public static String myFuncTest_7(Class<?> clazz, Context context) {
        Log.d(TAG, ">> myFuncTest_7...call ");
//        new Throwable().printStackTrace();
        String ret = HookHelper.invokeObjectOrigin(clazz, context);
        Log.d(TAG, ">>retVal =  " + ret);
        Log.d(TAG, ">> ------------------ ");
        return ret;
    }

    public static List<String> myFuncTest_7_1(Class<?> clazz, Context context) {
        Log.d(TAG, ">> myFuncTest_7_1...call ");
//        new Throwable().printStackTrace();
        List<String> ret = HookHelper.invokeObjectOrigin(clazz, context);
        for(int i = 0; i < ret.size(); i++) {

            Log.d(TAG, ">>retVal[" + i + "] = " + ret.get(i));
        }
        Log.d(TAG, ">> ------------------ ");
        return ret;
    }


    public static void myFuncTest_9(Class<?> clazz, Object obj, Object obj2, Object obj3) {
        Log.d(TAG, ">> myFuncTest_9 start..." + obj2.getClass().getName());
       // new Throwable().printStackTrace();
        HookHelper.invokeVoidOrigin(clazz, obj, obj2, obj3);
        Log.d(TAG, ">> myFuncTest_9 end...");
    }

    public static void myFuncTest_8_1(Class<?> clazz, Object obj) throws IllegalAccessException {
        Log.d(TAG, ">> myFuncTest_8_1 start...");
        Log.d(TAG, ">> obj | " + obj.getClass().getName());
        Object instance = HookHelper.invokeObjectOrigin(clazz, obj);
        getRunnableObject(instance);
        Log.d(TAG, ">> myFuncTest_8_1 end...");
    }

    private static void getRunnableObject(Object obj) throws IllegalAccessException {
        Field field = HookHelper.findFieldHierarchically(obj.getClass(), "a");
        if(field != null) {
            Object m = field.get(obj);
            Field g = HookHelper.findFieldHierarchically(m.getClass(), "g");
            if(g != null) {
                List<Runnable> runnableList = (List<Runnable>)g.get(m);
                if(!runnableList.isEmpty()) {
                    for(Runnable run: runnableList) {
                        Log.d(TAG, "Runable object : " + run.getClass().getName());
                    }
                }
            }
        }
    }

    public static void myFuncTest_8_2(Class<?> clazz, String url, String extra) throws IllegalAccessException {
        Log.d(TAG, ">> myFuncTest_8_2 call..| url=" + url + ", extra=" + extra);
        HookHelper.invokeVoidOrigin(clazz, url, extra);
    }

    @SuppressLint("HardwareIds")
    public static int myFuncTest_10(Class<?> clazz){
        Log.d(TAG, ">> myFuncTest_10 call.");
        new Throwable().printStackTrace();
        int ret = HookHelper.invokeIntOrigin(clazz);
        return ret;
    }

    public static void myFuncTest_11(Class<?> clazz, String name){
        Log.d(TAG, ">> myFuncTest_11 call.");
        Log.d(TAG, ">> myFuncTest_11 call | name=" + name);
        new Throwable().printStackTrace();
        HookHelper.invokeVoidOrigin(clazz, name);
    }


    private static void getRequest(Object obj) throws InvocationTargetException, IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Method method = HookHelper.findMethodHierarchically(clazz, "request");
        if(method != null) {
            Object request = method.invoke(obj);
            assert request != null;
            Log.d(TAG, request.toString());
        }
    }

    public static String security_matrix_h(Class<?> clazz, String str, boolean z, String str2) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(">> security_matrix_h start... \n");
//        sb.append(">> security_matrix_h | input =" + str + ", str2=" + str2 + "\n");
        String ret = HookHelper.invokeObjectOrigin(clazz, str, z, str2);
//        sb.append(">> security_matrix_h | output=" + ret + "\n");
//        Log.d(TAG, sb.toString());
        return ret;
    }

    public static byte[] toByteArray(Class<?> clazz, Object obj) throws IOException {
        byte[] bArr = HookHelper.invokeObjectOrigin(clazz, obj);
//        if(obj.getClass().getName().contains("SocketMessages$SocketMessage")) {
//            Log.d(TAG, "-------------> toByteArray");
//            DbgLog hexLog = new DbgLog();
//            hexLog.LogByteArray(bArr, bArr.hashCode());
//        }
//        Log.d(TAG, "-------------> toByteArray [" + bArr.hashCode() + "]");
        //logStart = false;
        return bArr;
    }



    public static void myScretKey(Class<?> clazz, KeyPair keypair) {
        Log.d(TAG, ">> myScretKey start... ");
        new Throwable().printStackTrace();
//        StringBuilder sb = new StringBuilder();
//        sb.append(">> myFuncTest_3 start... \n");
//        sb.append(">> input : " + privateKey + "\n");
        HookHelper.invokeVoidOrigin(clazz, keypair);
//        sb.append(">> output : " + encode + "\n");
        Log.d(TAG, ">> myScretKey end");
    }

    public static String myPhoneNumberEncode(Class<?> clazz, String phoneNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append(">> myPhoneNumberEncode start... \n");
        sb.append(">> input : " + phoneNumber + "\n");
        String encode= HookHelper.invokeObjectOrigin(clazz,phoneNumber);
        sb.append(">> output : " + encode + "\n");
        Log.d(TAG, String.valueOf(sb));
        return encode;
    }


    public static byte[] myFuncTest_4(Class<?> clazz, byte[] bytes) {
        Log.d(TAG, ">> myFuncTest_4 start... ");
//        String ss = new String(bytes);
//        Log.d(TAG, "__clientSign param [" + bytes.hashCode() + "]\t" + ss);
        byte[] encode = HookHelper.invokeObjectOrigin(clazz, bytes);
//        Log.d(TAG, "---------------------------");
//        DbgLog hexlog = new DbgLog();
//        hexlog.LogByteArray(encode, encode.hashCode());
//        Log.d(TAG, "---------------------------");
        return encode;
    }


    public static Object myNativeKwsgmain(Class<?> clazz, int id, Object[] objs){
        StringBuilder sb = new StringBuilder();
        sb.append(">> myNativeKwsgmain | objs = " + objs.length + "\n");
        for(int i = 0; i < objs.length; i++ )
        {
            if(objs[i] != null) {
                if(objs[i] instanceof String[]) {
                    String[] arr = (String[])objs[i];
                    for(int j = 0; j < arr.length; j++) {
                        sb.append("\tlength = " + "[" + i + "] " + arr[j].length());
                        sb.append("\tid=" + id + ", [" + i + "] " + arr[j] + "\n");
                    }
                }
                else if(objs[i] instanceof String) {
                    String s = (String)objs[i];
                    sb.append("\tlength = " + "[" + i + "] " + s.length());
                    sb.append("\tid=" + id + ", [" + i + "] " + s + "\n");
                }
                else if(objs[i] instanceof Context) {
                    AppInfo.setContextClass((Context) objs[i]);
                    sb.append("\tid=" + id + ", [" + i +"] Context | Applicatio Context set" + "\n");
                }
            }
        }
        Object sign = HookHelper.invokeObjectOrigin(clazz, id, objs);
        if(sign instanceof String) {
            String s = (String)sign;
            sb.append("\tid=" + id + " Return type is String, length = " + s.length());
            sb.append("\tid=" + id + " Return value | " + (String)sign + "\n");
        } else {
            byte[] ret = (byte[])sign;
            sb.append("\tid=" + id + " Return type is byte[], length = " + ret.length);
            DbgLog helLog = new DbgLog();
            helLog.LogByteArray(ret, id);
        }

//        Log.d(TAG,String.valueOf(sb));
        return  sign;
    }


    private static void getRequestInfo(Object request, int identify) {
        RequestInfo info = new RequestInfo(request, identify);
        info.start();
    }

    private static void getResponseInfo(Object response, int responseType, int identify){
        if(responseType == HookThread.HOOK_OKHTTP3_RESPONSE_BUILDER) {
//            Log.d("HTTP", "\t" + identify + "-[" + "OKHTTP" + "-RESPONSE] : " + response.toString());
            if(response.toString().contains("/live/startPlay/v2")) {
                logStart = false;
                ResponseInfo responseInfo = new ResponseInfo(response, identify, ResponseInfo.TO_FILE);
                //ResponseInfo responseInfo = new ResponseInfo(response, identify, "OKHTTP");
                responseInfo.setOnlyRequest(true);
                //responseInfo.setOnlyUrl(true);
                responseInfo.start();
            }
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
            for (int i = 0; i < arrayList.size(); i++) {
                Log.d(TAG, "ArrList : " + arrayList.get(i));
            }
            Collections.sort(arrayList);
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

        Class<?>[] clsArray = clazz.getDeclaredClasses();
        for (Class<?> aClass: clsArray) {
            sb.append("sub class : " + aClass.getSimpleName() + "\n");
        }

        Constructor<?>[] cs = clazz.getDeclaredConstructors();
        for(Constructor<?> c : cs) {
            sb.append("Constructor : " + c.toString() + "\n");
        }

        Method[] methods = clazz.getDeclaredMethods();
        for (Method md: methods) {
            sb.append("\tMethod : " + md.toString() + "\n");
        }

        Field[] fields = clazz.getFields();
        Field[] decField = clazz.getDeclaredFields();
        sb.append("field count : " + (fields.length + decField.length) + "\n");

        for (Field fd: fields) {
            sb.append("\t[*]field : " + fd.getName() + "\n");
        }

        for (Field fd: decField) {
            sb.append("\t[*]field : " + fd.getName() + "\n");
        }
        Log.d(TAG, sb + "\n");
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


