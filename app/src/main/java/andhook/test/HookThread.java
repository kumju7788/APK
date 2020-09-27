package andhook.test;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.util.LruCache;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import andhook.lib.HookHelper;
import static andhook.lib.HookHelper.findConstructorHierarchically;
import static andhook.lib.HookHelper.findConstructorHierarchicallyForString;
import static andhook.lib.HookHelper.findMethodHierarchically;
import static andhook.lib.HookHelper.findMethodHierarchicallyForString;

public class HookThread extends Thread {
    public static final int HOOK_TOUCH_LIVE_PLAZA                   = 1;
    public static final int HOOK_LOG                                = 2;
    public static final int HOOK_JSON_OBJECT_CLASS                  = 3;
    public static final int HOOK_FULL_SCREEN_CHECK                  = 4;
    public static final int HOOK_GSON_CLASS                         = 5;
    public static final int HOOK_RETROFIT_RESPONSE                  = 6;
    public static final int HOOK_OKHTTP3_REQUEST_BUILDER_BUILD      = 7;
    public static final int HOOK_OKHTTP3_RESPONSE_BUILDER           = 8;
    public static final int HOOK_SET_RESPONSE_BODY                  = 9;
    public static final int HOOK_OKHTTP3_HTTP_PROCEED               = 10;
    public static final int HOOK_NSTOKENSIG_PARAM                   = 11;
    public static final int HOOK_HASH_VALUE                         = 12;
    public static final int HOOK_FUNCTION_TEST_1                    = 13;
    public static final int HOOK_DEVICE_CARRY_INFO                  = 14;
    public static final int HOOK_KEYPAIR_CREATE                     = 15;
    public static final int HOOK_FUNCTION_TEST_4                    = 16;
    public static final int HOOK_APP_CONTEXT                        = 17;
    public static final int HOOK_CREATE_SECURITY_OBJECT             = 18;
    public static final int HOOK_SECURITY_CLASS                     = 19;
    public static final int HOOK_CUSTOM_ENCRYPTOR                   = 20;
    public static final int HOOK_ENGINE_PROXY                       = 21;
    public static final int HOOK_CHANGE_MAP_BINARY                  = 22;
    public static final int HOOK_NANO_MESSAGENANO                   = 23;
    public static final int HOOK_FUNCTION_WEIBO_SDK                 = 24;
    public static final int HOOK_FUNCTION_TEST_3                    = 25;
    public static final int HOOK_IMEIS_ENCRYPT                      = 26;
    public static final int HOOK_FUNCTION_TEST_5                    = 27;
    public static final int HOOK_NANO_CLIENT_EVENT                  = 28;
    public static final int HOOK_FUNCTION_TEST_6                    = 29;
    public static final int HOOK_FUNCTION_TEST_7                    = 30;
    public static final int HOOK_FUNCTION_TEST_8                    = 31;
    public static final int HOOK_CLIENT_EVENT_LIST_BUILDER          = 32;
    public static final int HOOK_FUNCTION_TEST_9                    = 33;
    public static final int HOOK_FUNCTION_TEST_10                   = 34;
    public static final int HOOK_FUNCTION_TEST_11                   = 35;


    private static final int HOOK_PRELOAD_CLASSES                   = 101;

    private static final String TAG = HookInit.TAG;
    private Class<?> clazz;
    private int hookIndex;

    static Class<?>[] rClasses;
    static int rClassCount;

    Class<?> paramCls1;
    Class<?> paramCls2;
    Class<?> paramCls3;
    Class<?> paramCls4;

    Method orgMethod = null;
    Method replaceMethod = null;
    boolean isHook1 = false;
    boolean isHook2 = false;


    HookThread(Class<?> cls, int index) {
        clazz = cls;
        hookIndex = index;
    }

