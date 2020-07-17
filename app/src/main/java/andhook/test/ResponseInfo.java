package andhook.test;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import android.util.Base64;
import java.util.List;

import javax.security.cert.Certificate;
import javax.security.cert.CertificateEncodingException;
import javax.security.cert.X509Certificate;

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
            if(isOnlyRequest) {
                Log.d(TAG, getRequestInfo(mResponse));
            }else {
                getResponse();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    void getResponse() throws IOException, IllegalAccessException, InvocationTargetException {
        String responseTag;
        String header;
        String request;
        String sslInfo;
        StringBuilder msg = new StringBuilder();

//        if(!bodyInfo.isEmpty()) {
            msg.append("\t").append(mIdentifyCode).append("-[").append(mSource).append("-RESPONSE] : start----------------------").append("\n");
            responseTag = getResponseTag(mResponse);
            msg.append(responseTag);
//
            header = getHeadersInfo(mResponse);
            msg.append(header);
//
            sslInfo = getSSLInfo(mResponse);
            msg.append(sslInfo);

            request = getRequestInfo(mResponse);
            msg.append(request);

            String bodyInfo = getResponseBodyInfo(mResponse);
            msg.append(bodyInfo);
            msg.append("\t").append(mIdentifyCode).append("-[").append(mSource).append("-RESPONSE] : end ------------------------").append("\n");
            Log.d(TAG, String.valueOf(msg));
  //      }
    }

    /**
     * OKHTTP3 Response class정보 얻는다. (class명 w0.a0)
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private String getResponseTag(Object response) throws IllegalAccessException {
        Class<?> clazz = response.getClass();
        StringBuilder logMsg = new StringBuilder();
        
        logMsg.append("\t")
                .append(mIdentifyCode)
                .append("-[")
                .append(mSource)
                .append("-RESPONSE] : ")
                .append(response.toString())
                .append("\n");

        logMsg.append("\t")
                .append(mIdentifyCode)
                .append("-[")
                .append(mSource)
                .append("-RESPONSE] class : ")
                .append(response.getClass().getName())
                .append("\n");

        //TODO Response - network response w0.a0 class
//        Field field = HookHelper.findFieldHierarchically(clazz, "h");
//        if(field != null) {
//            Object networkResponse = (Object)field.get(response);
//            if(networkResponse != null)
//                logMsg.append("\t")
//                        .append(mIdentifyCode)
//                        .append("-[").append(mSource)
//                        .append("-RESPONSE networkResponse] : ")
//                        .append(networkResponse.toString())
//                        .append("\n");
//
//        } else {
//            logMsg.append("\t")
//                    .append(mIdentifyCode)
//                    .append("-[")
//                    .append(mSource)
//                    .append("-RESPONSE:networkResponse] : error.")
//                    .append("\n");
//        }
//
//        //TODO Response - cache response w0.a0 class
//        field = HookHelper.findFieldHierarchically(clazz, "i");
//        if(field != null) {
//            Object cacheResponse = (Object)field.get(response);
//            if(cacheResponse != null)
//                logMsg.append("\t")
//                        .append(mIdentifyCode)
//                        .append("-[")
//                        .append(mSource)
//                        .append("-RESPONSE cacheResponse] : ")
//                        .append(cacheResponse.toString())
//                        .append("\n");
//        } else {
//            logMsg.append("\t")
//                    .append(mIdentifyCode)
//                    .append("-[")
//                    .append(mSource)
//                    .append("-RESPONSE:cacheResponse] : error.")
//                    .append("\n");
//        }
//
//        //TODO Response - prior response w0.a0 class
//        field = HookHelper.findFieldHierarchically(clazz, "j");
//        if(field != null) {
//            Object priorResponse = (Object)field.get(response);
//            if(priorResponse != null)
//                logMsg.append("\t")
//                        .append(mIdentifyCode)
//                        .append("-[")
//                        .append(mSource)
//                        .append("-RESPONSE priorResponse] : ")
//                        .append(priorResponse.toString())
//                        .append("\n");
//        } else {
//            logMsg.append("\t")
//                    .append(mIdentifyCode)
//                    .append("-[")
//                    .append(mSource)
//                    .append("-RESPONSE:priorResponse] : error.")
//                    .append("\n");
//        }
        return String.valueOf(logMsg);
    }

    /**
     * SSL 관련 (w0.p) class
     * @param response
     */
    private String getSSLInfo(Object response) throws IllegalAccessException {
        Field field;
        StringBuilder logMsg = new StringBuilder();
        Class<?> clazz = response.getClass();
        field = HookHelper.findFieldHierarchically(clazz, "e");
        if(field != null) {
            Object sslResponse = (Object)field.get(response);
            if(sslResponse != null) {
                clazz = sslResponse.getClass();
                field = HookHelper.findFieldHierarchically(clazz, "a");
                if(field != null) {
                    try {
                        // w0.d0
                        Object a = (Object)field.get(sslResponse);
                        if(a != null) {
                            Field subField = HookHelper.findFieldHierarchically(a.getClass(), "javaName");
                            if(subField != null) {
                                String javaName = (String)subField.get(a);
                                logMsg.append("\t").append(mIdentifyCode).append("-[").append(mSource).append("-RESPONSE:SSL - javaName] : ").append(javaName).append("\n");
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }


                field = HookHelper.findFieldHierarchically(clazz, "b");
                if(field != null) {
                    try {
                        Object b = (Object)field.get(sslResponse);
                        if(b != null) {
                            logMsg.append("\t")
                                    .append(mIdentifyCode)
                                    .append("-[")
                                    .append(mSource)
                                    .append("-RESPONSE:SSL - b] : ")
                                    .append(b.toString())
                                    .append("\n");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(logPath + "/" + mIdentifyCode + ".cer");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//                DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
                field = HookHelper.findFieldHierarchically(clazz, "c");
                if(field != null) {
                    try {
                        List<?> certificateArr = (List<?>) field.get(sslResponse);
                        if(certificateArr != null) {
                            //StringBuffer crt = new StringBuffer(certificateArr.toString());
//                            try {
//                                outStream.writeUTF(certificateArr.toString());
//                                outStream.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                            //Certificate xert = (Certificate)certificateArr.get(0);
                            Log.d(TAG, "certificateArrList size " + certificateArr.size());
//                            for(int i = 0; i < certificateArr.size(); i++) {
//                                Log.d(TAG, "certificateArrList write" + i);
//                                X509Certificate x509Certificate = (X509Certificate) certificateArr.get(i);
//                                byte[] bytes = x509Certificate.getEncoded();
//                                Log.d(TAG, "certificateArrList write1..");
//                                FileOutputStream os = new FileOutputStream(logPath + "/" + mIdentifyCode + "-" +i + ".cer");
//                                os.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
//                                os.write(Base64.encode(bytes, 2));
//                                os.write("-----END CERTIFICATE-----\n".getBytes("US-ASCII"));
//                                os.close();
//                            }
                            //Log.d(TAG, String.valueOf(crt));
//                            logMsg.append("\t").append(mIdentifyCode).append("-[")
//                                    .append(mSource)
//                                    .append("-RESPONSE:SSL - certificateArrList] : ")
//                                    .append(certificateArr.toString())
//                                    .append("\n");


                        }
                    } catch (IllegalAccessException e) {
                        Log.d(TAG, "certificateArrList IllegalAccessException..");
//                        e.printStackTrace();
//                    } catch (CertificateEncodingException e) {
//                        Log.d(TAG, "certificateArrList CertificateEncodingException..");
//                        e.printStackTrace();
//                    } catch (FileNotFoundException e) {
//                        Log.d(TAG, "certificateArrList FileNotFoundException..");
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        Log.d(TAG, "certificateArrList IOException..");
//                        e.printStackTrace();
                    }
                }

                field = HookHelper.findFieldHierarchically(clazz, "d");
                if(field != null) {
                    try {
                        List<Certificate> localCertificates = (List<Certificate>)field.get(sslResponse);
                        if(localCertificates != null) {
                            logMsg.append("\t")
                                    .append(mIdentifyCode)
                                    .append("-[").append(mSource)
                                    .append("-RESPONSE:SSL - localCertificatesList] : ")
                                    .append(localCertificates.toString())
                                    .append("\n");
                        }
                    } catch (IllegalAccessException e) {
                        Log.d(TAG, "localCertificatesList IllegalAccessException..");
                        e.printStackTrace();
                    }
                }


            }
        } else {
            logMsg.append("\t")
                    .append(mIdentifyCode)
                    .append("-[")
                    .append(mSource)
                    .append("-RESPONSE:SSL Response] : error.")
                    .append("\n");
        }
        return String.valueOf(logMsg);
    }

    private String getRequestInfo(Object response) throws IOException, IllegalAccessException {
        Class<?> clazz = response.getClass();
        StringBuilder logMsg = new StringBuilder();
        Field field = HookHelper.findFieldHierarchically(clazz, "a");
        if(field != null) {
            Object request = (Object)field.get(mResponse);
            if(request != null) {
//                logMsg.append("\t")
//                        .append(mIdentifyCode)
//                        .append("-[")
//                        .append(mSource)
//                        .append("-RESPONSE] : ")
//                        .append(request.toString())
//                        .append("\n");
                RequestInfo requestInfo = new RequestInfo(request, mIdentifyCode, mSource);
                requestInfo.putInfo();
//                requestInfo.start();
            }
        } else {
            logMsg.append("\t")
                    .append(mIdentifyCode)
                    .append("-[")
                    .append(mSource)
                    .append("-RESPONSE:Request] : error.")
                    .append("\n");
        }
        return String.valueOf(logMsg);
    }
    
    
    /**
     * OKHTTP3 Respons Body class(w0.a0-> 'g' field)
     * @param response
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String getResponseBodyInfo(Object response) throws IOException, IllegalAccessException, InvocationTargetException {
        StringBuilder logMsg = new StringBuilder();
        Class<?> clazz = response.getClass();

        Object responseBody = cloneResponseBody(response);

        if(responseBody != null) {
            clazz = responseBody.getClass();
            long contentLength = 0;
            String strContentType = null;
            logMsg.append("\t").
                    append(mIdentifyCode)
                    .append("-[")
                    .append(mSource)
                    .append("-RESPONSE:Body - class] : ")
                    .append(clazz.getName())
                    .append("\n");

            if(clazz.getName().equals("w0.e0.f.f")) {
                return getResponseBodyInfo_other(responseBody);
            }

            // TODO Response body content length 값을 읽는다.(w0.b0.e())
            Method method = HookHelper.findMethodHierarchically(clazz, "e");
            if (method != null) {
                try {
                    contentLength = (long) method.invoke(responseBody);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                logMsg.append("\t")
                        .append(mIdentifyCode)
                        .append("-[")
                        .append(mSource)
                        .append("-RESPONSE:Body - Content Length] : ")
                        .append(contentLength)
                        .append("\n");
            } else {
                logMsg.append("\t")
                        .append(mIdentifyCode)
                        .append("-[").append(mSource)
                        .append("-RESPONSE:Body - Content Length] : method error")
                        .append("\n");
            }

            // TODO Response body content type값을 읽는다.(w0.b0.t())
            method = HookHelper.findMethodHierarchically(clazz, "f");
            if (method != null) {
                try {
                    // contentType is 'w0.t' class
                    Object contentType = (Object) method.invoke(responseBody);
                    strContentType = contentType.toString();
                    logMsg.append("\t")
                            .append(mIdentifyCode)
                            .append("-[")
                            .append(mSource)
                            .append("-RESPONSE:Body - Content Type]  : ")
                            .append(strContentType)
                            .append("\n");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                logMsg.append("\t")
                        .append(mIdentifyCode)
                        .append("-[").append(mSource)
                        .append("-RESPONSE:Body - Content Type] : 'f' method error.")
                        .append("\n");
            }

            if(contentLength > 0 && strContentType != null && !isSkipInfo(strContentType)) {
                // TODO Response body content값을 스트림형식으로 읽는다.(w0.b0.a())
                method = HookHelper.findMethodHierarchically(clazz, "a");
                if (method != null) {
                    InputStream input = (InputStream) method.invoke(responseBody);
                    //String msg = writeFile(input, contentLength);
                    String msg = FileUtils.writeResponseData(input, contentLength, mIdentifyCode);
                    logMsg.append(msg);
                } else {
                    logMsg.append("\t")
                            .append(mIdentifyCode)
                            .append("-[")
                            .append(mSource)
                            .append("-RESPONSE Body - Content] : 'a' method error.")
                            .append("\n");
                }
            }
         }

        return String.valueOf(logMsg);
    }

    private String getResponseBodyInfo_other(Object responseBody) {
        StringBuilder logMsg = new StringBuilder();
        Class<?> clazz = responseBody.getClass();
        long contentLength = -1;
        String strContentType = null;

        // TODO: Content Length
        Method method = HookHelper.findMethodHierarchically(clazz, "e");
        if (method != null) {
            try {
                contentLength = (long) method.invoke(responseBody);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            logMsg.append("\t")
                    .append(mIdentifyCode)
                    .append("-[")
                    .append(mSource)
                    .append("-RESPONSE:Body - Content Length] : ")
                    .append(contentLength)
                    .append("\n");
        } else {
            logMsg.append("\t")
                    .append(mIdentifyCode)
                    .append("-[").append(mSource)
                    .append("-RESPONSE:Body - Content Length] : method error")
                    .append("\n");
        }
        // TODO: Content Type
        method = HookHelper.findMethodHierarchically(clazz, "f");
        if (method != null) {
            try {
                // contentType is 'w0.t' class
                Object contentType = (Object) method.invoke(responseBody);
                if (contentType != null) {
                    Class<?> clsContent = contentType.getClass();
                    Field a = HookHelper.findFieldHierarchically(clsContent, "a");
                    strContentType = (String) a.get(contentType);
                    logMsg.append("\t")
                            .append(mIdentifyCode)
                            .append("-[")
                            .append(mSource)
                            .append("-RESPONSE:Body - Content Type]  : ")
                            .append(strContentType)
                            .append("\n");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            logMsg.append("\t")
                    .append(mIdentifyCode)
                    .append("-[").append(mSource)
                    .append("-RESPONSE:Body - Content Type] : 'f' method error.")
                    .append("\n");
        }

        // TODO: Content
        method = HookHelper.findMethodHierarchically(clazz, "g");
        if (method != null) {
            try {
                Object content = (Object) method.invoke(responseBody);
                if(content != null) {
                    logMsg.append("\t")
                            .append(mIdentifyCode)
                            .append("-[")
                            .append(mSource)
                            .append("-RESPONSE:Body - Content] class : ")
                            .append(content.getClass().getName())
                            .append("(")
                            .append(content.toString())
                            .append(")")
                            .append("\n");

                    if(contentLength > 0 && strContentType != null && !isSkipInfo(strContentType)) {
                        // TODO Response body content값을 스트림형식으로 읽는다.(w0.b0.a())
                        method = HookHelper.findMethodHierarchically(clazz, "a");
                        if (method != null) {
                            InputStream input = (InputStream) method.invoke(responseBody);
                            //String msg = writeFile(input, contentLength);
                            String msg = null;
                            try {
                                msg = FileUtils.writeResponseData(input, contentLength, mIdentifyCode);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            logMsg.append(msg);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(logMsg);
    }

    private String getHeadersInfo(Object response) throws IllegalAccessException {
        StringBuilder logMsg = new StringBuilder();
        Class<?> clazz = response.getClass();
        Field field = HookHelper.findFieldHierarchically(clazz, "f");
        if(field != null) {
            Object header = (Object)field.get(response);
            logMsg.append("\t")
                    .append(mIdentifyCode)
                    .append("-[").append(mSource)
                    .append("-RESPONSE:Header] : ")
                    .append(header.toString())
                    .append("\n");
        } else {
            logMsg.append("\t")
                    .append(mIdentifyCode)
                    .append("-[")
                    .append(mSource)
                    .append("-RESPONSE:Header] : error.")
                    .append("\n");
        }
        return String.valueOf(logMsg);
    }


    private Object cloneResponseBody(Object response) {
        Class<?> clazz = response.getClass();
        Object cloneSource = null;
        Object content = null;
        Object cloneBody = null;
        long clone_length = 0;

        Field field = HookHelper.findFieldHierarchically(clazz, "g");
        if(field == null) {
            Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:Body] : error." + "\n");
            return null;
        }
        ////// Response Body class is 'w0.b0'
        Object responseBody = null;
        try {
            responseBody = (Object)field.get(response);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(responseBody != null) {
            //// w0.b0.g()
            Method method = HookHelper.findMethodHierarchically(responseBody.getClass(), "g");
            if(method != null) {
                //// x0.f class
                Object source = null;
                try {
                    source = method.invoke(responseBody);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if(source != null) {
                    if(source.getClass().getName().equals("x0.s"))
                        return responseBody;
                    //// x0.f.clone()
                    Method m = HookHelper.findMethodHierarchically(source.getClass(), "clone");
                    if(m!= null) {
                        try {
                            //// Clone x0.f class
                            cloneSource = (Object) m.invoke(source);
                            if(cloneSource != null) {
                                Field fld = HookHelper.findFieldHierarchically(cloneSource.getClass(), "b");
                                if (fld != null) {
                                    clone_length = (long) fld.get(cloneSource);
//                                    Log.d(TAG, "\t" + mIdentifyCode + "-[" + mSource + "-RESPONSE:Body] : cloneSource legth = " + clone_length);
                                }
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            method = HookHelper.findMethodHierarchically(responseBody.getClass(), "f");
            if(method != null) {
                //// w0.t
                try {
                    content = (Object)method.invoke(responseBody);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            if(cloneSource != null && content != null) {
                ////w0.b0.a(w0.t, long, x0.i)
                method = HookHelper.findMethodHierarchicallyForString(responseBody.getClass(), "a", "w0.t", "long", "x0.i");
                if(method != null) {
                    ////Clone object of 'w0.b0' object
                    try {
                        cloneBody = method.invoke(responseBody, content, clone_length , cloneSource);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
        return cloneBody;
    }

    public synchronized String writeFile(InputStream input, long length) throws IOException {
        int count = 0;
        File file = null;
        String fileName;
        OutputStream output = null;
        StringBuilder logMsg = new StringBuilder();
        int fileNumber = 0;

        if(length <= 0)
            return "";

        while(true) {
            if(file != null)
                file = null;
            fileNumber++;
            fileName = logPath + "/" + mIdentifyCode + "-" + fileNumber;
            logMsg.append("\t")
                    .append(mIdentifyCode)
                    .append("-[")
                    .append(mSource)
                    .append("-RESPONSE Body - Content saving] file name :")
                    .append(fileName)
                    .append("\n");
            file = new File(fileName);
            if(!file.exists())
                break;
            logMsg.append("\t")
                    .append(mIdentifyCode)
                    .append("-[")
                    .append(mSource)
                    .append("-RESPONSE Body - Content saving] file length :")
                    .append(file.length())
                    .append(" in length ")
                    .append(length)
                    .append("\n");

            if(file.length() == length) {
                return "";
            }
        }

        try {
            output = new FileOutputStream(file);
            byte[] data = new byte[(int) length];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
                total += count;
            }

            logMsg.append("\t")
                    .append(mIdentifyCode)
                    .append("-[")
                    .append(mSource)
                    .append("-RESPONSE Body - Content saved] ")
                    .append(total).append(": ")
                    .append(fileName)
                    .append("\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(output != null)
                output.close();
        }
        return String.valueOf(logMsg);
    }

    private boolean isSkipInfo(String url) {
        if( url.contains("image") ||
                url.contains("octet-stream") ||
                url.contains("zip")) {
            return true;
        }
        return false;
    }
}
