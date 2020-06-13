package andhook.test;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class AppInfo {

    @SuppressLint("PrivateApi")
    public static ApplicationInfo GetAppInfo(){
        Class activityThreadClass = null;
        ApplicationInfo mAppInfo = null;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            Object curActivityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field boundApplication = activityThreadClass.getDeclaredField("mBoundApplication");
            boundApplication.setAccessible(true);
            Object objAppBindData = boundApplication.get(curActivityThread);

            Class<?> clsAppBind = Class.forName("android.app.ActivityThread$AppBindData");
            Field appInfo = clsAppBind.getDeclaredField("appInfo");
            appInfo.setAccessible(true);
            mAppInfo = (ApplicationInfo)appInfo.get(objAppBindData);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return mAppInfo;
    }

//    public static void GetLoadedApkInfo(){
//        try {
//            Class clsLoadedApk = Class.forName("android.app.LoadedApk");
//            Field loadedApk = clsAppBind.getDeclaredField("info");
//            appInfo.setAccessible(true);
//            mAppInfo = (ApplicationInfo)appInfo.get(objAppBindData);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

}
