package andhook.test;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import andhook.lib.HookHelper;

public class Retrofit extends Thread {
    private static final String TAG = "RESP";
    private int mIdentifyCode = 0;
    private String mSource;
    Object mResponse;

    Retrofit(Object obj, int identifyCode, String source) {
        mResponse = obj;
        mIdentifyCode = identifyCode;
        mSource = source;
    }

    @Override
    public void run() {
        try {
            getResponseBodyInfo();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void getResponseBodyInfo() throws IllegalAccessException, InvocationTargetException {
        Class<?> clazz = mResponse.getClass();
        Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE] : " + clazz.getName());
        Field responseField = HookHelper.findFieldHierarchically(clazz, "b");
        if(responseField != null) {
            Object responseBody = (Object) responseField.get(mResponse);
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE] ResponseBody class: " + responseBody.getClass().getName());
            if(responseBody != null) {
                String info = getObjectInfo(responseBody);
                Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE] Body: " + info);
            } else {
                Field errBodyField = HookHelper.findFieldHierarchically(clazz, "c");
                if(errBodyField != null) {
                    Object errBody = (Object) errBodyField.get(mResponse);
                    getErrorBodyInfo(errBody);
                }
            }
        }

        responseField = HookHelper.findFieldHierarchically(clazz, "a");
        if(responseField != null) {
            Object response = (Object) responseField.get(mResponse);
            ResponseInfo responseInfo = new ResponseInfo(response, mIdentifyCode, mSource);
            responseInfo.start();
        }
    }

    private String getObjectInfo(Object responseBody) throws IllegalAccessException {
        Class<?> clazz = responseBody.getClass();
        Field field = HookHelper.findFieldHierarchically(clazz, "a");
        StringBuilder str = new StringBuilder();
        if(field != null) {
            Object obj = (Object)field.get(responseBody);
            str.append(obj.getClass().getName());
        }

        field = HookHelper.findFieldHierarchically(clazz, "b");
        if(field != null) {
            int obj = (int)field.get(responseBody);
            str.append(", ").append(obj);
        }

        field = HookHelper.findFieldHierarchically(clazz, "c");
        if(field != null) {
            String obj = (String)field.get(responseBody);
            str.append(", ").append(obj);
        }

        field = HookHelper.findFieldHierarchically(clazz, "d");
        if(field != null) {
            String obj = (String)field.get(responseBody);
            str.append(", ").append(obj);
        }

        field = HookHelper.findFieldHierarchically(clazz, "e");
        if(field != null) {
            long obj = (long)field.get(responseBody);
            str.append(", ").append(obj);
        }

        field = HookHelper.findFieldHierarchically(clazz, "f");
        if(field != null) {
            long obj = (long)field.get(responseBody);
            str.append(", ").append(obj);
        }

        field = HookHelper.findFieldHierarchically(clazz, "g");
        if(field != null) {
            Object obj = (Object)field.get(responseBody);
            if(obj != null) {
                Class<?> cls = obj.getClass();
                Method m = HookHelper.findMethodHierarchically(cls, "a");
                if(m != null) {
                    try {
                        String name = (String) m.invoke(obj);
                        str.append(", {name=").append(name);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                m = HookHelper.findMethodHierarchically(cls, "b");
                if(m != null) {
                    try {
                        String ticket = (String) m.invoke(obj);
                        str.append(", ticket=").append(ticket);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                m = HookHelper.findMethodHierarchically(cls, "c");
                if(m != null) {
                    try {
                        String uid = (String) m.invoke(obj);
                        str.append(", uid=").append(uid);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                str.append("}");
            }
        }

        field = HookHelper.findFieldHierarchically(clazz, "h");
        if(field != null) {
            long obj = (long)field.get(responseBody);
            str.append(", ").append(obj);
        }

        field = HookHelper.findFieldHierarchically(clazz, "i");
        if(field != null) {
            long obj = (long)field.get(responseBody);
            str.append(", ").append(obj);
        }

        return String.valueOf(str);
    }

    private void getErrorBodyInfo(Object errBody) throws InvocationTargetException, IllegalAccessException {
        Class<?> clazz = errBody.getClass();
        Method method = HookHelper.findMethodHierarchically(clazz, "D");
        if (method != null) {
            String contentType = (String) method.invoke(errBody);
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:ErrorBody] : " + contentType);
        }

        method = HookHelper.findMethodHierarchically(clazz, "e");
        if (method != null) {
            long contentLegth = (long) method.invoke(errBody);
            Log.d(TAG, "\t" + mIdentifyCode + "-[RESPONSE:ErrorBody - Content Length] : " + contentLegth);
        }
        Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:ErrorBody - class] : " + clazz.getName());

//
//        if (clazz.getName().equals("w0.b0$a")) {
//
//        } else if (clazz.getName().equals("w0.b0$b")) {
//
//        }
    }

}