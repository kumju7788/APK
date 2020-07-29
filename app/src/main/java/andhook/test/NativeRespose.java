package andhook.test;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.CRC32;

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

    List<String> mParam;
    List<String> mRequest;

    static Class<?> mSecurityPhoneNumberClass;
    static Class<?> mSecurityKeyPair;
    static Object mCustomEncryptorB;
    static Class<?> mCPU;
    static Class<?> mDeviceInfoClass;
    static Class<?> mMapChange;
    static Class<?> mEngineProxy;
    static Class<?> mMessageNano;


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
                mEngineProxy = clazz;
                break;
            case MAP_CHANGE:
                mMapChange = clazz;
                break;
            case MAP_MESSAGE_NANO:
                mMessageNano = clazz;
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
                    sb.append("" + res);
                }
                continue;
            }

            if(mRequest.get(i).equals("deviceInfo")) {
                param = getParam("info");
                if(!param.isEmpty()) {
                    try {
                        res = getDeviceInfo("info");
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
                    sb.append("" + res);
                }
                continue;
            }

            if(mRequest.get(i).equals("keypair")) {
                String publicKey = "";
                String privateKey = "";
                try {
                    getKeyPair(publicKey, privateKey);
                    sb.append("PublicKey=")
                            .append(publicKey)
                            .append("&PrivateKey")
                            .append(privateKey);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }


        }
        return String.valueOf(sb);
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
            }
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

    private String getDeviceInfo(String key) throws InvocationTargetException, IllegalAccessException, UnsupportedEncodingException, NoSuchMethodException, InstantiationException {
        String res = "";
        String encode = "";
        String sign = "";
        String errMsg;
        if(mDeviceInfoClass != null) {
            Constructor<?> constructor = mDeviceInfoClass.getConstructor();
            Object objDeviceInfo = constructor.newInstance();
            //TODO 디바이스파라메터들을 맵형식으로 변환한다.
            Map<String, String> map = createDeviceParam(key);
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
                    Log.d(TAG, "|\tmencode bytes size : " + encData.length);
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

    private void getKeyPair(String publicKey, String privateKey) throws InvocationTargetException, IllegalAccessException {
        KeyPair keys = null;

        if (mSecurityKeyPair != null) {
            Method e = HookHelper.findMethodHierarchically(mSecurityKeyPair, "e");
            if (e != null) {
                keys = (KeyPair) e.invoke(null);
            }

            Method a = HookHelper.findMethodHierarchically(mSecurityKeyPair, "a", PrivateKey.class, String.class);
            if (a != null && keys != null) {
                String timeStr = String.valueOf(System.currentTimeMillis());
                privateKey = (String) a.invoke(keys.getPrivate(), timeStr);
                if(mCustomEncryptorB != null) {
                    Class<?> bb = mCustomEncryptorB.getClass();
                    Method b = HookHelper.findMethodHierarchically(bb, "a", byte[].class);
                    if(b != null) {
                        publicKey = (String) b.invoke(null, keys.getPublic().getEncoded());
                    }
                }
            }
        }
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


    private Map<String, String> createDeviceParam(String paramKey) throws InvocationTargetException, IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        String[] key_val;
        byte[] b = new byte[10];
        String key = "";
        String value = "";
        String k9 = "";
        String k31;
        String k31_89 = getDevParam_K31_k89();
        String[] k = k31_89.split("&");
        String socName = "";
        k31 = k31(k[0]);

        for(int i = 0; i < mParam.size(); i++)
        {
            System.out.println(mParam.get(i));
            key_val = mParam.get(i).split("=");
            if(key_val[0].equals(paramKey)) {
                if(key_val.length == 3) {
                    key = key_val[1];
                    value = key_val[2];
                } else if(key_val.length == 2){
                    key = key_val[1];
                    value = "";
                }
            } else {
                if(key_val.length == 1) {
                    key = key_val[0];
                    value = "";
                }
                else {
                    key = key_val[0];
                    value = key_val[1];
                }
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
                    value = getCpuInfo(socName);
                    key = "k89";
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

}