    @Override
    public void run() {
        switch(hookIndex) {
            case HOOK_PRELOAD_CLASSES:
                Preload preload = new Preload();
                preload.start();
                break;

            case HOOK_TOUCH_LIVE_PLAZA:
//                Log.d(TAG, "[===] HOOK_TOUCH_LIVE_PLAZA signal input...");
//                orgMethod = findMethodHierarchically(clazz, "onInterceptTouchEvent", MotionEvent.class);
//                replaceMethod = findMethodHierarchically( AppHooking.class, "LivePlaza_Click", Class.class, MotionEvent.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] HOOK_TOUCH_LIVE_PLAZA hooking success...");
//
//                Log.d(TAG, "[===] openPane(View, int) hooking.");
//                orgMethod = findMethodHierarchically(clazz, "openPane", View.class, int.class);
//                replaceMethod = findMethodHierarchically( AppHooking.class, "CheckSlidingPanOpened", Class.class, View.class, int.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] openPane(View, int) hooking success.");
//
//                Log.d(TAG, "[===] closePane(View, int) hooking.");
//                orgMethod = findMethodHierarchically(clazz, "closePane", View.class, int.class);
//                replaceMethod = findMethodHierarchically( AppHooking.class, "CheckSlidingPanClosed", Class.class, View.class, int.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] closePane(View, int) hooking success.");

                break;

            case HOOK_LOG:
//                Log.d(TAG, "[===] HOOK_LOG signal input...");
//                orgMethod = findMethodHierarchically(clazz, "a", String.class, String.class);
//                replaceMethod = findMethodHierarchically( AppHooking.class, "my_logDbg", Class.class, String.class, String.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//
//                orgMethod = findMethodHierarchically(clazz, "c", String.class, String.class);
//                replaceMethod = findMethodHierarchically( AppHooking.class, "my_logInfo", Class.class, String.class, String.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//
//                orgMethod = findMethodHierarchically(clazz, "d", String.class, String.class);
//                replaceMethod = findMethodHierarchically( AppHooking.class, "my_logVerb", Class.class, String.class, String.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] HOOK_LOG hooking success...");
                break;

            case HOOK_JSON_OBJECT_CLASS:
//                Log.d(TAG, "[===] HOOK_JSON_OBJECT_CLASS signal input...");
//                orgMethod = findMethodHierarchically(clazz, "put", String.class, Object.class);
//                Log.d(TAG, "orgMethod : " + orgMethod.getName());
//                replaceMethod = findMethodHierarchically(AppHooking.class,"my_JsonObjectPut", Class.class, String.class, Object.class);
//                Log.d(TAG, "replaceMethod : " + replaceMethod.getName());
//                HookHelper.hook(orgMethod, replaceMethod);
//
//                orgMethod = findMethodHierarchically(clazz, "put", String.class, boolean.class);
//                Log.d(TAG, "orgMethod : " + orgMethod.getName());
//                replaceMethod = findMethodHierarchically(AppHooking.class,"my_JsonBooleanPut", Class.class, String.class, boolean.class);
//                Log.d(TAG, "replaceMethod : " + replaceMethod.getName());
//                HookHelper.hook(orgMethod, replaceMethod);
//
//                Log.d(TAG, "[===] HOOK_JSON_OBJECT_CLASS hooking success...");
                break;

            case HOOK_FULL_SCREEN_CHECK:
//                Log.d(TAG, "[===] HOOK_FULL_SCREEN_CHECK signal input...");
//                orgMethod = findMethodHierarchically(clazz, "a", boolean.class, int.class);
//                replaceMethod = findMethodHierarchically(AppHooking.class,"myCheckFullScreen", Class.class, boolean.class, int.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] HOOK_FULL_SCREEN_CHECK hooking success...");
                break;

            case HOOK_GSON_CLASS:
//                Log.d(TAG, "[===] HOOK_GSON_CLASS signal input...");
//                // public <T> T fromJson(String json, Class<T> classOfT)
//                orgMethod = findMethodHierarchically(clazz, "a", String.class, Class.class);
//                replaceMethod = findMethodHierarchically(AppHooking.class,"myFromJSon", Class.class, String.class, Class.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//
//                // public String toJson(Object src)
//                orgMethod = findMethodHierarchically(clazz, "a", Object.class);
//                replaceMethod = findMethodHierarchically(AppHooking.class,"myToJSon", Class.class, Object.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//
//                Log.d(TAG, "[===] HOOK_GSON_CLASS hooking success...");
                break;

            case HOOK_RETROFIT_RESPONSE:
//                Log.d(TAG, "[===] HOOK_RETROFIT_RESPONSE signal input...");
//                orgMethod = findMethodHierarchically(clazz, "execute");
//                replaceMethod = findMethodHierarchically(AppHooking.class,"myRetrofitExecute", Class.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] HOOK_RETROFIT_RESPONSE hooking success...");
                break;

            case HOOK_OKHTTP3_REQUEST_BUILDER_BUILD:
//                Log.d(TAG, "[===] HOOK_OKHTTP3_REQUEST_BUILDER_BUILD signal input...");
//                orgMethod = findMethodHierarchically(clazz, "a");
//                replaceMethod = findMethodHierarchically(AppHooking.class,"myRequestBuilder_builder", Class.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] HOOK_OKHTTP3_REQUEST_BUILDER_BUILD hooking success...");
                break;

            case HOOK_OKHTTP3_RESPONSE_BUILDER:
                Log.d(TAG, "[===] HOOK_OKHTTP3_RESPONSE_BUILDER signal input...");
                orgMethod = findMethodHierarchically(clazz, "execute");
                replaceMethod = findMethodHierarchically(AppHooking.class, "myOkhttpExecute", Class.class);
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_OKHTTP3_RESPONSE_BUILDER hooking success...");
                break;

            case HOOK_OKHTTP3_HTTP_PROCEED:
//                Log.d(TAG, "[===] HOOK_OKHTTP3_HTTP_PROCEED signal input...");
//                orgMethod = findMethodHierarchicallyForString(clazz, "proceed", "okhttp3.Request", "w0.e0.e.f", "w0.e0.f.c", "w0.e0.e.c");
//                replaceMethod = findMethodHierarchically(AppHooking.class, "myOkhttp3_HttpProceed", Class.class,Object.class, Class.class, Class.class, Class.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] HOOK_OKHTTP3_HTTP_PROCEED hooking success...");

                break;

            case HOOK_SET_RESPONSE_BODY:
//                Log.d(TAG, "[===] HOOK_SET_RESPONSE_BODY signal input...");
//                Member org = findConstructorHierarchicallyForString(clazz, "w0.t", "long", "x0.i");
//                replaceMethod = findMethodHierarchically(AppHooking.class, "mySetResponseBody", Class.class, Class.class, long.class, Class.class);
//                HookHelper.hook(org, replaceMethod);
//                Log.d(TAG, "[===] HOOK_SET_RESPONSE_BODY hooking success...");
                break;

////////////
            case HOOK_NSTOKENSIG_PARAM:
                Log.d(TAG, "[===] HOOK_NSTOKENSIG_PARAM hooking input..." + clazz.getName());
                orgMethod = findMethodHierarchically(clazz, "a", byte[].class);
                replaceMethod = findMethodHierarchically(AppHooking.class, "NStokensigParam1", Class.class, byte[].class);
                HookHelper.hook(orgMethod, replaceMethod);
                new NativeRespose(clazz, NativeRespose.SECURITY_CPU_GETCLOCK);
                Log.d(TAG, "[===] HOOK_NSTOKENSIG_PARAM hooking success...");
                break;
///////////
            //TODO _Nssig3 만드는 클라스 : com.kuaishou.android.security.matrix.h
            case HOOK_CREATE_SECURITY_OBJECT:
                Log.d(TAG, "[===] HOOK_CREATE_SECURITY_OBJECT signal input..." + clazz.getName());
                NativeRespose nate = new NativeRespose();
                nate.createSecurityObject(clazz);
                orgMethod = findMethodHierarchically(clazz, "a", String.class, boolean.class, String.class);
                replaceMethod = findMethodHierarchically(AppHooking.class, "security_matrix_h", Class.class, String.class, boolean.class, String.class);
                HookHelper.hook(orgMethod, replaceMethod);
                //nate.getNssig3("");
                Log.d(TAG, "[===] HOOK_CREATE_SECURITY_OBJECT hooking success...");
                break;
/////////////
            //TODO 전화번호암호화 하는 클라스 : com.kuaishou.android.security.KSecurity
            case HOOK_SECURITY_CLASS:
                Log.d(TAG, "[===] HOOK_SECURITY_CLASS signal input..." + clazz.getName());
                NativeRespose security = new NativeRespose(clazz, NativeRespose.SECURITY_PHONE_NUMBER);
                //nate.getNssig3("");
                Log.d(TAG, "[===] HOOK_SECURITY_CLASS hooking success...");
                break;
////////////
            case HOOK_APP_CONTEXT:
                Log.d(TAG, "[===] HOOK_APP_CONTEXT signal input..." + clazz.getName());
                //AppHooking.appClassTest(clazz, 0);
                orgMethod = findMethodHierarchically(clazz, "a", int.class, Object[].class);
                replaceMethod = findMethodHierarchically(AppHooking.class, "myNativeKwsgmain", Class.class, int.class, Object[].class);
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_APP_CONTEXT hooking success...");
                break;
////////////
            //TODO 공개키와 비공개키를 만드는 클라스 : com.yxcorp.gifshow.util.AccountSecurityHelper
            case HOOK_KEYPAIR_CREATE:
                Log.d(TAG, "[===] HOOK_KEYPAIR_CREATE signal input..." + clazz.getName());
                new NativeRespose(clazz, NativeRespose.SECURITY_KEYPAIR_CREATE);
                //nate.getNssig3("");
                Log.d(TAG, "[===] HOOK_KEYPAIR_CREATE hooking success...");
                break;
//////////////
            //TODO 공개키와 비공개키를 다시 암호화하는데 리용되는 클라스 : com.yxcorp.gifshow.util.AccountSecurityHelper
            case HOOK_CUSTOM_ENCRYPTOR:
                Log.d(TAG, "[===] HOOK_CUSTOM_ENCRYPTOR signal input..." + clazz.getName());
                NativeRespose customEncryptor = new NativeRespose();
                try {
                    customEncryptor.createCustomEncryptClassB(clazz);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                //nate.getNssig3("");
                Log.d(TAG, "[===] HOOK_CUSTOM_ENCRYPTOR hooking success...");
                break;
/////////////
            case HOOK_HASH_VALUE:
                Log.d(TAG, "[===] HOOK_HASH_VALUE signal input...");
                //AppHooking.appClassTest(clazz, 0);
                orgMethod = findMethodHierarchicallyForString(clazz, "a", "j.a.z.g$b", "java.lang.String", "java.lang.String");
                replaceMethod = findMethodHierarchically(AppHooking.class, "myNStokenHashVal", Class.class, Object.class, String.class, String.class);
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_HASH_VALUE hooking success...");
                break;
/////////////
            case HOOK_FUNCTION_TEST_1: //CarryInfo에 들어가는 파라메터
                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_1 signal input...");

                orgMethod = findMethodHierarchically(clazz, "a", String.class, String.class, boolean.class, Bundle.class);
                replaceMethod = findMethodHierarchically(AppHooking.class, "myCarryInfoParam", Class.class, String.class, String.class, boolean.class, Bundle.class);
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_1 hooking success...");
                break;
////////////
            case HOOK_DEVICE_CARRY_INFO:
                Log.d(TAG, "[===] HOOK_DEVICE_CARRY_INFO signal input..." + clazz.getName());
                orgMethod = findMethodHierarchically(clazz, "atlasEncrypt", String.class, String.class, int.class, byte[].class);
                replaceMethod = findMethodHierarchically(AppHooking.class, "atlasEncrypt", Class.class, String.class, String.class, int.class, byte[].class);
                HookHelper.hook(orgMethod, replaceMethod);
                new NativeRespose(clazz, NativeRespose.SECURITY_DEVICE_AND_CARRY);

                Log.d(TAG, "[===] HOOK_DEVICE_CARRY_INFO hooking success...");
                break;
///////////
            // TODO 디바이스정보에 들어가는 파라메터들중에서 K31, K89를 얻는 모쥴
            case HOOK_ENGINE_PROXY:
                Log.d(TAG, "[===] HOOK_ENGINE_PROXY signal input..." + clazz.getName());
                new NativeRespose(clazz, NativeRespose.SECURITY_ENGINE_PROXY);
                Log.d(TAG, "[===] HOOK_ENGINE_PROXY hooking success...");
                break;
///////////
            // TODO 디바이스정보에 들어가는 파라메터맵을 바이너리로 변환한다.
            case HOOK_CHANGE_MAP_BINARY: //egid 얻어오는 URL에 들어가는 장치정보얻는 부분
                Log.d(TAG, "[===] HOOK_CHANGE_MAP_BINARY signal input..." + clazz.getName());
                new NativeRespose(clazz, NativeRespose.MAP_CHANGE);
                orgMethod = findMethodHierarchically(clazz, "a", Map.class);
                replaceMethod = findMethodHierarchically(AppHooking.class, "myDeviceInfoParam", Class.class, Map.class);
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_CHANGE_MAP_BINARY hooking success...");
                break;
////////////
            case HOOK_NANO_MESSAGENANO:
                Log.d(TAG, "[===] HOOK_NANO_MESSAGENANO signal input..." + clazz.getName());

                new NativeRespose(clazz, NativeRespose.MAP_MESSAGE_NANO);
//                orgMethod = HookHelper.findMethodHierarchicallyForString(clazz, "toByteArray", "com.google.protobuf.nano.MessageNano");
//                replaceMethod = findMethodHierarchically(AppHooking.class, "toByteArray", Class.class, Object.class);
//                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_NANO_MESSAGENANO hooking success...");
                break;

////////////
            // TODO 이메지 슬라이드할 때(육성) /rest/n/clc/show URL에 포함되는 imeis파라메터 얻는 부분.
            case HOOK_IMEIS_ENCRYPT:
                Log.d(TAG, "[===] HOOK_IMEIS_ENCRYPT signal input..." + clazz.getName());
                new NativeRespose(clazz, NativeRespose.ENCRYPT_IMEIS);
                Log.d(TAG, "[===] HOOK_IMEIS_ENCRYPT hooking success...");
                break;

////////////
            case HOOK_NANO_CLIENT_EVENT:
                Log.d(TAG, "[===] HOOK_NANO_CLIENT_EVENT signal input..." + clazz.getName());
                new NativeRespose(clazz, NativeRespose.CLASS_CLIENT_EVENT);
                Log.d(TAG, "[===] HOOK_NANO_CLIENT_EVENT hooking success...");
                break;

////////////
            case HOOK_CLIENT_EVENT_LIST_BUILDER:
                Log.d(TAG, "[===] HOOK_CLIENT_EVENT_LIST_BUILDER signal input..." + clazz.getName());
                new NativeRespose(clazz, NativeRespose.CLASS_CLIENT_EVENT_BUILDER);
                Log.d(TAG, "[===] HOOK_CLIENT_EVENT_LIST_BUILDER hooking success...");
                break;

            case HOOK_FUNCTION_TEST_4:
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_4 signal input..." + clazz.getName());
//                orgMethod = findMethodHierarchically(clazz, "doFinal", byte[].class);
//                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_4", Class.class, byte[].class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_4 hooking success...");
/////////////
                // 전화번호 encode
                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_4 signal input..." + clazz.getName());
                orgMethod = findMethodHierarchically(clazz, "a", String.class);
                replaceMethod = findMethodHierarchically(AppHooking.class, "myPhoneNumberEncode", Class.class, String.class);
                HookHelper.hook(orgMethod, replaceMethod);

                //TODO mobileVerifyCode에 들어가는 "secret" 키분석부분
                orgMethod = findMethodHierarchically(clazz, "a", KeyPair.class);
                replaceMethod = findMethodHierarchically(AppHooking.class, "myScretKey", Class.class, KeyPair.class);
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_4 hooking success...");
                break;

/////////////
            case HOOK_FUNCTION_WEIBO_SDK:
                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_2 signal input..." + clazz.getName());
                new NativeRespose(clazz, NativeRespose.CLASS_WEIBO_SDK);
                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_2 hooking success...");
                break;

            case HOOK_FUNCTION_TEST_3:
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_3 hooking input...");
//                orgMethod = findMethodHierarchicallyForString(clazz, "a", "j.a.a.r4.x0");
//                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_3", Class.class,  Object.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_3 hooking success...");

                break;
            case HOOK_FUNCTION_TEST_5:
                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_5 hooking input...");
                orgMethod = findMethodHierarchicallyForString(clazz, "a", "com.google.protobuf.nano.MessageNano");
                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_5", Class.class, Object.class);
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_5 hooking success...");

                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_5_1 hooking input...");
                orgMethod = findMethodHierarchicallyForString(clazz, "a", "j.b.e0.m.a.a");
                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_5_1", Class.class, Object.class);
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_5 hooking success...");
                break;

            case HOOK_FUNCTION_TEST_6:
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_6 hooking input...");
//                orgMethod = findMethodHierarchicallyForString(clazz, "a", "java.lang.Object", "l0.b.c.v");
//                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_6", Class.class, Object.class, Object.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_6 hooking success...");
                break;

            case HOOK_FUNCTION_TEST_7:
                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_7 hooking input...");
                orgMethod = findMethodHierarchically(clazz, "c", Context.class);
                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_7", Class.class, Context.class);
                HookHelper.hook(orgMethod, replaceMethod);

                orgMethod = findMethodHierarchically(clazz, "b", Context.class);
                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_7_1", Class.class, Context.class);
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_7 hooking success...");
                break;

            case HOOK_FUNCTION_TEST_8:
                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_8 hooking input...");
//                orgMethod = findMethodHierarchically(clazz, "a");
//                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_8", Class.class);
//                HookHelper.hook(orgMethod, replaceMethod);

//                orgMethod = findMethodHierarchically(clazz, "a");
//                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_8_1", Class.class, Object.class);
//                HookHelper.hook(orgMethod, replaceMethod);

//                Member org = findConstructorHierarchically(clazz, String.class, String.class);
//                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_8_2", Class.class, String.class, String.class);
//                HookHelper.hook(org, replaceMethod);
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_8 hooking success...");
                break;

            case HOOK_FUNCTION_TEST_9:
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_9 hooking input...");
//                orgMethod = findMethodHierarchicallyForString(clazz, "a", "l0.b.c.j", "java.lang.Object", "l0.b.c.v");
//                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_9", Class.class, Object.class, Object.class, Object.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_9 hooking success...");
                break;

            case HOOK_FUNCTION_TEST_10:
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_10 hooking input...");
//                orgMethod = findMethodHierarchically(clazz, "A");
//                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_10", Class.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_10 hooking success...");
                break;

            case HOOK_FUNCTION_TEST_11:
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_11 hooking input...");
//                orgMethod = findMethodHierarchically(clazz, "setDid", String.class);
//                replaceMethod = findMethodHierarchically(AppHooking.class, "myFuncTest_11", Class.class, String.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                Log.d(TAG, "[===] HOOK_FUNCTION_TEST_11 hooking success...");
                break;
        }
        //super.run();
    }
}
