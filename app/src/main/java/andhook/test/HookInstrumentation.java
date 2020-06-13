package andhook.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class HookInstrumentation extends Instrumentation {
    private final String TAG = HookInit.TAG;
    private Instrumentation base;

    HookInstrumentation(Instrumentation base) {
        this.base = base;
        Log.d( TAG, "HookInstrumentation constructor.");
    }

    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        super.callActivityOnCreate(activity, icicle);
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        findAllViews(viewGroup);
    }

    public void findAllViews(ViewGroup viewGroup) {
        Log.d( TAG, "===findAllViews====");
        for (int i = 0, n = viewGroup.getChildCount(); i < n; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                findAllViews((ViewGroup) child);
            } else {
                String viewClassName= child.getClass().getName();
                Log.d( TAG, "===============================================================");
                Log.d( TAG, "=[Find View] : " +  viewClassName);
                Log.d( TAG, "===============================================================");
            }
        }
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Log.d("[app]","OK，Hooked....");
        Log.d(TAG,"className="+className+" intent="+intent);
        return super.newActivity(cl, className, intent);
    }

}
