package andhook.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import andhook.lib.HookHelper;
import andhook.test.AppHooking;

import static andhook.lib.HookHelper.findFieldHierarchically;
import static andhook.lib.HookHelper.findMethodHierarchically;

public class HookThread extends Thread {
    private static final int HOOK_TOUCH_LIVE_PLAZA          = 1;
    private static final int HOOK_CHECK_IS_SLIDINGPAN_OPEN  = 2;
    private static final int HOOK_JSON_OBJECT_CLASS         = 3;
    private static final int HOOK_MAIN_ACTIVITY_LIKE_CLICK  = 4;
    private static final int HOOK_LIKE_CLICK_TEST           = 5;
    private static final int HOOK_TTNET_CHECK               = 6;

    private static final String TAG = HookInit.TAG;
    private Class<?> clazz;
    private int hookIndex;

    static Class<?>[] rClasses;
    static int rClassCount;

    Class<?> paramCls1;
    Class<?> paramCls2;

    Method orgMethod = null;
    Method replaceMethod = null;


    HookThread(Class<?> cls, int index) {
        clazz = cls;
        hookIndex = index;
    }

    public void setRelationClass(Class<?>[] clss, int count) {
        rClasses = clss;
        rClassCount = count;
    }

    private static Class<?> getRelationClassForName(String clsName) {
        for (int i = 0; i < rClassCount; i++) {
            if(rClasses[i].getName().equals(clsName)) {
                return rClasses[i];
            }
        }
        return null;
    }


    @Override
    public void run() {
        switch(hookIndex) {
            case HOOK_TOUCH_LIVE_PLAZA:
                Log.d(TAG, "[===] HOOK_TOUCH_LIVE_PLAZA signal input...");
                orgMethod = findMethodHierarchically(clazz, "onInterceptTouchEvent", MotionEvent.class);
                replaceMethod = findMethodHierarchically( AppHooking.class, "LivePlaza_Click", Class.class, MotionEvent.class);
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_TOUCH_LIVE_PLAZA hooking success...");

                Log.d(TAG, "[===] openPane(View, int) hooking.");
                orgMethod = findMethodHierarchically(clazz, "openPane", View.class, int.class);
                replaceMethod = findMethodHierarchically( AppHooking.class, "CheckSlidingPanOpened", Class.class, View.class, int.class);
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] openPane(View, int) hooking success.");

                Log.d(TAG, "[===] closePane(View, int) hooking.");
                orgMethod = findMethodHierarchically(clazz, "closePane", View.class, int.class);
                replaceMethod = findMethodHierarchically( AppHooking.class, "CheckSlidingPanClosed", Class.class, View.class, int.class);
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] closePane(View, int) hooking success.");

                break;

            case HOOK_CHECK_IS_SLIDINGPAN_OPEN:
//                Log.d(TAG, "[===] doBindView(View) hooking.");
//                orgMethod = findMethodHierarchically(clazz, "doBindView", View.class);
//                replaceMethod = findMethodHierarchically( AppHooking.class, "myDoBindView", Class.class, View.class);
//                HookHelper.hook(orgMethod, replaceMethod);
//                AppHooking.SlidingPaneLayout = clazz;
//                Log.d(TAG, "[===] doBindView(View) hooking success.");
                break;

            case HOOK_JSON_OBJECT_CLASS:
                Log.d(TAG, "[===] HOOK_JSON_OBJECT_CLASS signal input...");
                orgMethod = findMethodHierarchically(clazz, "put", String.class, Object.class);
                Log.d(TAG, "orgMethod : " + orgMethod.getName());
                replaceMethod = findMethodHierarchically(AppHooking.class,"my_JsonObjectPut", Class.class, String.class, Object.class);
                Log.d(TAG, "replaceMethod : " + replaceMethod.getName());
                HookHelper.hook(orgMethod, replaceMethod);

                orgMethod = findMethodHierarchically(clazz, "put", String.class, boolean.class);
                Log.d(TAG, "orgMethod : " + orgMethod.getName());
                replaceMethod = findMethodHierarchically(AppHooking.class,"my_JsonBooleanPut", Class.class, String.class, boolean.class);
                Log.d(TAG, "replaceMethod : " + replaceMethod.getName());
                HookHelper.hook(orgMethod, replaceMethod);

                Log.d(TAG, "[===] HOOK_JSON_OBJECT_CLASS hooking success...");
                break;

            case HOOK_MAIN_ACTIVITY_LIKE_CLICK:
                Log.d(TAG, "[===] HOOK_MAIN_ACTIVITY_LIKE_CLICK signal input...");
                orgMethod = findMethodHierarchically(clazz, "onClick", View.class);
                replaceMethod = findMethodHierarchically(AppHooking.class,"onLikeClick", Class.class, View.class);
                Log.d(TAG, "[HOOK_MAIN_ACTIVITY_LIKE_CLICK]  org : " + orgMethod.getName() + ", rep : " + replaceMethod.getName());
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_MAIN_ACTIVITY_LIKE_CLICK hooking success...");
                break;

            case HOOK_LIKE_CLICK_TEST:
                Log.d(TAG, "[===] HOOK_LIKE_CLICK_TEST signal input...");
                paramCls1 = getRelationClassForName("com.ss.android.ugc.aweme.feed.model.Aweme");
                orgMethod = findMethodHierarchically(clazz, "a", paramCls1, String.class);
                Log.d(TAG, "[HOOK_LIKE_CLICK_TEST] orgMethod : " + orgMethod.getName());
                replaceMethod = findMethodHierarchically(AppHooking.class, "aFunc1", Class.class, Class.class, String.class);
                Log.d(TAG, "[HOOK_LIKE_CLICK_TEST] replaceMethod : " + replaceMethod.getName());
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[HOOK_LIKE_CLICK_TEST] hook...");
                Log.d(TAG, "[===] HOOK_LIKE_CLICK_TEST hooking success...");
                break;

            case HOOK_TTNET_CHECK:
                Log.d(TAG, "[===] HOOK_TTNET_CHECK signal input...");
                orgMethod = findMethodHierarchically(clazz, "a");
                replaceMethod = findMethodHierarchically(AppHooking.class,"myTTNetCheck", Class.class);
                Log.d(TAG, "[HOOK_TTNET_CHECK]  org : " + orgMethod.getName() + ", rep : " + replaceMethod.getName());
                HookHelper.hook(orgMethod, replaceMethod);
                Log.d(TAG, "[===] HOOK_TTNET_CHECK hooking success...");
                break;

        }
        //super.run();
    }
}
