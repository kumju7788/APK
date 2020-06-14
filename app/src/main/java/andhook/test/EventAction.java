package andhook.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Random;

public class EventAction {
    private static final String TAG = HookInit.TAG;

    static void swipeUp() {
        BaseOperation.fling(200, 200, 500, 50, 10);
    }

    static void swipeDown() {
        BaseOperation.fling(200, 200, 100, 500, 10);
    }

    static void swipeRight() {
        BaseOperation.fling(50, 400, 200, 200, 10);
    }

    static void swipeLeft() {
        BaseOperation.fling(400, 40, 200, 200, 10);
    }

    static ArrayList<View> findChildrenWithId(View v, String strId) {
        ArrayList<View> chVArray = new ArrayList<View>();
        BaseOperation.findChildren(v, chVArray);
        ArrayList<View> chVArrayWithId = new ArrayList<View>();

        for (View chV : chVArray) {
            if (Integer.toString(chV.getId()).equals(strId)) {
                chVArrayWithId.add(chV);
            }
        }

        return chVArrayWithId;
    }

    static ArrayList<View> findChildrenWithOnClickListener(View v) {
        ArrayList<View> chVArray = new ArrayList<View>();
        BaseOperation.findChildren(v, chVArray);
        ArrayList<View> chVArrayWithOnClickListener = new ArrayList<View>();

        Log.d(TAG, "*** View Count: " + Integer.toString(chVArray.size()));

        for (View chV : chVArray) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                if (chV.hasOnClickListeners()) {
                if (chV instanceof Button) {
                    Log.d(TAG, "*** Has ClickListener " + chV.toString() + " ID is: " + Integer.toString(chV.getId()));
                    chVArrayWithOnClickListener.add(chV);
                }
            }
        }
        return chVArrayWithOnClickListener;
    }

    static void tapOnScreen(float x, float y) {
        BaseOperation.touching(x, y);
    }

    static int getScrHeight() {
        return BaseOperation.getScreenHeight();
    }

    static int getScrWidth() {
        return BaseOperation.getScreenWidth();
    }

    static int[] getViewLocation(View tv) {
        return BaseOperation.getViewLocationOnScreen(tv);
    }

    static void actionClick(Activity activity, int vID) {
        @SuppressLint("ResourceType") View v = activity.findViewById(vID);
        int[] viewLocation = getViewLocation(v);
        tapOnScreen(viewLocation[0], viewLocation[1]);
    }

    static void setText(Activity activity, int vID, String str) {
        @SuppressLint("ResourceType") EditText editText = activity.findViewById(vID);
        editText.setText(str);
    }

    static void moveAppToBackFromActivity(Activity topActivity) {
        BaseOperation.moveAppToBackFromActivity(topActivity);
    }

    static void bringAppToForeground(Activity topActivity) {
        BaseOperation.bringAppToForeground(topActivity);
    }

    /**
     * Pushed by Jin Weiyi
     * Base class for Operation
     */
    private static class BaseOperation {
        /**
         * Pushed by Jin Weiyi
         * Get all views in a parent view
         * Get rid of "if" state to see expected result
         * Or pass second argument to find a view with onClickListener
         */
        static void findChildren(View v, ArrayList<View> chVArray) {
            ViewGroup viewGroup = (ViewGroup)v;
            for (int i=0; i<viewGroup.getChildCount(); i++) {
                View chV = viewGroup.getChildAt(i);
                if (chV instanceof ViewGroup) {
                    findChildren(chV, chVArray);
                    //Log.d(TAG, "*** Group View " + chV.toString() + " ID is: " + Integer.toString(chV.getId()));
                } else {
                    //String viewClassName = chV.getClass().getName();
                    //Log.d(TAG, "*** Single View " + viewClassName + " ID is: " + Integer.toString(chV.getId()));
                }
                chVArray.add(chV);
            }
        }

        static float getRandom(int min, int max) {
            Random r = new Random();
            int rnd = r.nextInt(max - min + 1) + min;
            double rndDouble = r.nextDouble();
            return (float) (rnd + rndDouble);
        }
        /**
         * Pushed by Jin Weiyi
         * Trigger Swipe event
         * Must be running in a thread
         * Change params to see different swipe feature (left-right, top-bottom and vise versa)
         */
        static void fling (float fromX, float toX, float fromY, float toY, int stepCount) {
            Instrumentation inst = new Instrumentation();

            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis();

            float y = getRandom((int)fromY-50, (int)fromY+50);
            float x = getRandom((int)fromX-50, (int)fromX+100);

            float yStep = (getRandom((int)toY-5, (int)toY+5) - y) / stepCount;
            float xStep = (getRandom((int)toX-10, (int)toX+10) - x) / stepCount;

            MotionEvent event = MotionEvent.obtain(
                    downTime, eventTime,
                    MotionEvent.ACTION_DOWN, x, y, 0
            );

            event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
            inst.sendPointerSync(event);
            event.recycle();

            for (int i=0; i<stepCount; i++) {
                y += yStep;
                x += xStep;
                eventTime = SystemClock.uptimeMillis();
                event = MotionEvent.obtain(
                        downTime, eventTime + stepCount,
                        MotionEvent.ACTION_MOVE, x, y, 0
                );
                event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
                inst.sendPointerSync(event);
                event.recycle();
            }

            eventTime = SystemClock.uptimeMillis() + (long)stepCount + 2;
            event = MotionEvent.obtain(
                    downTime, eventTime,
                    MotionEvent.ACTION_UP, x, y, 0
            );

            event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
            inst.sendPointerSync(event);
            event.recycle();
            try {
                Thread.sleep((int)getRandom(100, 150));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * Pushed by Jin Weiyi
         * Trigger Touch event
         * Must be running in a thread
         */
        static void touching (float tX, float tY) {

            Instrumentation inst = new Instrumentation();

            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis();

            float y = getRandom((int)tY-20, (int)tY+20);
            float x = getRandom((int)tX-20, (int)tX+20);
            Log.d(TAG, "x=" + x + ", y=" + y);

            MotionEvent event = MotionEvent.obtain(
                    downTime, eventTime,
                    MotionEvent.ACTION_DOWN, x, y, 0
            );

            event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
            inst.sendPointerSync(event);
            event.recycle();
            Log.d(TAG, "MotionEvent Down");

            try {
                Thread.sleep((int)getRandom(200, 350));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            downTime = SystemClock.uptimeMillis();
            eventTime = SystemClock.uptimeMillis();
            Log.d(TAG, "MotionEvent Up - 1");

            event = MotionEvent.obtain(
                    downTime, eventTime,
                    MotionEvent.ACTION_UP, x, y, 0
            );

            event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
            inst.sendPointerSync(event);
            Log.d(TAG, "MotionEvent Up - 2");
            event.recycle();
        }

        /**
         * Pushed by Jin Weiyi
         * Get Screen width and height in pixels
         */
        static int getScreenWidth() {
            return Resources.getSystem().getDisplayMetrics().widthPixels;
        }

        static int getScreenHeight() {
            return Resources.getSystem().getDisplayMetrics().heightPixels;
        }

        /**
         * Pushed by Jin Weiyi
         * Trigger Key event
         * Must be running in a thread
         * Change params to see different key feature
         */
        static void triggerKey(int keycode) {
            Instrumentation inst = new Instrumentation();
            inst.sendKeyDownUpSync(keycode);
        }

        /**
         * Pushed by Jin Weiyi
         * Get absolute position in x, y coordinate on screen
         */
        public static int[] getViewLocationOnScreen(View v) {
            int[] viewLocation = new int[2];
            v.getLocationOnScreen(viewLocation);
            return viewLocation;
        }

        /**
         * Pushed by Jin Weiyi
         * Put running activity to background
         */

        static void moveAppToBackFromActivity(Activity activity) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            activity.startActivity(intent);
        }

        /**
         * Pushed by Jin Weiyi
         * Bring app to foreground from background
         */
        static void bringAppToForeground(Activity activity) {
            PackageManager packageManager = activity.getApplicationContext().getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage("com.ss.android.ugc.aweme");
            if (intent != null) {
                intent.setPackage(null);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                activity.getApplicationContext().startActivity(intent);
            }
//            ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
//            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(3);
//            for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
//                if (activity.getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
//                    activityManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
//                }
//            }
        }
    }
}
