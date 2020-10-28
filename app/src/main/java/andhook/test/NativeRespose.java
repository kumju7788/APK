package andhook.test;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.GZIPOutputStream;

import andhook.lib.HookHelper;

public class NativeRespose {
    public static Object kwaiSecurity = null;
    public static String TAG = "JAVA";
    public static final int SECURITY_PHONE_NUMBER       = 100;
    public static final int SECURITY_KEYPAIR_CREATE     = 101;
    public static final int SECURITY_CPU_GETCLOCK       = 102;
    public static final int SECURITY_DEVICE_AND_CARRY   = 103;
    public static final int SECURITY_ENGINE_PROXY       = 104;
    public static final int MAP_CHANGE                  = 105;
    public static final int MAP_MESSAGE_NANO            = 106;
    public static final int ENCRYPT_IMEIS               = 107;
    public static final int CLASS_CLIENT_EVENT          = 108;
    public static final int CLASS_CLIENT_EVENT_BUILDER  = 109;
    public static final int CLASS_WEIBO_SDK             = 110;
    public static final int CLASS_REAL_SHOW_FEED        = 111;
    public static final int CLASS_REAL_SHOW_SUB_1       = 112;
    public static final int CLASS_REAL_SHOW_SUB_2       = 113;
    public static final int CLASS_IV2                   = 114;
    public static final int CLASS_CONTACT               = 115;


    List<String> mParam;
    List<String> mRequest;

    static Class<?> mSecurityPhoneNumberClass;
    static Class<?> mSecurityKeyPair;
    static Object mCustomEncryptorB;
    static Class<?> mCPU;
    static Class<?> mDeviceInfoClass;
    static Class<?> mMapChange;
    static Class<?> mEngineProxy;
    public static Class<?> mMessageNano;
    static Class<?> mEncryptImeis;
    static Class<?> mClientEvent;
    static Class<?> mClientEventBuilder;
    static Class<?> mWeiboSdk;
    static Class<?> mRealShowFeed;
    static Class<?> mRealShowSub_1;
    static Class<?> mRealShowSub_2;
    static Class<?> mClassIV2;
    static Class<?> mClassContact;

    static String mSocName;
    static String mCpuCount;
    static String mDevWidth;
    static String mDevHeight;

    String mAppKey = "d7b7d042-d4f2-4012-be60-d97ff2429c17";

    NativeRespose() {
    }

    NativeRespose(Class<?> clazz, int secureType) {
        switch (secureType) {
            case SECURITY_PHONE_NUMBER:
                mSecurityPhoneNumberClass = clazz;
                break;
            case SECURITY_KEYPAIR_CREATE:
                mSecurityKeyPair = clazz;
                break;
            case SECURITY_CPU_GETCLOCK:
                mCPU = clazz;
                break;
            case SECURITY_DEVICE_AND_CARRY:
                mDeviceInfoClass = clazz;
                break;
            case SECURITY_ENGINE_PROXY:
                mEngineProxy = clazz ;
                break;
            case MAP_CHANGE:
                mMapChange = clazz;
                break;
            case MAP_MESSAGE_NANO:
                mMessageNano = clazz;
                break;
            case ENCRYPT_IMEIS:
                mEncryptImeis = clazz;
                break;
            case CLASS_CLIENT_EVENT:
                mClientEvent = clazz;
                break;
            case CLASS_CLIENT_EVENT_BUILDER:
                mClientEventBuilder = clazz;
                break;
            case CLASS_WEIBO_SDK:
                mWeiboSdk = clazz;
                break;
            case CLASS_REAL_SHOW_FEED:
                mRealShowFeed = clazz;
                break;
            case CLASS_REAL_SHOW_SUB_1:
                mRealShowSub_1 = clazz;
                break;
            case CLASS_REAL_SHOW_SUB_2:
                mRealShowSub_2 = clazz;
                break;
            case CLASS_IV2:
                mClassIV2 = clazz;
                break;
            case CLASS_CONTACT:
                mClassContact = clazz;
                break;
        }
    }


    NativeRespose(List<String> reqCode, List<String> param) {
        mParam = param;
        mRequest = reqCode;
    }
    
    private String getParam(String key) {
        String param = "";
        String ret;
        int index;
        for(int i = 0; i < mParam.size(); i++) {
            ret = mParam.get(i);
            if(ret.startsWith(key)) {
                index = ret.indexOf("=");
                param = ret.substring(index + 1);
                break;
            }
        }
        return param;
    }

