package andhook.test;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import andhook.lib.HookHelper;

public class RequestInfo{

    private static final String TAG = "HTTP";
    private int mIdentifyCode = 0;
    private String mSource;
    Object mRequest;

    RequestInfo(Object obj, int identifyCode, String source) {
        mRequest = obj;
        mIdentifyCode = identifyCode;
        mSource = source;
    }

    public void putInfo() throws IOException {
        getRequestInfo();
    }


    private void getRequestInfo() throws IOException {
        byte[] requestBytes;
        Class<?> cls = mRequest.getClass();
        Log.d(TAG, "-----" + mIdentifyCode + "-[---REQUEST---] --> start ++++++++++++++++++++");
        Log.d(TAG,  "\t" + mIdentifyCode + "-[---REQUEST---] : " + mRequest.toString());

        Field fld = HookHelper.findFieldHierarchically(cls, "headers");
        if(fld != null) {
            try {
                Object hd = (Object)fld.get(mRequest);
                Method toStringMethod = HookHelper.findMethodHierarchically(hd.getClass(), "toString");
                String header = (String) toStringMethod.invoke(hd);
                Log.d(TAG,  "\t" + mIdentifyCode + "-[---REQUEST---] Header : " + header);
            } catch (IllegalAccessException e) {
                Log.d(TAG,  "\t" + mIdentifyCode + "-[---REQUEST---] Error : 'headers' field get() error");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Log.d(TAG,  "\t" + mIdentifyCode + "-[---REQUEST---] Error : 'headers' toString() error");
                e.printStackTrace();
            }
        }

        fld = HookHelper.findFieldHierarchically(cls, "body");
        if(fld != null) {
            try {
                Object body = (Object) fld.get(mRequest);
                if(body != null) {
                    Class<?> clazz = body.getClass();
                    getBodyInfo(body);

                    if (clazz.getName().equals("w0.v")) {
                        Field field = HookHelper.findFieldHierarchically(clazz, "d");
                        if (field != null) {
                            List<Object> d = (List<Object>) field.get(body);
                            Log.d(TAG, "multiplex body count : " + d.size());
                            Log.d(TAG, "=======================================");
                            for(int i = 0; i < d.size(); i++)
                            {
                                Object b = d.get(i);
                                Field a = HookHelper.findFieldHierarchically(b.getClass(), "a");
                                Object q = a.get(b);
                                Method toStringMethod = HookHelper.findMethodHierarchically(q.getClass(), "toString");
                                String contStr = (String) toStringMethod.invoke(q);
                                Log.d(TAG,  "\t" + mIdentifyCode + "-[---REQUEST---] Body" + i + " : " + contStr);

                                Field bf = HookHelper.findFieldHierarchically(b.getClass(), "b");
                                Object z = bf.get(b);
                                getBodyInfo(z);
                                Log.d(TAG, "=======================================");
                            }
                        }
                    } else {

                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "-----" + mIdentifyCode + "-[---REQUEST---] --> end ++++++++++++++++++++");
    }

    private void getBodyInfo(Object body) throws
            IllegalAccessException, IOException, InvocationTargetException {
        Class<?> clazz = body.getClass();
        List<String> encodedNames = null;
        List<String> encodedValues = null;
        long conLength = 0;
        Object contentType = null;
        Log.d(TAG,  "\t" + mIdentifyCode + "-[---REQUEST---] Body-Class Name : " + clazz.getName());

        Method method = HookHelper.findMethodHierarchically(clazz, "contentType");
        if (method != null) {
            contentType = (Object) method.invoke(body);
        }
        if( contentType != null ) {
            Log.d(TAG,  "\t" + mIdentifyCode + "-[---REQUEST---] Body-Content Type : " + contentType.toString());
        }

        method = HookHelper.findMethodHierarchically(clazz, "contentLength");
        if (method != null) {
            conLength = (long) method.invoke(body);
        }
        Log.d(TAG,  "\t" + mIdentifyCode + "-[---REQUEST---] Body-Content Length : " + conLength);


        if (clazz.getName().equals("okhttp3.FormBody")) {
            Field nameFld = HookHelper.findFieldHierarchically(clazz, "encodedNames");
            if (nameFld != null) {
                encodedNames = (List<String>) nameFld.get(body);
            }
            Field valuesFld = HookHelper.findFieldHierarchically(clazz, "encodedValues");
            if (valuesFld != null) {
                encodedValues = (List<String>) valuesFld.get(body);
            }
            assert encodedNames != null;
            assert encodedValues != null;
            for (int i = 0; i < encodedNames.size(); i++) {
                Log.d(TAG,  "\t" + mIdentifyCode + "-[---REQUEST---] Body:" + encodedNames.get(i) +"=" + encodedValues.get(i));
            }
////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (clazz.getName().equals("w0.z$b")) {
            byte[] buffer;
            Field nameFld = HookHelper.findFieldHierarchically(clazz, "c");
            if (nameFld != null) {
                buffer = (byte[]) nameFld.get(body);
                if (buffer.length > 0) {
                    //DbgLog hexLog = new DbgLog(TAG, buffer, hashCode);
                    //hexLog.start();
                }
            }
////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (clazz.getName().equals("w0.z$a")) {
            byte[] bData;
            Field field = HookHelper.findFieldHierarchically(clazz, "b");
            if (field != null) {
                Object b = (Object) field.get(body);
                bData = BytesUtil.serialize(b);
                if (bData.length > 0) {
                    //DbgLog hexLog = new DbgLog(TAG, bData, hashCode);
                   // hexLog.start();
                }
            }
////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (clazz.getName().equals("w0.z$c")) {
            Field field = HookHelper.findFieldHierarchically(clazz, "b");
            if (field != null) {
                File b = (File) field.get(body);
                Log.d(TAG,  "\t" + mIdentifyCode + "-[---REQUEST---] File : " + b.getName());
            }
        } else {

        }
    }
}
