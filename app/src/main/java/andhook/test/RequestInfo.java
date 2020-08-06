package andhook.test;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import andhook.lib.HookHelper;

public class RequestInfo extends Thread{

    private static final String TAG = "HTTP";
    private int mIdentifyCode = 0;
    private String mSource;
    Object mRequest;

    RequestInfo(Object obj, int identifyCode, String source) {
        mRequest = obj;
        mIdentifyCode = identifyCode;
        mSource = source;
    }

    RequestInfo(Object obj, int identifyCode) {
        mRequest = obj;
        mIdentifyCode = identifyCode;
        mSource = "";
    }

    @Override
    public void run() {
        try {
            getRequestInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putInfo() throws IOException {
        getRequestInfo();
    }


    private void getRequestInfo() throws IOException {
        byte[] requestBytes;
        StringBuilder logMsg = new StringBuilder();
        Class<?> cls = mRequest.getClass();

        logMsg.append("-----" + mIdentifyCode + "-[---REQUEST---] --> start ++++++++++++++++++++\n");
        logMsg.append("\t" + mIdentifyCode + "-[---REQUEST---] : " + mRequest.toString() + "\n");

        Field fld = HookHelper.findFieldHierarchically(cls, "headers");
        if(fld != null) {
            try {
                Object hd = (Object)fld.get(mRequest);
                Method toStringMethod = HookHelper.findMethodHierarchically(hd.getClass(), "toString");
                String header = (String) toStringMethod.invoke(hd);
                logMsg.append("\t" + mIdentifyCode + "-[---REQUEST---] Header : " + header + "\n");
            } catch (IllegalAccessException e) {
                logMsg.append("\t" + mIdentifyCode + "-[---REQUEST---] Error : 'headers' field get() error\n");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                logMsg.append("\t" + mIdentifyCode + "-[---REQUEST---] Error : 'headers' toString() error\n");
                e.printStackTrace();
            }
        }

        fld = HookHelper.findFieldHierarchically(cls, "body");
        if(fld != null) {
            try {
                Object body = (Object) fld.get(mRequest);
                if(body != null) {
                    Class<?> clazz = body.getClass();
                    String msg = getBodyInfo(body);
                    logMsg.append(msg);

                    if (clazz.getName().equals("w0.v")) {
                        Field field = HookHelper.findFieldHierarchically(clazz, "d");
                        if (field != null) {
                            List<Object> d = (List<Object>) field.get(body);
                            logMsg.append("multiplex body count : " + d.size() + "\n");
                            logMsg.append("=======================================\n");
                            for(int i = 0; i < d.size(); i++)
                            {
                                Object b = d.get(i);
                                Field a = HookHelper.findFieldHierarchically(b.getClass(), "a");
                                Object q = a.get(b);
                                Method toStringMethod = HookHelper.findMethodHierarchically(q.getClass(), "toString");
                                String contStr = (String) toStringMethod.invoke(q);
                                logMsg.append("\t" + mIdentifyCode + "- Body" + i + " : " + contStr + "\n");

                                Field bf = HookHelper.findFieldHierarchically(b.getClass(), "b");
                                Object z = bf.get(b);
                                msg = getBodyInfo(z);
                                logMsg.append(msg);
                                logMsg.append("=======================================\n");
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
        File file = new File("/data/data/com.smile.gifmaker/log/request.txt");
        FileWriter fw = new FileWriter(file, true);
        fw.write(String.valueOf(logMsg));
        fw.close();
        logMsg.append("-----" + mIdentifyCode + "-[---REQUEST---] --> end ++++++++++++++++++++\n");

        Log.d(TAG, String.valueOf(logMsg));
    }

    private String getBodyInfo(Object body) throws
            IllegalAccessException, IOException, InvocationTargetException {
        Class<?> clazz = body.getClass();
        List<String> encodedNames = null;
        List<String> encodedValues = null;
        StringBuilder logMsg = new StringBuilder();

        long conLength = 0;
        Object contentType = null;
        logMsg.append("\t")
                .append(mIdentifyCode)
                .append("-[---REQUEST---] Body-Class Name : ")
                .append(clazz.getName())
                .append("\n");

        Method method = HookHelper.findMethodHierarchically(clazz, "contentType");
        if (method != null) {
            contentType = (Object) method.invoke(body);
        }
        if( contentType != null ) {
            logMsg.append("\t" + mIdentifyCode + "-[---REQUEST---] Body-Content Type : " + contentType.toString() + "\n");
        }

        method = HookHelper.findMethodHierarchically(clazz, "contentLength");
        if (method != null) {
            conLength = (long) method.invoke(body);
        }
        logMsg.append("\t" + mIdentifyCode + "-[---REQUEST---] Body-Content Length : " + conLength + "\n");


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
                logMsg.append("\t" + "- Body:" + encodedNames.get(i) +"=" + encodedValues.get(i) + "\n");
            }
////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (clazz.getName().equals("w0.z$b")) {
            byte[] buffer;
            Field nameFld = HookHelper.findFieldHierarchically(clazz, "c");
            if (nameFld != null) {
                buffer = (byte[]) nameFld.get(body);
                if (buffer.length > 0) {
                    logMsg.append("\t" + "-[---REQUEST---] Body: byte array " + buffer.length + "bytes\n");
                    logMsg.append("\t" + new String(buffer));
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
                bData = ByteUtil.serialize(b);
                if (bData.length > 0) {
                    logMsg.append("\t" + mIdentifyCode + "-[---REQUEST---] Body: Object byte array " + bData.length + "bytes\n");
                    //DbgLog hexLog = new DbgLog(TAG, bData, hashCode);
                   // hexLog.start();
                }
            }
////////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (clazz.getName().equals("w0.z$c")) {
            Field field = HookHelper.findFieldHierarchically(clazz, "b");
            if (field != null) {
                File b = (File) field.get(body);
                logMsg.append("\t"+ mIdentifyCode + "-[---REQUEST---] File : " + b.getName() + "\n");
            }
        } else {

        }

        return String.valueOf(logMsg);
    }

    public void getRequestUrl() {
        byte[] requestBytes;
        StringBuilder logMsg = new StringBuilder();
        Class<?> cls = mRequest.getClass();
        Log.d(TAG,"-[---REQUEST---start]");
        Log.d(TAG,"-[---REQUEST---] : " + mRequest.toString());
        Log.d(TAG,"-[---REQUEST---end]");
    }

}