    public String getResult() {
        String res = "";
        String param;
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < mRequest.size(); i++) {
            Log.d(TAG, "function name = " + mRequest.get(i));
            if(mRequest.get(i).equals("Nssig3")) {
                param = getParam("sig");
                if(!param.isEmpty()) {
                    res = getNssig3(param);
                    sb.append("__NS_sig3=" + res);
                }
                continue;
            }
            
            if(mRequest.get(i).equals("egid")) {
                param = getParam("devInfo");
                if(!param.isEmpty()) {
                    res = egid(param);
                    sb.append("egid=" + res);
                }
                continue;
            }
   
            if(mRequest.get(i).equals("phoneCode")) {
                param = getParam("phoneNumber");
                if(!param.isEmpty()) {
                    res = phoneCode(param);
                    sb.append("mobile=" + res);
                }
                continue;
            }

            if(mRequest.get(i).equals("sig")) {
                param = getParam("url");
                if(!param.isEmpty()) {
                    res = getSig(param);
                    sb.append(res);
                }
                continue;
            }

            if(mRequest.get(i).equals("deviceInfo")) {
                param = getParam("info");
                if(!param.isEmpty()) {
                    try {
                        res = getDeviceInfo(param);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                    sb.append(res);
                }
                continue;
            }

            if(mRequest.get(i).equals("get_environment")) {
                String country = getParam("country");
                String devParam = getParam("info");
                String egid = getParam("egid");
                String did = getParam("did");
                String uid = getParam("uid");
                try {
                    res = getEnvironmentInfo(country, devParam, egid, did, uid);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                sb.append(res);
            }

            if(mRequest.get(i).equals("get_config_param")) {
                String egid = getParam("egid");
                String appVer = getParam("appver");
                try {
                    res = getReportConfigParam(egid, appVer);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                sb.append(res);
            }

            if(mRequest.get(i).equals("get_config")) {
                String conf = getParam("conf");
                try {
                    res = getReportConfig(conf);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                sb.append(res);
            }

            if(mRequest.get(i).equals("keypair")) {
                String raw = getParam("raw");
                try {
                    sb.append(getKeyPair(raw));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if(mRequest.get(i).equals("get_imeis")) {
                String imei = getParam("imei");
                if(!imei.isEmpty())
                {
                    try {
                        sb.append(getEncryprImeis(imei));
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(mRequest.get(i).equals("get_query_id")) {
                String keyword = getParam("keyword");
                String userId = getParam("uid");
                long timeVal =  Long.parseLong(getParam("timeVal"));
                if(!keyword.isEmpty() && !userId.isEmpty())
                {
                    try {
                        sb.append(getQueryId(keyword, userId, timeVal));
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(mRequest.get(i).equals("get_exp_tag_list")) {
                String strSrvExpTag = getParam("serverExpTag");
                String strClientExpTag = getParam("clientExpTag");
                try {
                    sb.append(getExpTagList(strSrvExpTag, strClientExpTag));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            if(mRequest.get(i).equals("get_package_hash")) {
                try {
                    sb.append(getPackageHash());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            if(mRequest.get(i).equals("get_show_log")) {
                String strLlsid = getParam("Llsid");
                String strSrvExpTag = getParam("serverExpTag");
                String strClientExpTag = getParam("ExpTag");
                String strUserId = getParam("UserId");
                String strBrowserType = getParam("BrowserType");
                String strPhotoId = getParam("PhotoId");
                String strSessionId = getParam("SessionId");
                String strDuration = getParam("Duration");
                try {
                    sb.append(getRealShowLog(strLlsid, strSrvExpTag, strClientExpTag, strUserId, strBrowserType, strPhotoId, strSessionId, strDuration));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(mRequest.get(i).equals("get_device_traffic_param")) {
                String strKey = getParam("key");
                String strSimInfo = getParam("simInfo");
                try {
                    sb.append(getDeviceTrafficParam(strKey, strSimInfo));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }


        }
        return String.valueOf(sb);
    }

    private String getDeviceTrafficParam(String strKey, String strSimInfo) throws InvocationTargetException, IllegalAccessException {
        String res = new String();
        if(mClassIV2 != null) {
            Method method = HookHelper.findMethodHierarchically(mClassIV2,"a", String.class, String.class);
            if(method != null) {
                Map<String, String> map = (Map<String, String>) method.invoke(null, strKey, strSimInfo);
                if(map.size() != 0) {
                    res = "iv2=" + map.get("iv2") + "&e2=" + map.get("e2");
                }
            }
        }
        return res;
    }

    private String getRealShowLog(String strLlsid, String strSrvExpTag, String strClientExpTag, String strUserId, String strBrowserType, String strPhotoId, String strSessionId, String strDuration) throws IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        byte[] bArr = null;
        Constructor eVar = null;
        Constructor fVar = null;
        Field field;
        Object eVarObj = null;
        Object fVarObj = null;
        Object fVarObjArr = null;
        Object realShowObjArr = null;
        Method toByteArray = null;
        String res = "";

        //TODO : j.a.a.r4.c3의 b(d3)에서 설정하는 값들이다
        if(mRealShowFeed != null) {
            Constructor constructor = HookHelper.findConstructorHierarchically(mRealShowFeed);
            Object objRealShow = constructor.newInstance();
            field = HookHelper.findFieldHierarchically(objRealShow.getClass(), "B");
            field.set(objRealShow, Integer.valueOf(strBrowserType));
            field = HookHelper.findFieldHierarchically(objRealShow.getClass(), "C");
            field.set(objRealShow, 5);
            field = HookHelper.findFieldHierarchically(objRealShow.getClass(), "a");
            field.set(objRealShow, 1);
            field = HookHelper.findFieldHierarchically(objRealShow.getClass(), "b");
            field.set(objRealShow, Long.valueOf(strUserId));
            field = HookHelper.findFieldHierarchically(objRealShow.getClass(), "c");
            field.set(objRealShow, Long.valueOf(strPhotoId));
            field = HookHelper.findFieldHierarchically(objRealShow.getClass(), "e");
            field.set(objRealShow, strClientExpTag);
            field = HookHelper.findFieldHierarchically(objRealShow.getClass(), "C");
            field.set(objRealShow, 3);
            field = HookHelper.findFieldHierarchically(objRealShow.getClass(), "s");
            field.set(objRealShow, strSessionId);
            field = HookHelper.findFieldHierarchically(objRealShow.getClass(), "t");
            field.set(objRealShow, strSrvExpTag);
            field = HookHelper.findFieldHierarchically(objRealShow.getClass(), "z");
            field.set(objRealShow, Long.valueOf(strDuration));

            realShowObjArr = Array.newInstance(mRealShowFeed, 1);
            Array.set(realShowObjArr, 0, objRealShow);

            if (mMessageNano != null) {
                toByteArray = HookHelper.findMethodHierarchicallyForString(mMessageNano, "toByteArray", "com.google.protobuf.nano.MessageNano");
                if (toByteArray != null) {
                    bArr = (byte[]) toByteArray.invoke(null, objRealShow);
                }
            }

            if(mRealShowSub_1 != null) {
                eVar = HookHelper.findConstructorHierarchically(mRealShowSub_1);
                eVarObj = eVar.newInstance();
            }

            if(mRealShowSub_2 != null) {
                fVar = HookHelper.findConstructorHierarchically(mRealShowSub_2);
                fVarObj = fVar.newInstance();
                field = HookHelper.findFieldHierarchically(mRealShowSub_2, "a");
                field.set(fVarObj, Long.valueOf(strLlsid));
                field = HookHelper.findFieldHierarchically(mRealShowSub_2, "b");
                field.set(fVarObj, realShowObjArr);
                fVarObjArr = Array.newInstance(mRealShowSub_2, 1);
                Array.set(fVarObjArr, 0, fVarObj);
            }

            if(eVar != null) {
                field = HookHelper.findFieldHierarchically(eVarObj.getClass(), "a");
                field.set(eVarObj, fVarObjArr);
            }

            if (toByteArray != null) {
                bArr = (byte[]) toByteArray.invoke(null, eVarObj);
                DbgLog hexLog = new DbgLog();
                hexLog.LogByteArray(bArr, bArr.hashCode());
            }

            if(bArr != null && bArr.length != 0) {
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(bArr.length);
                GZIPOutputStream outputStream = new GZIPOutputStream(arrayOutputStream);
                outputStream.write(bArr);
                outputStream.flush();
                outputStream.close();
                arrayOutputStream.flush();
                arrayOutputStream.close();
                res = Base64.encodeToString(arrayOutputStream.toByteArray(), 2);
            }
        }
        return res;
    }

    private String getPackageHash() throws InvocationTargetException, IllegalAccessException {
        String strHash = "";
        if(mWeiboSdk != null)  {
            Method method = HookHelper.findMethodHierarchically(mWeiboSdk, "d", Context.class, String.class);
            if(method != null) {
                Context context = getAppContext();
                strHash = (String)method.invoke(null, context, "com.smile.gifmaker");
            }
        }
        return strHash;
    }

    //TODO feef/hot 에서 받은 자료중에서 현재 화면에 대한 자료의 serverExpTag자료를 엔코딩한다.
    private String getExpTagList(String strServerExpTag, String strClientExpTag) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> clsExpTagTrans;
        Class<?> clsExpTagTransArray;
        Object objExpTagTrans = null;
        Object objExpTagTransList = null;
        Object objExpTagTransArray = null;
        Method getArray;
        byte[] bArr = new byte[0];
        String res = "";

        //TODO : 1. strServerExpTag를 ExpTagTrans클라스에 셋팅한다.
        if(mClientEvent != null) {
            // TODO: com.kuaishou.client.log.event.packages.nano.ClientEvent클라스에서 ExpTagTrans클라스를 찾는다.
            clsExpTagTrans = HookHelper.findSubClass(mClientEvent, "ExpTagTrans");
            if(clsExpTagTrans != null) {
                Constructor constructor =  HookHelper.findConstructorHierarchically(clsExpTagTrans);
                objExpTagTrans = constructor.newInstance();
                // TODO: ExpTagTrans클라스에 있는 serverExpTag필드를 찾는다.
                Field serverExpTag = HookHelper.findFieldHierarchically(objExpTagTrans.getClass(), "serverExpTag");
                if(serverExpTag != null) {
                    serverExpTag.setAccessible(true);
                    serverExpTag.set(objExpTagTrans, strServerExpTag);
                }

                // TODO: ExpTagTrans클라스에 있는 clientExpTag 찾는다.
                Field clientExpTag = HookHelper.findFieldHierarchically(objExpTagTrans.getClass(), "clientExpTag");
                if(serverExpTag != null) {
                    clientExpTag.setAccessible(true);
//                    clientExpTag.set(objExpTagTrans, "1");
                    clientExpTag.set(objExpTagTrans, strClientExpTag);
                }

            } else {
                Log.d(TAG, "|\tExpTagTrans class : 'null'");
                return res;
            }

//            // TODO: com.kuaishou.client.log.event.packages.nano.ClientEvent클라스에서 ExpTagTransList클라스를 찾는다.
//            clsExpTagTransList = HookHelper.findSubClass(mClientEvent, "ExpTagTransList");
//            if (clsExpTagTransList != null) {
//                Constructor constructor =  HookHelper.findConstructorHierarchically(clsExpTagTransList);
//                objExpTagTransList = constructor.newInstance();
//            }
        }

        //TODO : 2. ExpTagTrans클라스를 ExpTagTransList형식으로 list에 추가한다.
        if(mClientEventBuilder != null) {
            clsExpTagTransArray = HookHelper.findSubClass(mClientEventBuilder, "c");
            if(clsExpTagTransArray != null) {
                Constructor<?> constructor =  HookHelper.findConstructorHierarchically(clsExpTagTransArray, int.class);
                objExpTagTransArray = constructor.newInstance(10);
                Log.d(TAG, "|\tobjExpTagTransArray class : " + objExpTagTransArray.getClass().getName());
                Field field = HookHelper.findFieldHierarchically(objExpTagTransArray.getClass(), "a");
                if(field != null) {
                    List<Object> a = (List<Object>) field.get(objExpTagTransArray);
                    assert a != null;
                 a.add(objExpTagTrans);
                } else {
                    Log.d(TAG, "|\tmClientEventBuilder [a] field class : 'null'");
                    return res;
                }
            }
        } else {
            Log.d(TAG, "|\tmClientEventBuilder class : 'null'");
            return res;
        }

        //TODO : 3. ExpTagTransList를 엔코딩된 바이트형식으로 변환한다.
        getArray = HookHelper.findMethodHierarchically(objExpTagTransArray.getClass(), "a");
        if(getArray != null) {
            objExpTagTransList = getArray.invoke(objExpTagTransArray);
            if(mMessageNano != null && objExpTagTransList != null) {
                Method toByteArray = HookHelper.findMethodHierarchicallyForString(mMessageNano, "toByteArray", "com.google.protobuf.nano.MessageNano");
                if(toByteArray != null) {
                    bArr = (byte[])toByteArray.invoke(null, objExpTagTransList);
                    DbgLog hexLog = new DbgLog();
                    hexLog.LogByteArray(bArr, bArr.hashCode());
                } else {
                    Log.d(TAG,"toByteArray method not fount..");
                    return res;
                }
            } else {
                Log.d(TAG,"mMessageNano class is null.");
                return res;
            }
        }

        //TODO : 4. 바이트로 변환된 자료를 엔코딩한다.
        if(mCustomEncryptorB != null) {
            Class<?> bb = mCustomEncryptorB.getClass();
            Method b = HookHelper.findMethodHierarchically(bb, "a", byte[].class);
            if(b != null && bArr.length > 0) {
                res = (String) b.invoke(mCustomEncryptorB, bArr);
            }
        }
        else {
            Log.d(TAG, "|\tmCustomEncryptorB class : 'null'");
            return res;
        }

        return res;
    }

    private String getQueryId(String keyword, String userId, long timeVal) throws InvocationTargetException, IllegalAccessException {
        String res = "";

        StringBuilder sb = new StringBuilder();

        sb.append(keyword);
        sb.append(userId);
//        sb.append(changeLongValue(System.currentTimeMillis()));
        sb.append(changeLongValue(timeVal));
        sb.append(new Random().nextInt(10000));

        if(mCustomEncryptorB != null) {
            Class<?> bb = mCustomEncryptorB.getClass();
            Method b = HookHelper.findMethodHierarchically(bb, "a", byte[].class);
            if(b != null) {
                res = (String) b.invoke(mCustomEncryptorB, sb.toString().getBytes());
            }
        }
        else {
            Log.d(TAG, "|\tmCustomEncryptorB class : 'null'");
        }
        return res;
    }

    private String changeLongValue(long longValue) {
        String str3;
        if (longValue <= 9999) {
            return String.valueOf(longValue);
        }
        double doubleValue = new BigDecimal((((float) longValue) / 10000.0f) + "").setScale(1, 4).doubleValue();
        if (((int) (10.0d * doubleValue)) % 10 == 0) {
            str3 = "%.0fw";
        } else {
            str3 = "%.1fw";
        }
        return String.format(str3, new Object[]{Double.valueOf(doubleValue)});
    }

    private String getEncryprImeis(String imei) throws InvocationTargetException, IllegalAccessException {
        String res ="";

        if(mEncryptImeis != null) {
            String param = "[" + imei + "]";
            Method method = HookHelper.findMethodHierarchically(mEncryptImeis, "a", byte[].class);
            if(method != null) {
                byte[] encBytes = (byte[])method.invoke(null, param.getBytes());
                res = Base64.encodeToString(encBytes, 2);
            }else {
                Log.d(TAG, "getEncryprImeis() method is null. ");
            }
        }else {
            Log.d(TAG, "getEncryprImeis() mEncryptImeis class is null. ");
        }
        return res;
    }

    private String getDevParam_K31_k89() throws InvocationTargetException, IllegalAccessException {
        String res ="";
        String strNzz = "";
        String strCrtt = "";

        if(mEngineProxy != null) {
            Context context = getAppContext();
            if(context != null) {
                Method method = HookHelper.findMethodHierarchically(mEngineProxy, "getInstance", Context.class);
                if(method != null) {
                    Object instance = method.invoke(null, context);
                    Class<?> clsProxy = instance.getClass();
                    Method nzz = HookHelper.findMethodHierarchically(clsProxy, "nzz", String.class);
                    if(nzz != null) {
                        strNzz = (String) nzz.invoke(instance, "1");
                    }

                    Method crtt = HookHelper.findMethodHierarchically(clsProxy, "crtt");
                    if(crtt != null) {
                        strCrtt = (String) crtt.invoke(instance);
                    }
                }
            } else {
                Log.d(TAG, "getDevParam_K31_k89() Application context is null. ");
            }
        } else {
            Log.d(TAG, "getDevParam_K31_k89() mEngineProxy class is null. ");
        }
        res= strNzz + "&" + strCrtt;
        return res;
    }

    private byte[] changeDevinfo2Binary(Map<String, String> map) throws InvocationTargetException, IllegalAccessException {
        byte[] bArr = null;
        if(mMapChange == null) {
            Log.d(TAG,"mMapChange class is null.");
            return null;
        }
        Method method = HookHelper.findMethodHierarchically(mMapChange, "a", Map.class);
        if(method == null) {
            Log.d(TAG,"a() method is null.");
            return null;
        }

        Object object = method.invoke(null, map);
        if(mMessageNano != null) {
            Method toByteArray = HookHelper.findMethodHierarchicallyForString(mMessageNano, "toByteArray", "com.google.protobuf.nano.MessageNano");
            if(toByteArray != null) {
                bArr = (byte[])toByteArray.invoke(null, object);
                DbgLog hexLog = new DbgLog();
                hexLog.LogByteArray(bArr, bArr.hashCode());
            } else {
                Log.d(TAG,"toByteArray method not fount..");
            }
        } else {
            Log.d(TAG,"mMessageNano class is null.");
        }
        return bArr;
    }

    private String getDeviceInfo(String param) throws InvocationTargetException, IllegalAccessException, UnsupportedEncodingException, NoSuchMethodException, InstantiationException {
        String res = "";
        String encode = "";
        String sign = "";
        String errMsg;
        if(mDeviceInfoClass != null) {
            Constructor<?> constructor = mDeviceInfoClass.getConstructor();
            Object objDeviceInfo = constructor.newInstance();
            //TODO 디바이스파라메터들을 맵형식으로 변환한다.
            Map<String, String> map = createDeviceParam(param);
            // 테스트용
//            Map<String, String> map = createDeviceParam();
//            for(int i = 1; i <= 102; i++) {
//                String k = "k" + i;
//                Log.d(TAG, "devInfo: " + k + "=" + map.get(k));
//            }

            //TODO 맵형식의 자료를 바이트배렬로 변환한다.
            byte[] bArr = changeDevinfo2Binary(map);
            if(bArr == null) {
                errMsg = "changeDevinfo2Binary() is null pointer..";
                return  errMsg;
            }
            // TODO 바이트배렬의 디바이스파라메터 인코딩
            Method method = HookHelper.findMethodHierarchically(mDeviceInfoClass, "atlasEncrypt", String.class, String.class, int.class, byte[].class);
            if(method != null) {
                byte[] encBytes = (byte[]) method.invoke(objDeviceInfo, "ztdfp", "7e46b28a-8c93-4940-8238-4c60e64e3c81", 0, bArr);
                Log.d(TAG,  "param length=" + bArr.length + "encByte lebgth=" + encBytes.length);
                encode = URLEncoder.encode(Base64.encodeToString(encBytes, 0), "utf-8");
                Log.d(TAG, "encode value = " + encode);
            }

            // TODO 인코딩된 자료의 서명
            method = HookHelper.findMethodHierarchically(mDeviceInfoClass, "atlasSign", String.class, String.class, int.class, String.class);
            if(method != null) {
                String timeStamp = String.valueOf(Long.valueOf(System.currentTimeMillis()));
                String signParam = "KUAISHOU" + timeStamp + "2" +  encode;
                sign = (String) method.invoke(objDeviceInfo, "ztdfp", "7e46b28a-8c93-4940-8238-4c60e64e3c81", 0, signParam);

                //TODO HTTPS파라메터형식으로 자료를 넘긴다.
                res = "productName=KUAISHOU&ts="+ timeStamp + "&deviceInfo=" +  URLEncoder.encode(encode, "utf-8") + "&sign=" + sign + "&sv=2";
            }
        } else {
            res = "mDeviceInfoClass is null";
        }

        return res;
    }

    private String getSig(String param) {
        String sig = "";
        if(mCPU != null) {
            Method method = HookHelper.findMethodHierarchically(mCPU, "a", byte[].class);
            if(method != null) {
                try {
                    sig = (String) method.invoke(null, param.getBytes(Charset.forName("UTF-8")));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return sig;
    }

    private String getNssig3(String param) {
        String res;
        //String param = "/rest/system/keyconfigcbbfbbba82fcdc1762683129be3d0b0e";
        Log.d(TAG, "|\tkwaiSecurity : call");
        res = callKwaisgmainNativeFunc(param);
        Log.d(TAG, "|\tkwaiSecurity : " + res);
        return res;
    }

    private String egid(String devInfo) {
        String res = "";
        return res;
    }

    private String phoneCode(String phoneNumber) {
        String res = "";
        Log.d(TAG, "|\tphoneCode() param | " + phoneNumber);
        if(mSecurityPhoneNumberClass != null) {
            Method atlasEncrypt = HookHelper.findMethodHierarchically(mSecurityPhoneNumberClass, "atlasEncrypt", byte[].class);
            if(atlasEncrypt != null) {
                try {
                    byte[] phone = phoneNumber.getBytes();
                    DbgLog hexlog = new DbgLog();
                    hexlog.LogByteArray(phone, phone.hashCode());

                    byte[] encData = (byte[]) atlasEncrypt.invoke(null, phone);
                    Log.d(TAG, "|\tencode bytes size : " + encData.length);
                    hexlog.LogByteArray(encData, encData.hashCode());

                    if(mCustomEncryptorB != null) {
                        Class<?> bb = mCustomEncryptorB.getClass();
                        Method b = HookHelper.findMethodHierarchically(bb, "a", byte[].class);
                        if(b != null) {
                            res = (String) b.invoke(mCustomEncryptorB, encData);
                        }
                    }
                    else {
                        Log.d(TAG, "|\tmCustomEncryptorB class : 'null'");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.d(TAG, "|\tatlasEncrypt method : 'null'");
            }
        }
        else {
            Log.d(TAG, "|\tmSecurityPhoneNumberClass : 'null'");
        }
        return res;
    }

    private String callKwaisgmainNativeFunc(String param) {
        String res = "";
        Class<?> clazz = kwaiSecurity.getClass();
        Method secureMethod = HookHelper.findMethodHierarchically(clazz,"a", String.class, boolean.class, String.class);
        if(secureMethod != null) {
            try {
                res = (String) secureMethod.invoke(kwaiSecurity, param, false, "");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    private Context getAppContext() {
        return AppInfo.mApplicationContext;
    }

    public void createSecurityObject(Class<?> clazz) {
        try {
            if(kwaiSecurity == null) {
                Constructor<?> constructor = clazz.getDeclaredConstructor(Context.class);
                kwaiSecurity = constructor.newInstance(AppInfo.mApplicationContext);
            }
            Log.d(TAG, "kwaiSecurity object created : " + kwaiSecurity.getClass().getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private String getKeyPair(String raw) throws InvocationTargetException, IllegalAccessException {
        KeyPair keys = null;
        String publicKey = "";
        String privateKey = "";
        String timeStr = "";

        if (mSecurityKeyPair != null) {
            Method e = HookHelper.findMethodHierarchically(mSecurityKeyPair, "e");
            if (e != null) {
                keys = (KeyPair) e.invoke(null);
            }

            Method a = HookHelper.findMethodHierarchically(mSecurityKeyPair, "a", PrivateKey.class, String.class);
            if (a != null && keys != null) {
//                timeStr = String.valueOf(System.currentTimeMillis());
                timeStr = raw;
                privateKey = (String) a.invoke(null, keys.getPrivate(), timeStr);
                if(mCustomEncryptorB != null) {
                    Class<?> bb = mCustomEncryptorB.getClass();
                    Method b = HookHelper.findMethodHierarchically(bb, "a", byte[].class);
                    if(b != null) {
                        publicKey = (String) b.invoke(mCustomEncryptorB, keys.getPublic().getEncoded());
                    }
                }
            }
        }
        return "PublicKey=" + publicKey + "&PrivateKey=" + privateKey + "&raw=" + timeStr;
    }

    public void createCustomEncryptClassB(Class<?> clazz) throws InvocationTargetException, IllegalAccessException {
        mCustomEncryptorB = null;
        Method b = HookHelper.findMethodHierarchically(clazz, "b");
        if(b != null) {
            mCustomEncryptorB = b.invoke(null);
        }
    }

    public static String k31(String nzz) {
        try {
            if (TextUtils.isEmpty(nzz)) {
                return "KWE_OTHER";
            }
            JSONObject jSONObject = new JSONObject(nzz);
            String optString = jSONObject.optString("1", "");
            if (!TextUtils.isEmpty(optString)) {
                return "fuels:" + optString;
            }
            String optString2 = jSONObject.optString("3", "");
            if (!TextUtils.isEmpty(optString2)) {
                return "fuels:" + optString2;
            }
            String optString3 = jSONObject.optString("2", "");
            if (!TextUtils.isEmpty(optString3)) {
                return "fuels:" + optString3;
            }
            String optString4 = jSONObject.optString("4", "");
            if (TextUtils.isEmpty(optString4)) {
                return "KWE_OTHER";
            }
            return "fuels:" + optString4;
        } catch (Throwable unused) {
            return "KWE_PE";
        }
    }

    private Map<String, String> createDeviceParam() {
        Map<String, String> map = new HashMap<>();
 /*
        map.put("k1", "KWE_NC");
        map.put("k2", "WiFi");
        map.put("k3", "com.smile.gifmaker");
        map.put("k4", "KWE_NC");
        map.put("k5", "27446517760");
        map.put("k6", "0");
        map.put("k7", "ANDROID_64b3329dc9db223d");
        map.put("k8", "user");
        map.put("k9", "9JGscOw-zmG2a-x0mko6IQ6bL7awQxbJGsyKlHvUXRw");
        map.put("k10", "0");
        map.put("k11", "0");
        map.put("k12", "KWE_NPN");
        map.put("k13", "KWE_NC");
        map.put("k15", "KWE_NC");
        map.put("k16", "SWDG9707");
        map.put("k17", "192.168.1.6");
        map.put("k18", "KWE_NPN");
        map.put("k19", "universal7420");
        map.put("k20", "KWE_NC");
        map.put("k21", "KWE_NC");
        map.put("k22", "7.5.10.14246");
        map.put("k23", "samsung");
        map.put("k24", "KWE_NC");
        map.put("k25", "0");
        map.put("k26", "armeabi-v7a,armeabi");
        map.put("k27", "SM-G920S");
        map.put("k28", "samsungexynos7420");
        map.put("k29", "Dalvik/2.1.0 (Linux; U; Android 7.0; SM-G920S Build/NRD90M)");
        map.put("k30", "NRD90M.G920SKSU3EQL1");
        map.put("k31", "fuels:lJGflpefwZSfnJGfl8Cfl5w=");
        map.put("k32", "KWE_NC");
        map.put("k33", "KWE_NPN");
        map.put("k34", "[4.0,1440,2560,4.0,580.571,580.571]");
        map.put("k35", "7.0");
        map.put("k36", "1.1.8.0");
        map.put("k37", "NRD90M");
        map.put("k38", "KWE_NC");
        map.put("k39", "1595334845241");
        map.put("k40", "samsung/zeroflteskt/zeroflteskt:7.0/NRD90M/G920SKSU3EQL1:user/release-keys");
        map.put("k41", "[ZU,e4:a7:c5:54:57:10]");
        map.put("k42", "KWE_NC");
        map.put("k43", "KWE_PN");
        map.put("k44", "release-keys");
        map.put("k45", "KWE_NC");
        map.put("k46", "2806185984");
        map.put("k47", "G920SKSU3EQL1");
        map.put("k48", "KWE_NC");
        map.put("k49", "0");
        map.put("k50", "KWE_NC");
        map.put("k51", "KWE_NC");
        map.put("k52", "zeroflteskt");
        map.put("k53", "KWE_NC");
        map.put("k54", "KWE_NPN");
        map.put("k55", "14:32:d1:94:2e:28");
        map.put("k56", "KWE_NC");
        map.put("k57", "KWE_PN");
        map.put("k58", "zeroflteskt");
        map.put("k59", "0");
        map.put("k60", "G920SKSU3EQL1");
        map.put("k61", "samsung");
        map.put("k62", "KWE_NC");
        map.put("k63", "dpi");
        map.put("k64", "03157df3798e1f18");
        map.put("k65", "KWE_NC");
        map.put("k66", "9785ea257bf9c42");
        map.put("k67", "cn");
        map.put("k68", "KWE_PN");
        map.put("k69", "KWE_NC");
        map.put("k70", "KWE_NC");
        map.put("k71", "KWE_NPN");
        map.put("k72", "KWE_NC");
        map.put("k73", "KWE_NPN");
        map.put("k74", "KWE_PN");
        map.put("k75", "KWE_PN");
        map.put("k76", "KWE_NC");
        map.put("k77", "KWE_PN");
        map.put("k78", "KWE_NC");
        map.put("k79", "KWE_NC");
        map.put("k80", "KWE_NU");
        map.put("k81", "KWE_NC");
        map.put("k82", "[9,Gravity Sensor,3,Samsung Electronics,19.6133,10000,6.0,5.9604645E-8:65560,Grip Sensor,512,SEMTECH,5.0,0,0.75,5.0:65579,Interrupt Gyroscope Sensor,1,Invensense,34.906586,20000,0.061,0.0010652645:6,LPS25H Barometer Sensor,1,STMicroelectronics,1260.0,200000,1.0,1.0:10,Linear Acceleration Sensor,3,Samsung Electronics,19.6133,10000,6.0,5.9604645E-8:15,MPL Game Rotation Vector,1,Invensense,1.0,10000,6.0,5.9604645E-8:11,MPL Rotation Vector,1,Invensense,1.0,10000,6.0,5.9604645E-8:1,MPU6500 Acceleration Sensor,1,Invensense,39.2266,5000,0.25,0.0011971008:4,MPU6500 Gyroscope Sensor,1,Invensense,34.906586,5000,6.1,0.0010652645:16,MPU6500 Gyroscope sensor UnCalibrated,1,Invensense,34.906586,5000,6.1,0.0010652645:65559,Motion Sensor,1,Samsung Electronics,200.0,0,0.0,0.0]");
        map.put("k83", "KWE_FIRST");
        map.put("k84", "1a5fd9f3829c3eb5");
        map.put("k85", "KWE_N");
        map.put("k86", "KWE_N");
        map.put("k87", "867748516");
        map.put("k88", "exsist");
        map.put("k89", "276977576");
        map.put("k90", "KWE_NC");
        map.put("k91", "KWE_NC");
        map.put("k92", "1595754352986");
        map.put("k93", "{\"1\":\"1\",\"0\":1,\"2\":\"false\",\"8\":\"9\",\"10\":\"KWE_N\"}");
        map.put("k94", "KWE_N");
        map.put("k95", "0|3d74f72getNetworkIso4bdf95e44086b1ed92b7dbec|27");
        map.put("k96", "KWE_N");
        map.put("k97", "KWE_N");
        map.put("k98", "KWE_NS");
        map.put("k99", "KWE_NS");
        map.put("k100", "d7b7d042-d4f2-4012-be60-d97ff2429c17");
        map.put("k101", "KWE_N");
        map.put("k102", "17851ec0-3cda-460e-8361-e421337f5a88");
*/
        map.put("k1", "KWE_NC");
        map.put("k2", "WiFi");
        map.put("k3", "com.smile.gifmaker");
        map.put("k4", "KWE_NC");
        map.put("k5", "16776077312");
        map.put("k6", "0");
        map.put("k7", "ANDROID_962ad1a2fd485522");
        map.put("k8", "user");
        map.put("k9", "6TBrwp-kfscV533Yn6u42mlmnnbvvAUAdG2nGaOFMPY");
        map.put("k10", "0");
        map.put("k11", "0");
        map.put("k12", "KWE_NPN");
        map.put("k13", "KWE_NC");
        map.put("k15", "KWE_NC");
        map.put("k16", "ubuntu");
        map.put("k17", "172.16.2.66");
        map.put("k18", "KWE_NPN");
        map.put("k19", "V1916A");
        map.put("k20", "KWE_NC");
        map.put("k21", "KWE_NC");
        map.put("k22", "7.5.10.14246");
        map.put("k23", "vivo");
        map.put("k24", "KWE_NC");
        map.put("k25", "0");
        map.put("k26", "armeabi-v7a,armeabi");
        map.put("k27", "V1916A");
        map.put("k28", "android_x86");
        map.put("k29", "Dalvik/2.1.0 (Linux; U; Android 5.1.1; V1916A Build/LMY47I)");
        map.put("k30", "LMY47I");
        map.put("k31", "fuels:lZWfwcefx5SfwcefnJ2fkMA=");
        map.put("k32", "KWE_NC");
        map.put("k33", "KWE_NPN");
        map.put("k34", "[3.0,1080,1920,3.0,480.0,480.0]");
        map.put("k35", "5.1.1");
        map.put("k36", "1.1.8.0");
        map.put("k37", "LMY47I");
        map.put("k38", "KWE_NC");
        map.put("k39", "1595848872727");//Long.toString(elasedTime()));
        map.put("k40", "asus/android_x86/x86:5.1.1/LMY47I/V9.5.3.0.LACCNFA:user/release-keys");
        map.put("k41", "[BB15E,00:81:5e:db:db:98]");
        map.put("k42", "KWE_NC");
        map.put("k43", "0.0|0.0");
        map.put("k44", "release-keys");
        map.put("k45", "KWE_NC");
        map.put("k46", "3184037888");
        map.put("k47", "unknown");
        map.put("k48", "KWE_NC");
        map.put("k49", "0");
        map.put("k50", "KWE_NC");
        map.put("k51", "KWE_NC");
        map.put("k52", "V1916A");
        map.put("k53", "KWE_NC");
        map.put("k54", "KWE_NPN");
        map.put("k55", "KWE_OTHER");
        map.put("k56", "KWE_NC");
        map.put("k57", "865166025904767");
        map.put("k58", "aosp");
        map.put("k59", "1");
        map.put("k60", "unknown");
        map.put("k61", "vivo");
        map.put("k62", "KWE_NC");
        map.put("k63", "builder");
        map.put("k64", "00cf4313");
        map.put("k65", "KWE_NC");
        map.put("k66", "962ad1a2fd485522");
        map.put("k67", "cn");
        map.put("k68", "89860079681881303141");
        map.put("k69", "KWE_NC");
        map.put("k70", "KWE_NC");
        map.put("k71", "KWE_NPN");
        map.put("k72", "KWE_NC");
        map.put("k73", "KWE_NPN");
        map.put("k74", "KWE_N");
        map.put("k75", "KWE_N");
        map.put("k76", "KWE_NC");
        map.put("k77", "460002475419849");
        map.put("k78", "KWE_NC");
        map.put("k79", "KWE_NC");
        map.put("k80", "KWE_NU");
        map.put("k81", "KWE_NC");
        map.put("k82", "[9,Gravity,2,QTI,78.4532,200000,0.59999084,0.001:4,Gyroscope,1,STMicroelectronics,17.453293,200000,4.3,0.009576807:1,LSM330 Accelerometer,1,STMicroelectronics,78.4532,200000,0.23,0.01:5,Light sensor,1,AMS TAOS,40000.0,200000,0.1,1.0:10,Linear Acceleration,2,QTI,78.4532,200000,0.59999084,0.0023956299:2,Magnetometer,1,AKM,4911.9995,200000,1.1,0.14953613:3,Orientation Sensor,1,AOSP,360.0,200000,5.63,0.00390625:8,Proximity sensor,2,AMS TAOS,8.000183,200000,0.1,100.0:11,Rotation Vector Sensor,3,AOSP,1.0,200000,5.63,5.9604645E-8]");
        map.put("k83", "KWE_FIRST");
        map.put("k84", "2bf14987f79b1c42");
        map.put("k85", "KWE_N");
        map.put("k86", "{\"KUAISHOU\":\"DFP6480B483F4E367F00CFCAC2D5C284C98A934B3F1C28ADF70ECA6195508139\"}");
        map.put("k87", "KWE_N");
        map.put("k88", "exsist");
        map.put("k89", "3126887128");
        map.put("k90", "KWE_NC");
        map.put("k91", "KWE_NC");
        map.put("k92", "1595849310283");//Long.toString(Long.valueOf(System.currentTimeMillis())));
        map.put("k93", "{\"1\":\"0\",\"0\":1,\"2\":\"false\",\"8\":\"9\",\"10\":\"KWE_N\"}");
        map.put("k94", "KWE_N");
        map.put("k95", "0|6027getNetworkIsob40456getNetworkIso080713eb448c4getNetworkIso7cgetNetworkIso7d9|58");
        map.put("k96", "{\"0\":\"1595849129000\",\"1\":\"1\"}");
        map.put("k97", "KWE_N");
        map.put("k98", "KWE_NS");
        map.put("k99", "KWE_NS");
        map.put("k100", "d7b7d042-d4f2-4012-be60-d97ff2429c17");
        map.put("k101", "KWE_N");
        map.put("k102", "9c6a8ca7-1432-4a65-8873-5e5828857758");
        setParamCRC(map);

        return map;
    }


    private Map<String, String> createDeviceParam(String param) throws InvocationTargetException, IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        String[] key_val;
        byte[] b = new byte[10];
        String key = "";
        String value = "";
        String k9 = "";
        String k31;
        String k31_89 = getDevParam_K31_k89();
        Log.d(TAG, "k31_89 param = | " + k31_89);
        String[] k = k31_89.split("&");
        String socName = "";
        k31 = k31(k[0]);
        String[] kValue = param.split("&");

        for(int i = 0; i < kValue.length; i++)
        {
            key_val = kValue[i].split("=");

            if(key_val.length == 1) {
                key = key_val[0];
                value = "";
            }
            else {
                key = key_val[0];
                value = key_val[1];
            }

            switch (key) {
                case "k39":
                    value = Long.toString(elasedTime());
                    break;
                case "k92":
                    value = Long.toString(Long.valueOf(System.currentTimeMillis()));
                    break;
                case "k31":
                    value = k31.isEmpty() ? "KWE_N" : k31;
                    break;
                case "k95":
                    value = getUUID();
                    break;
                case "k103":
                    socName = value;
                    mSocName = value;
                    value = getCpuInfo(socName);
                    key = "k89";
                    break;
                case "k104":
                    mCpuCount = value;
                    break;
                case "k105":
                    mDevWidth = value;
                    break;
                case "k106":
                    mDevHeight =value;
                    break;

            }
            map.put(key, value);
        }
        if(k9.isEmpty()) {
            map.put("k9", "KWE_N");
        } else {
            map.put("k9", k9);
        }

        setParamCRC(map);

        return map;
    }

    public static String getUUID() {
        StringBuilder sb = new StringBuilder();
        sb.append("0|");
        sb.append (md5Hash(UUID.randomUUID().toString().getBytes()));
        String str3 = sb.toString();
        char[] charArray = str3.toCharArray();
        int i2 = 0;
        for (int i = 0; i < charArray.length; i++) {
            if ((charArray[i] >= 'a' && charArray[i] <= 'z') || (charArray[i] >= 'A' && charArray[i] <= 'Z')) {
                i2++;
            }
        }
        return str3 + "|" + i2;
    }

    public static String selectVal(String str) throws JSONException {
        if (!TextUtils.isEmpty(str)) {
            String optString = new JSONObject(str).optString("0", "");
            if (!TextUtils.isEmpty(optString)) {
                return optString;
            }
        }
        return "";
    }

    private String getCpuInfo(String socName) {
        StringBuilder cpu = new StringBuilder();
        String[] cpuInfo = new String[16];
        cpuInfo[0] = "Processor       : AArch64 Processor rev 2 (aarch64)";
        cpuInfo[1] = "processor       : 0";
        cpuInfo[2] = "processor       : 1";
        cpuInfo[3] = "processor       : 2";
        cpuInfo[4] = "processor       : 3";
        cpuInfo[5] = "processor       : 4";
        cpuInfo[6] = "processor       : 5";
        cpuInfo[7] = "processor       : 6";
        cpuInfo[8] = "processor       : 7";
        cpuInfo[9] = "Features        : fp asimd aes pmull sha1 sha2 crc32";
        cpuInfo[10] = "CPU implementer : 0x41";
        cpuInfo[11] = "CPU architecture: AArch64";
        cpuInfo[12] = "CPU variant     : 0x0";
        cpuInfo[13] = "CPU part        : 0xd03";
        cpuInfo[14] = "CPU revision    : 2";
        cpuInfo[15] = "Hardware        : " + socName;
        for (String s : cpuInfo) {
            char[] array = new char[s.length()];
            s.getChars(0, s.length(), array, 0);
            cpu.append(array, 0, array.length);
        }
        return getCrc32(cpu.toString());
    }

    public static String getCrc32(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return "";
            }
            CRC32 crc32 = new CRC32();
            crc32.update(str.getBytes());
            return String.valueOf(crc32.getValue());
        } catch (Throwable unused) {
            return "";
        }
    }

    public static String customEncrypt2(byte[] bArr) {
        String[] a = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "getNetworkIso", "b", "c", "d", "e", "f"};
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bArr.length; i++) {
            byte b2 = bArr[i];
            int i2 = b2 < 0 ? b2 + 256 : b2;
            stringBuffer.append(a[i2 / 16] + a[i2 % 16]);
        }
        return stringBuffer.toString();
    }

    public static String md5Hash(byte[] bArr) {
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        try {
            return customEncrypt2(MessageDigest.getInstance("MD5").digest(bArr));
        } catch (Throwable unused) {
            return null;
        }
    }


    public static long elasedTime() {
        try {
            return System.currentTimeMillis() - SystemClock.elapsedRealtime();
        } catch (Throwable unused) {
            return 0;
        }
    }

    public static String stringEncode(String str) {
        byte[] bArr = null;
        try {
            byte[] bArr2 = null;
            if (!str.isEmpty()) {
                try {
                    MessageDigest instance = MessageDigest.getInstance("MD5");
                    instance.reset();
                    instance.update(str.getBytes("utf-8"));
                    bArr = instance.digest();
                } catch (Throwable unused) {
                    unused.printStackTrace();
                }
                MessageDigest instance2 = MessageDigest.getInstance("SHA-256");
                instance2.update(bArr);
                bArr2 = instance2.digest();
                return customEncrypt(bArr2);
            }
            bArr = null;
            MessageDigest instance22 = MessageDigest.getInstance("SHA-256");
            instance22.update(bArr);
            bArr2 = instance22.digest();
            return customEncrypt(bArr2);
        } catch (Throwable unused2) {
            return "KWE_PE";
        }
    }

    public static String customEncrypt(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return null;
        }

        char[] charArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();
        char[] b = charArray;
        int f8283c = 31 - Integer.numberOfLeadingZeros(charArray.length);


        if (f8283c <= 8) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            int i2 = 0;
            for (byte b2 : bArr) {
                i += 8;
                int i3 = 32 - i;
                i2 = (((i2 << 8) + ((b2 << 24) >>> 24)) << i3) >>> i3;
                do {
                    sb.append(b[i2 >>> (i - f8283c)]);
                    i -= f8283c;
                    if (i == 0) {
                        i2 = 0;
                    } else {
                        int i4 = 32 - i;
                        i2 = (i2 << i4) >>> i4;
                    }
                } while (i >= f8283c);
            }
            if (i > 0) {
                sb.append(b[i2 << (f8283c - i)]);
            }
            return sb.toString();
        }
        throw new RuntimeException("power over 8");
    }

    public void setParamCRC(Map<String, String> map) {
        int i2 = 1;
        CRC32 crc322 = new CRC32();
        while (i2 <= 102) {
            String str3 = "k" + i2;
            if (!map.containsKey(str3)) {
                map.put(str3, "KWE_OTHER");
            }
            if ("k14".equals(str3)) {
                crc322.update("AND".getBytes());
            } else {
                crc322.update(map.get(str3).getBytes());
            }
            i2++;
        }
        String valueOf2 = String.valueOf(crc322.getValue());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("AND");
        sb2.append(":");
        sb2.append(valueOf2);
        map.put("k14", sb2.toString());
    }

    private String getEnvironmentInfo(String country, String devParam, String egid, String did, String uid) throws IllegalAccessException, JSONException, InvocationTargetException, NoSuchMethodException, InstantiationException, UnsupportedEncodingException {
        String res = "";
        String encode = "";
        String sign = "";
        String errMsg;
        if(mDeviceInfoClass != null) {
            Constructor<?> constructor = mDeviceInfoClass.getConstructor();
            Object objDeviceInfo = constructor.newInstance();

            String carryInfo = createCarryInfo(country, devParam, egid, did, uid);
            byte[] bArr = carryInfo.getBytes();
            // TODO 바이트배렬의 인코딩
            Method method = HookHelper.findMethodHierarchically(mDeviceInfoClass, "atlasEncrypt", String.class, String.class, int.class, byte[].class);
            if(method != null) {
                byte[] encBytes = (byte[]) method.invoke(objDeviceInfo, "ztdfp", "7e46b28a-8c93-4940-8238-4c60e64e3c81", 0, bArr);
                Log.d(TAG,  "param length=" + bArr.length + "encByte lebgth=" + encBytes.length);
                encode = URLEncoder.encode(Base64.encodeToString(encBytes, 0), "utf-8");
                Log.d(TAG, "encode value = " + encode);
            }

            // TODO 인코딩된 자료의 서명
            method = HookHelper.findMethodHierarchically(mDeviceInfoClass, "atlasSign", String.class, String.class, int.class, String.class);
            if(method != null) {
                String timeStamp = String.valueOf(Long.valueOf(System.currentTimeMillis()));
                String signParam = "KUAISHOU" + timeStamp + "2" +  encode;
                sign = (String) method.invoke(objDeviceInfo, "ztdfp", "7e46b28a-8c93-4940-8238-4c60e64e3c81", 0, signParam);

                //TODO HTTPS파라메터형식으로 자료를 넘긴다.
                res = "productName=KUAISHOU&ts="+ timeStamp + "&carryInfo=" +  URLEncoder.encode(encode, "utf-8") + "&sign=" + sign + "&sv=2";
            }
        } else {
            res = "mDeviceInfoClass is null";
        }

        return res;
    }


    public String createCarryInfo(String country, String devParam, String egid, String did, String uid) throws JSONException, InvocationTargetException, IllegalAccessException {
        JSONObject json100102 = new JSONObject();
        JSONObject json100103 = new JSONObject();
        JSONObject dataSection1 = new JSONObject();
        JSONObject dataSection2 = new JSONObject();
        JSONArray jsonDataSection1 = new JSONArray();
        JSONArray jsonDataSection2 = new JSONArray();
        JSONArray jsonCarryInfo = new JSONArray();
        Map<String, String> devInfo = createDeviceParam(devParam);
        Map<String, String> apiLevel = new HashMap<>();
        apiLevel.put("5.0","21");
        apiLevel.put("5.1","22");
        apiLevel.put("6.0","23");
        apiLevel.put("7.0","24");
        apiLevel.put("7.1","25");
        apiLevel.put("8.0","26");
        apiLevel.put("8.1","27");
        apiLevel.put("9.0","28");
        apiLevel.put("10.0","29");

        if(country.equals("CN")) {
            json100102.put("1", "快手");
        } else {
            json100102.put("1", "Kuaishou");
        }

        json100102.put("2", "com.smile.gifmaker");
        json100102.put("3", devInfo.get("k22"));
        json100102.put("4", "100102");
        json100102.put("5", Long.valueOf(System.currentTimeMillis()));
        json100102.put("6", "KUAISHOU");
        json100102.put("7", "1.1.8.0");
        json100102.put("8", egid);
        json100102.put("9", "1:21");
        json100102.put("10", devInfo.get("k95"));
        json100102.put("11", did);

        dataSection1.put("1", "KWE_N");
        dataSection1.put("2", "KWE_N");
        dataSection1.put("3", "KWE_N");
        dataSection1.put("4", devInfo.get("k17"));
        dataSection1.put("5", devInfo.get("k108"));
        dataSection1.put("6", "KWE_N");
        dataSection1.put("7", "KWE_N");
        dataSection1.put("8", "0");

        int batteryChargeInfo = getRandom(0, 4);
        dataSection1.put("9", String.valueOf(batteryChargeInfo));
        dataSection1.put("10", "0");
        dataSection1.put("11", "0");
        dataSection1.put("12", "KWE_NPN");
        dataSection1.put("13", "2");
        dataSection1.put("14", "KWE_N");
        dataSection1.put("15", "7");
        dataSection1.put("16", "KWE_N");
        dataSection1.put("17", "KWE_N");
        dataSection1.put("18", "0");
        dataSection1.put("22", "{\"1\":\"" + devInfo.get("k107") + "\",\"2\":\"" + devInfo.get("k107") + "\",\"3\":\"KWE_N\",\"4\":\"KWE_N\"}");
        dataSection1.put("23", "{\"1\":\"" + devInfo.get("k111") + "\",\"2\":\"" + devInfo.get("k111") + "\"}");
        dataSection1.put("24", "{\"1\":\"" + devInfo.get("k109") + "\",\"2\":\"" + devInfo.get("k109") + "\",\"3\":\"KWE_N\",\"4\":\"KWE_N\"}");

        String[] wifi = devInfo.get("k41").split(",");
        String mac = wifi[1].substring(0, wifi[1].length()-1);
        dataSection1.put("25", "{\"1\":\"" + mac + "\",\"2\":\"" + mac + "\"}");
        dataSection1.put("26", "{\"1\":\"\\\"" + wifi[0].substring(1) + "\\\"\",\"2\":\"" + wifi[0].substring(1) + "\"}");
        dataSection1.put("27", "{\"1\":\"02:00:00:00:00:00\",\"2\":\"02:00:00:00:00:00\"}");

        String ud = "{\"1\":\"KWE_N\",\"2\":\"" + uid + "\",\"3\":false,\"5\":\"KWE_N\",\"4\":0}";
        dataSection1.put("28", ud);

        String batteryRate = String.valueOf(getRandom(25, 100)) + "%";
        dataSection1.put("29", batteryRate);
        dataSection1.put("30", "KWE_N");

        StringBuilder c31 = new StringBuilder();
        c31.append("{");
        c31.append("\"1\":\"REL\",");
        c31.append("\"2\":\"").append(devInfo.get("k61")).append("\",");
        c31.append("\"3\":\"").append(devInfo.get("k27")).append("\",");
        c31.append("\"4\":\"").append(devInfo.get("k23")).append("\",");
        c31.append("\"5\":\"").append(devInfo.get("k19")).append("\",");
        c31.append("\"6\":\"").append(devInfo.get("k58")).append("\",");
        c31.append("\"7\":\"").append(devInfo.get("k28")).append("\",");
        c31.append("\"8\":\"").append(devInfo.get("k52")).append("\",");
        c31.append("\"9\":\"").append(devInfo.get("k64")).append("\",");
        c31.append("\"10\":\"").append(devInfo.get("k35")).append("\",");
        c31.append("\"11\":\"").append(apiLevel.get(devInfo.get("k35"))).append("\",");
        c31.append("\"12\":\"").append(devInfo.get("k30")).append("\",");
        c31.append("\"13\":\"").append(devInfo.get("k40")).append("\",");
        c31.append("\"14\":\"").append(devInfo.get("k37")).append("\",");
        c31.append("\"15\":\"").append(devInfo.get("k47")).append("\",");
        //String[] chip = devInfo.get("k26").split(",");
        c31.append("\"16\":\"").append("armeabi-v7a").append("\",");
        c31.append("\"17\":\"").append(devInfo.get("k44")).append("\",");
        c31.append("\"18\":\"").append(devInfo.get("k8")).append("\",");
        c31.append("\"19\":\"").append(devInfo.get("k16")).append("\",");
        c31.append("\"20\":\"").append(devInfo.get("k60")).append("\",");
        c31.append("\"22\":\"").append("\",");;
        c31.append("\"23\":\"").append(devInfo.get("k47")).append("-")
                .append(devInfo.get("k8")).append(" ")
                .append(devInfo.get("k35")).append(" ")
                .append(devInfo.get("k37")).append(" ")
                .append(devInfo.get("k60")).append(" ")
                .append(devInfo.get("k44")).append("\",");
        c31.append("\"24\":\"").append(devInfo.get("k28")).append("\",");
        c31.append("\"25\":\"").append(devInfo.get("k28")).append("\",");
        c31.append("\"100\":\"").append("{\"1\":\"" + devInfo.get("k107") + "\",\"2\":\"KWE_N\"}").append("\",");
        c31.append("\"101\":\"").append(devInfo.get("k66")).append("\",");
        c31.append("\"102\":\"").append("{\"1\":\"" + devInfo.get("k109") + "\",\"2\":\"KWE_N\"}").append("\",");
        c31.append("\"103\":\"").append("KWE_N").append(",");
        c31.append("\"104\":\"").append(devInfo.get("k111")).append("\",");
        c31.append("\"105\":\"").append("cn\"}\"").append(",");
        dataSection1.put("31", c31.toString());

        StringBuilder c32 = new StringBuilder();
        c32.append("{");
        c32.append("\"0\":\"com.android.inputmethod.pinyin\\/.PinyinIME\",");
        c32.append("\"1\":\"com.android.inputmethod.pinyin::com.android.inputmethod.latin\",");
        c32.append("\"2\":\"}");
        dataSection1.put("32", c32.toString());
        dataSection1.put("33", "1");
        dataSection1.put("37", "{\"1\":\"exist\",\"2\":\"exist\"}");

        String gateway = "IP address HW type Flags HW$" + devInfo.get("k108") + " 0x1 0x2 " + devInfo.get("k112") + " * wlan0$";
        dataSection1.put("38", gateway);
        gateway = devInfo.get("k108") + "*" + "255.255.255.0";
        dataSection1.put("39", gateway);
        dataSection1.put("40", "0");
        dataSection1.put("41", "KWE_N");
        dataSection1.put("42", "0");
        dataSection1.put("43", "com.sec.phone,com.sec.location.nsflp2,system,android.process.media,com.android.systemui,com.android.phone,com.sec.sve,com.trustonic.tuiservice,com.smile.gifmaker,");
        dataSection1.put("44", "{\"1\":\"0\",\"2\":\"0\",\"3\":\"KWE_NPN\",\"4\":\"KWE_NPN\",\"5\":\"KWE_NPN\",\"6\":\"KWE_NPN\",\"7\":\"10000\",\"8\":\"KWE_N\"}");
        dataSection1.put("45", "com.smile.gifmaker");
        dataSection1.put("46", "0");
        dataSection1.put("47", "{\"1\":\"" + devInfo.get("k55") + "\"}");
        dataSection1.put("48", "KWE_NS");
        dataSection1.put("49", "{\"1\":\"4:5\",\"2\":\"6:15\",\"3\":\"11:15\",\"4\":\"1:15\",\"5\":\"11:15\"}");

        jsonDataSection1.put(dataSection1);
        json100102.put("data_section", jsonDataSection1);
        jsonCarryInfo.put(json100102);

        if(country.equals("CN")) {
            json100103.put("1", "快手");
            json100103.put("2", "com.smile.gifmaker");
            json100103.put("3", devInfo.get("k22"));
            json100103.put("4", "100103");
            json100103.put("5", Long.toString(Long.valueOf(System.currentTimeMillis())));
            json100103.put("6", "KUAISHOU");
            json100103.put("7", "1.1.8.0");
            json100103.put("8", egid);
            json100103.put("9", "1:21");
            json100103.put("10", devInfo.get("k95"));
            json100103.put("11", did);

            dataSection2.put("1", "{\"1\":\"KWE_PN\",\"2\":\"KWE_PN\"}");
            dataSection2.put("2", devInfo.get("k66"));
            dataSection2.put("3", "{\"1\":\"KWE_PN\",\"2\":\"KWE_PN\"}");
            dataSection2.put("4", "KWE_PN");
            dataSection2.put("5", "KWE_PN");
            dataSection2.put("6", "cn");
            dataSection2.put("7", "46000");
            dataSection2.put("8", "cn");
            dataSection2.put("9", "2");
            dataSection2.put("10", "1");
            dataSection2.put("11", devInfo.get("k39"));
            dataSection2.put("12", devInfo.get("k37"));
            dataSection2.put("13", devInfo.get("k47"));
            dataSection2.put("14", devInfo.get("k35"));
            dataSection2.put("15", devInfo.get("k23"));
            dataSection2.put("16", devInfo.get("k27"));
            dataSection2.put("17", devInfo.get("k60"));
            dataSection2.put("18", devInfo.get("k30"));
            dataSection2.put("19", devInfo.get("k52"));
            dataSection2.put("20", devInfo.get("k61"));
            dataSection2.put("21", devInfo.get("k58"));
            dataSection2.put("22", devInfo.get("k28"));
            dataSection2.put("23", devInfo.get("k40"));
            dataSection2.put("24", devInfo.get("k64"));
            dataSection2.put("25", devInfo.get("k8"));
            dataSection2.put("26", devInfo.get("k44"));
            dataSection2.put("27", devInfo.get("k16"));
            dataSection2.put("28", devInfo.get("k19"));
            dataSection2.put("29", devInfo.get("k63"));
            dataSection2.put("30", devInfo.get("k26"));
            dataSection2.put("31", devInfo.get("k46"));
            dataSection2.put("32", devInfo.get("notExist"));
            dataSection2.put("33", devInfo.get("k17"));
            dataSection2.put("34", devInfo.get("k29"));
            dataSection2.put("35", devInfo.get("k55"));
            dataSection2.put("36", "\"1\":\"" + devInfo.get("k55") + "\"}");
            dataSection2.put("37", "KWE_N");
            dataSection2.put("38", devInfo.get("k84"));
            dataSection2.put("39", "KWE_N");
            dataSection2.put("40", devInfo.get("k87"));
            dataSection2.put("41", devInfo.get("k82"));
            dataSection2.put("42", "exist");
            dataSection2.put("43", "KWE_N");
            dataSection2.put("44", "KWE_N");
            dataSection2.put("45", "KUAISHOU");
            dataSection2.put("46", "KWE_N");
            dataSection2.put("47", "KWE_N");
            dataSection2.put("48", String.valueOf(getRandom(100, 140)));
            dataSection2.put("49", "{\"1\":\"4:5\",\"2\":\"6:15\",\"3\":\"11:15\",\"4\":\"1:15\",\"5\":\"11:15\"}");
            dataSection2.put("50", "KWE_N");
            dataSection2.put("51", "KWE_N");
            dataSection2.put("52", "KWE_OTHER");
            dataSection2.put("53", "true");
            dataSection2.put("54", c32.toString());
            dataSection2.put("55", "true");
            dataSection2.put("56", "false");
            dataSection2.put("57", "325:323:1");
            dataSection2.put("58", "[com.sec.android.service.health, com.sec.android.app.soundalive, com.sec.android.app.chromecustomizations, com.android.providers.userdictionary, com.dsi.ant.server, com.ims.dm, com.samsung.android.communicationservice, com.android.apps.tag, com.sec.android.easyMover.Agent, com.android.mtp, com.google.android.packageinstaller, com.google.android.googlequicksearchbox, com.sec.android.app.easylauncher, com.android.providers.partnerbookmarks, com.sec.android.RilServiceModeApp, com.android.managedprovisioning, com.android.wallpaperbackup, com.google.android.play.games, com.sec.android.emergencylauncher, com.google.android.feedback, com.sec.android.app.snsimagecache, com.sec.android.fido.uaf.asm, com.android.wallpaper.livepicker, com.google.android.onetimeinitializer, com.google.android.ext.services, com.skt.tservice.utility, com.android.inputdevices, com.google.android.ext.shared, com.android.bluetooth, com.sec.android.uibcvirtualsoftkey, com.sec.android.app.simsettingmgr, com.dsi.ant.service.socket, com.sec.enterprise.mdm.services.simpin, com.sec.android.app.servicemodeapp, com.expway.embmsserver, com.android.printspooler, com.sec.android.easyonehand, com.google.android.apps.maps, com.android.vpndialogs, com.sec.phone, com.android.server.telecom, com.sec.bcservice, com.android.defcontainer, com.sec.android.provider.emergencymode, com.google.android.gm, com.android.systemui, com.android.nfc, com.sec.android.app.safetyassurance, com.android.providers.calendar, com.android.vending, com.android.providers.blockednumber, com.android.mms, com.android.providers.downloads, com.google.android.gsf, com.sec.android.app.factorykeystring, com.sec.app.RilErrorNotifier, com.android.htmlviewer, com.android.bookmarkprovider, com.android.location.fused, com.android.providers.contacts, com.android.bluetoothmidiservice, com.sec.android.AutoPreconfig, com.sec.automation, com.sec.android.mimage.photoretouching, com.sec.android.splitsound, com.hancom.office.viewer, com.sec.android.app.bluetoothtest, com.sec.usbsettings, com.monotype.android.font.tinkerbell, com.sec.android.app.dmb, com.sec.android.app.wlantest, com.android.captiveportallogin, com.google.android.gms, com.google.android.configupdater, com.gd.mobicore.pa, com.android.statementservice, com.sec.android.app.magnifier, com.samsung.android.drivelink.stub, com.sec.imslogger, com.samsung.android.networkdiagnostic, com.sec.android.emergencymode.service, com.dsi.ant.plugins.antplus, com.wsomacp, com.sec.ims, com.android.pacprocessor, com.skt.t_smart_charge, com.sec.android.app.tourviewer, com.sec.android.preloadinstaller, com.sec.android.app.billing, com.sec.android.app.launcher, com.google.android.apps.books, com.sec.android.providers.security, com.sec.android.app.clockpackage, com.sec.android.app.hwmoduletest, com.android.emergency, com.sec.android.Preconfig, com.android.stk, com.samsung.android.svcagent, com.android.egg, com.google.android.gsf.login, com.sec.android.app.camera.plb, com.sec.android.provider.badge, com.sec.android.diagmonagent, com.android.calendar, com.sec.android.gallery3d, com.sec.smartcard.manager, com.android.backupconfirm, com.wssyncmldm, com.skt.prod.dialer, com.android.providers.downloads.ui, com.android.externalstorage, com.android.calllogbackup, com.skt.skaf.Z00000TAPI, com.sec.factory.camera, com.sec.android.providers.tasks, com.sec.android.app.DataCreate, com.android.shell, com.android.proxyhandler, com.android.chrome, com.sec.android.app.dictionary, com.sec.android.app.ringtoneBR, com.sec.android.app.wfdbroker, com.sec.android.app.sysscope, com.android.keychain, com.monotype.android.font.applemint, service.odtcfactory.sec.com.odtcfactoryservice, com.sec.android.app.personalization, com.sec.android.app.parser, com.android.dreams.basic, com.sec.android.app.vepreload, com.sec.factory, com.sec.android.gallery3d.panorama360view, com.dsi.ant.sample.acquirechannels, com.sec.android.app.myfiles, com.android.carrierconfig, com.sec.spen.flashannotate, com.android.cts.priv.ctsshim, com.policydm, com.sec.app.TransmitPowerService, com.sec.android.provider.logsprovider, com.android.phone, com.google.android.apps.plus, android, com.android.certinstaller, com.android.providers.telephony, com.google.android.webview, com.sec.android.wallpapercropper2, com.sec.android.QRreader, com.sec.svoice.lang.en_US, com.sec.android.app.applinker, com.sec.android.widgetapp.webmanual, com.sec.android.soagent, com.google.android.talk, com.sec.android.app.camera, com.android.cts.ctsshim, com.android.settings, com.sec.android.fido.uaf.client, com.android.sharedstoragebackup, com.android.wallpapercropper, com.sec.android.app.clipvideo, com.skp.tstore.startup, com.skms.android.agent, com.sec.android.daemonapp, com.android.providers.settings, com.sec.android.app.SecSetupWizard, com.sec.enterprise.mdm.vpn, com.samsung.android.sm.provider, com.google.android.backuptransport, com.wssnps, com.sec.android.app.music, com.sec.android.inputmethod, com.sec.location.nsflp2, com.google.android.apps.docs, com.monotype.android.font.chococooky, com.sec.android.app.sns3, com.google.android.printservice.recommendation, com.android.providers.media, com.sec.modem.settings, flipboard.boxer.app, com.sec.sve, com.google.android.partnersetup, com.smile.gifmaker, com.trustonic.tuiservice]");
            dataSection2.put("59", "KWE_PN:2");
            dataSection2.put("60", "KWE_NPN");
            dataSection2.put("61", mCpuCount);
            dataSection2.put("62", devInfo.get("k46"));
            dataSection2.put("63", devInfo.get("68141632:536870912"));
            dataSection2.put("64", wifi[0].substring(1));
            dataSection2.put("65", "/system/lib64/libsec-ril.so");

            String[] batteryChager = {"", "AC charger", "USB charger", "", "Wireless charger"};
            dataSection2.put("66", batteryChager[batteryChargeInfo]);
            dataSection2.put("67", "false");
            dataSection2.put("68", "KWE_PN");
            dataSection2.put("69", "KWE_PN");
            dataSection2.put("70", "KWE_PN");
            dataSection2.put("71", "KWE_PN");
            dataSection2.put("72", "0");
            dataSection2.put("73", batteryRate);
            dataSection2.put("74", "zh");
            dataSection2.put("75", "com.sec.phone,com.sec.location.nsflp2,system,android.process.media,com.android.systemui,com.android.phone,com.sec.sve,com.trustonic.tuiservice,com.smile.gifmaker,");
            dataSection2.put("76", "0");
            dataSection2.put("77", "0");
            dataSection2.put("78", "{\"1\":\"KWE_N\",\"2\":\"" + ud + "\",\"3\":false,\"5\":\"KWE_N\",\"4\":0}");
            dataSection2.put("79", "[]");
            dataSection2.put("80", "0");
            dataSection2.put("81", devInfo.get("k103"));
            dataSection2.put("82", "7");
            dataSection2.put("83", "{\"1\":\"KWE_PN\",\"2\":\"KWE_PN\"}");
            dataSection2.put("84", apiLevel.get(devInfo.get("k35")));

            String[] dispInfos = devInfo.get("k34").split(",");
            dataSection2.put("85", dispInfos[2] + "X" + dispInfos[1]);

            String size = Math.round(Float.parseFloat(mDevHeight)) + " mm x " + Math.round(Float.parseFloat(mDevWidth)) + " mm";
            dataSection2.put("86", size);
            dataSection2.put("87", "KWE_N");
            dataSection2.put("88", "KWE_N");
            dataSection2.put("89", "KWE_PN");
            dataSection2.put("90", "KWE_PN");
            dataSection2.put("91", "KWE_N");
            dataSection2.put("92", "KWE_N");
            dataSection2.put("93", "{\"1\":\"KWE_PN\",\"2\":\"KWE_PN\"}");
            dataSection2.put("94",  Long.toString(Long.valueOf(System.currentTimeMillis())));

            jsonDataSection2.put(dataSection2);
            json100103.put("data_section", jsonDataSection2);
            jsonCarryInfo.put(json100103);
        }
        System.out.println(jsonCarryInfo.toString());
        return jsonCarryInfo.toString();
    }

    private int getRandom(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    private String getReportConfigParam(String egid, String appVer) throws JSONException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, UnsupportedEncodingException {
        JSONObject carry = new JSONObject();
        carry.put("sdkVersion", "1.1.8.0");
        carry.put("appVersion", appVer);
        carry.put("packageName", "com.smile.gifmaker");
        carry.put("productName", "KUAISHOU");
        carry.put("outId", egid);


        String res = "";
        String encode = "";
        String sign = "";
        String errMsg;
        if(mDeviceInfoClass != null) {
            Constructor<?> constructor = mDeviceInfoClass.getConstructor();
            Object objDeviceInfo = constructor.newInstance();

            String carryInfo = carry.toString();
            byte[] bArr = carryInfo.getBytes();
            // TODO 바이트배렬의 인코딩
            Method method = HookHelper.findMethodHierarchically(mDeviceInfoClass, "atlasEncrypt", String.class, String.class, int.class, byte[].class);
            if(method != null) {
                byte[] encBytes = (byte[]) method.invoke(objDeviceInfo, "ztdfp", "7e46b28a-8c93-4940-8238-4c60e64e3c81", 0, bArr);
                Log.d(TAG,  "param length=" + bArr.length + "encByte lebgth=" + encBytes.length);
                encode = URLEncoder.encode(Base64.encodeToString(encBytes, 0), "utf-8");
                Log.d(TAG, "encode value = " + encode);
            }

            // TODO 인코딩된 자료의 서명
            method = HookHelper.findMethodHierarchically(mDeviceInfoClass, "atlasSign", String.class, String.class, int.class, String.class);
            if(method != null) {
                String timeStamp = String.valueOf(Long.valueOf(System.currentTimeMillis()));
                String signParam = "KUAISHOU" + timeStamp + "2" +  encode;
                sign = (String) method.invoke(objDeviceInfo, "ztdfp", "7e46b28a-8c93-4940-8238-4c60e64e3c81", 0, signParam);

                //TODO HTTPS파라메터형식으로 자료를 넘긴다.
                res = "productName=KUAISHOU&ts="+ timeStamp + "&carryInfo=" +  URLEncoder.encode(encode, "utf-8") + "&sign=" + sign + "&sv=2";
            }
        } else {
            res = "mDeviceInfoClass is null";
        }

        return res;
    }

    private String getReportConfig(String conf) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String res = "";

        if(mDeviceInfoClass != null) {
            Constructor<?> constructor = mDeviceInfoClass.getConstructor();
            Object objDeviceInfo = constructor.newInstance();

            byte[] bArr = Base64.decode(conf, 0);
            // TODO 바이트배렬의 인코딩
            Method method = HookHelper.findMethodHierarchically(mDeviceInfoClass, "atlasDecrypt", String.class, String.class, int.class, byte[].class);
            if(method != null) {
                byte[] decBytes = (byte[]) method.invoke(objDeviceInfo, "ztdfp", "7e46b28a-8c93-4940-8238-4c60e64e3c81", 0, bArr);
                if(decBytes != null) {
                    res = new String(decBytes);
                    Log.d(TAG, "param length=" + bArr.length + "encByte lebgth=" + decBytes.length);
                    Log.d(TAG, "decode value = " + res);
                }
            }
        } else {
            res = "mDeviceInfoClass is null";
        }

        return res;
    }

}

