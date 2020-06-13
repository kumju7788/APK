package andhook.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.channels.FileChannel;

import andhook.lib.AndHook;

public class HookInit {
    public static final String TAG = "JAVA";
    private static Context mContext;

    @TargetApi(Build.VERSION_CODES.O)
    public static void Inite_Hook() throws Exception {
        Log.d(TAG, "[--]Inite_JAVA Hook");

        Log.d(TAG, "This is dex code. Start hooking process in Java world.");
        String targrtDir = "/data/data/com.smile.gifmaker/files/";
        String srcDir = "/data/local/tmp/";
        String hkFile = "libAK.so";
        filecopy(srcDir + hkFile, targrtDir + hkFile);
        AndHook.ensureNativeLibraryLoaded(null);

        //java층 후크를 실행하기 위해서 처음에 로드한다.
        AppHooking.init();


        // 디버그용으로 클라스인스턴스 얻음.
        JSONObject json = new JSONObject();

        try {
            final Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field currentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            currentActivityThreadField.setAccessible(true);
            Object currentActivityThread = currentActivityThreadField.get(null);

            Field mHField = activityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            Handler mH = (Handler) mHField.get(currentActivityThread);

            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            Handler.Callback oriCallback = (Handler.Callback) mCallbackField.get(mH);
            Handler.Callback hookCallBack = new HookCallback(oriCallback);
            mCallbackField.set(mH, hookCallBack);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void filecopy(String from, String to) throws Exception{
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;

        try {
            fis = new FileInputStream(from);
            fos = new FileOutputStream(to);
            in = fis.getChannel();
            out = fos.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) out.close();
            if (in != null) in.close();
            if (fis != null) fis.close();
            if (fos != null) fos.close();
        }
    }


    public static void hookActivityManagerService() throws Throwable {
        Class<?> activityManagerNativeClass=Class.forName("android.app.ActivityManagerNative");
        //4.0以后，ActivityManagerNative有gDefault单例来进行保存，这个代码中一看就知道了
        Field gDefaultField=activityManagerNativeClass.getDeclaredField("gDefault");
        gDefaultField.setAccessible(true);
        Object gDefault=gDefaultField.get(null);

        Class<?> singleton=Class.forName("android.util.Singleton");
        //mInstance는 오직 한개의 오브젝트만이다.
        Field mInstance=singleton.getDeclaredField("mInstance");
        mInstance.setAccessible(true);

        Object originalIActivityManager=mInstance.get(gDefault);
        Log.d("[app]","originalIActivityManager="+originalIActivityManager);

        //동적proxy를 통해 인터페이스오브젝트 생성
        Class<?> iActivityManagerInterface=Class.forName("android.app.IActivityManager");
        Object object= Proxy.newProxyInstance(iActivityManagerInterface.getClassLoader(),
                new Class[]{iActivityManagerInterface}, new IActivityManagerServiceHandler(originalIActivityManager));
        //원본객체를 교체한다.
        mInstance.set(gDefault,object);
        Log.d("[app]","Hook AMS성공");
    }

    public static class IActivityManagerServiceHandler implements InvocationHandler {

        private Object base;

        public IActivityManagerServiceHandler(Object base) {
            this.base = base;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //判断是不是activityResumed,如果是的话，那么拦截参数，然后反射获取实例就好
            if (method.getName().equals("activityResumed")){
                //这里拿到想要的IBinder啦，就是token
                IBinder iBinder= (IBinder) args[0];
                Log.d("[app]","执行activityResumed方法了,参数toke为"+iBinder);
                Class<?> clazz=Class.forName("android.app.ActivityThread");
                Method method1=clazz.getDeclaredMethod("currentActivityThread");
                Object object=method1.invoke(null);

                Method getActivity=clazz.getDeclaredMethod("getActivity",IBinder.class);
                Activity mActivity= (Activity) getActivity.invoke(object,iBinder);
                Log.d("[app]","Hook AMS以后:当前的Activity为:"+mActivity);
            }
            return method.invoke(base,args);
        }
    }

}
