package andhook.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class GetActivity {

    public static Activity getTopActivity() {
        Activity topActivity = null;
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Method getATMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            Object activityThread = getATMethod.invoke(null);
            activitiesField.setAccessible(true);
            Object activityClientRecord;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ArrayMap activities = (ArrayMap) activitiesField.get(activityThread);
                activityClientRecord = activities.valueAt(0);
            }else{
                HashMap activities = (HashMap) activitiesField.get(activityThread);
                activityClientRecord = activities.values();
            }
            Class activityClientRecordClass = Class.forName("android.app.ActivityThread$ActivityClientRecord");
            Field activityField = activityClientRecordClass.getDeclaredField("activity");
            activityField.setAccessible(true);
            topActivity = (Activity) activityField.get(activityClientRecord);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return topActivity;
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Activity getRunningActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread")
                    .invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Didn't find the running activity");
    }

//    public static void GetViewInfo(Activity runningActivity) {
//        ViewGroup views = (ViewGroup) runningActivity.getWindow().getDecorView();
//        for (int i=0; i<views.getChildCount(); i++) {
//            View chV = views.getChildAt(i);
//            if (chV instanceof ViewGroup) {
//                findChildren(chV, chVArray);
//                Log.d(TAG, "*** Group View " + chV.toString() + " ID is: " + Integer.toString(chV.getId()));
//            } else {
//                String viewClassName = chV.getClass().getName();
//                Log.d(TAG, "*** Single View " + viewClassName + " ID is: " + Integer.toString(chV.getId()));
//            }
//            chVArray.add(chV);
//        }
//    }


}

