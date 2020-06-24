package andhook.test;

import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import andhook.lib.HookHelper;

public class ResponseInfo extends Thread {
    private static final String TAG = "HTTP";
    private int mIdentifyCode = 0;
    private String mSource;
    Object mResponse;
    private boolean isOnlyRequest;
    private String logPath;

    ResponseInfo(Object obj, int identifyCode, String source) {
        mResponse = obj;
        mIdentifyCode = identifyCode;
        mSource = source;
        isOnlyRequest = false;
        logPath = AppHooking.logPath;
    }

    public void setOnlyRequest(boolean onlyRequest) {
        isOnlyRequest = true;
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "-----------------------------------------------------------------------");
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE] : " + mResponse.getClass().getName() + "----start ");
            if(isOnlyRequest) {
                getRequestInfo();
            }else {
                getResponseInfo();
            }
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE] : end ------------------------ ");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * OKHTTP3 Response class정보 얻는다. (class명 w0.a0)
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void getResponseInfo() throws IllegalAccessException, InvocationTargetException, IOException {
        Class<?> clazz = mResponse.getClass();
        Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE] : " + mResponse.toString());
//        if(isPrint == false && mResponse.toString().contains("n/system/abtest/config")) {
//            isPrint = true;
//        }
//        if(isPrint == false)
//            return;
        //TODO Response - Request w0.a0 class
        Field requestField = HookHelper.findFieldHierarchically(clazz, "a");
        if(requestField != null) {
            Object request = (Object)requestField.get(mResponse);
            RequestInfo requestInfo = new RequestInfo(request, mIdentifyCode, mSource);
            requestInfo.putInfo();
        } else {
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:Request] : error.");
        }

        //TODO Response Header
        Field headerField = HookHelper.findFieldHierarchically(clazz, "f");
        if(headerField != null) {
            Object header = (Object)headerField.get(mResponse);
            String headStr = getHeadersInfo(header);
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:Header] : " + headStr);
        } else {
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:Header] : error.");
        }

        // TODO Response Body
        Field bodyField = HookHelper.findFieldHierarchically(clazz, "g");
        if(bodyField != null) {
            Object responseBody = (Object)bodyField.get(mResponse);
            getResponseBodyInfo(responseBody);
        } else {
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:Body] : error.");
        }

    }

    private void getRequestInfo() throws IOException {

        RequestInfo requestInfo = new RequestInfo(mResponse, mIdentifyCode, mSource);
        requestInfo.putInfo();
    }
    /**
     * OKHTTP3 Respons Body class(w0.b0)
     * @param responseBody
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void getResponseBodyInfo(Object responseBody) throws IOException {
        Class<?> clazz = responseBody.getClass();
        long contentLength = 0;
        int streamReadCount;
        int contentReadCount;
        String strContentType = null;
        Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:Body - class] : " + clazz.getName());

        Method method = HookHelper.findMethodHierarchically(clazz, "e");
        if (method != null) {
            try {
                contentLength = (long) method.invoke(responseBody);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:Body - Content Length] : " + contentLength);
        } else {
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:Body - Content Length] : method error");
        }

        method = HookHelper.findMethodHierarchically(clazz, "f");
        if (method != null) {
            try {
                Object contentType = (Object) method.invoke(responseBody);
                strContentType = contentType.toString();
                Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:Body - Content Type]  : " + strContentType );
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:Body - Content Type] : 'f' method error.");
        }

        method = HookHelper.findMethodHierarchically(clazz, "a");
        if (method != null) {
            try {
                if(contentLength >0) {
                    int count = 0;
                    String fileName = logPath + "/" + mIdentifyCode + "-1";
                    File file = new File(fileName);
                    if(!file.canWrite())
                        file.setWritable(true);

                    InputStream input = (InputStream) method.invoke(responseBody);
                    OutputStream output = new FileOutputStream(file);
                    byte[] data = new byte[(int) contentLength];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        output.write(data, (int) total, count);
                        total += count;
                    }
                    //input.close();
                    output.flush();
                    output.close();
                    Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE Body - Content] " + total + ": " + fileName);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE Body - Content] : 'a' method error.");
        }


        method = HookHelper.findMethodHierarchically(clazz, "g");
        if (method != null) {
            Object content = null;
            try {
                content = (Object) method.invoke(responseBody);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (content != null) {
                Method readBytes = HookHelper.findMethodHierarchically(content.getClass(), "s");
                if (readBytes != null) {
                    byte[] bytes = new byte[0];
                    try {
                        bytes = (byte[]) readBytes.invoke(content);
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    }

                    if(bytes.length > 0) {
                        String fileName = logPath + "/" + mIdentifyCode + "-2";
                        OutputStream output = new FileOutputStream(fileName);
                        output.write(bytes, 0, bytes.length);
                        output.flush();
                        output.close();
                        Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE Body - Content] " + fileName);
                    }
                    Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:Body - Content Stream] length : " + bytes.length);
                    //if( strContentType != null && !strContentType.contains("zip"))
                    //    DbgLog.LogByteArray(bytes, mIdentifyCode);
                }
            }
        }

    }


    private String getHeadersInfo(Object header) {
        return header.toString();
    }
}
