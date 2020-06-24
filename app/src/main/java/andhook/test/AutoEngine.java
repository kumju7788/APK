package andhook.test;


import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import java.util.Random;
import java.util.Timer;

import static andhook.test.AutoEngine.STATE.*;

public class AutoEngine implements Runnable {
    private static final String TAG = HookInit.TAG;

    enum STATE {
        PLAY_CHECK_SCREEN_MODE,
        PLAY_ENDING,
        PLAY_SLIDING,
        PLAY_CHANGE_SCREEN_MODE,
            SUB_MENU_TOUCH,
            SUB_SCREEN_MODE_TOUCH,
        PLAY_DUMMY
    }

    //    private long baseTime;
    public static boolean mPause;
    public static long totalTime;
    public static boolean isScreenModeChanged;
    public static int[] posScreenModeIcon = new int[2];
    public static int[] posMainMenuIcon = new int[2];

    private TimeRange playTime;
    private TimeRange waitTime;
    private STATE actionState;
    private STATE actionSubState;

    AutoEngine() {
        mPause = false;
        totalTime = 0;
        isScreenModeChanged = false;
        waitTime = new TimeRange();
        playTime = new TimeRange();
        playTime.setTimeRange(18, 23, TimeRange.UNIT_MINUTE);
        waitTime.setTimeRange(10, 15, TimeRange.UNIT_SECOND);
        actionState = PLAY_CHECK_SCREEN_MODE;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (this) {
                if (!mPause) {
                    if(waitTime.isEventTime())
                        actionTraining(actionState);
                    if(playTime.isEventTime()) {
                        actionTraining(PLAY_ENDING);
                    }
                }
            }
            SystemClock.sleep(100);
        }
    }

    private void actionTraining(STATE state) {

        switch (state) {
            case PLAY_CHECK_SCREEN_MODE:
                Log.d(TAG, "STATE : PLAY_CHECK_SCREEN_MODE");
                synchronized (this) {
                    if(isScreenModeChanged) {
                        actionState = PLAY_SLIDING;
                        waitTime.setTimeRange(18, 23, TimeRange.UNIT_SECOND);
                    }
                    else {
                        actionState = PLAY_CHANGE_SCREEN_MODE;
                        actionSubState = SUB_MENU_TOUCH;
                    }
                }
                break;
            case PLAY_ENDING:
                Log.d(TAG, "STATE : PLAY_ENDING");
                break;
            case PLAY_SLIDING:
                Log.d(TAG, "STATE : PLAY_SLIDING");
                EventAction.swipeUp();
                waitTime.setTimeRange(18, 23, TimeRange.UNIT_SECOND);
                break;
            case PLAY_CHANGE_SCREEN_MODE:
                Log.d(TAG, "STATE : PLAY_CHANGE_SCREEN_MODE");
                switch(actionSubState) {
                    case SUB_MENU_TOUCH: //메인액티비티의 메뉴버튼 터치
                        Log.d(TAG, "STATE : PLAY_CHANGE_SCREEN_MODE --> SUB_MENU_TOUCH");
                        TimeRange.setPauseTime(true);
                        EventAction.tapOnScreen(20, 70);
                        waitTime.setTimeRange(2, 5, TimeRange.UNIT_SECOND);
                        actionSubState = SUB_SCREEN_MODE_TOUCH;
                        break;
                    case SUB_SCREEN_MODE_TOUCH: // 메뉴에서 <大屏模式>(Screen mode)누르기
                        Log.d(TAG, "STATE : PLAY_CHANGE_SCREEN_MODE --> SUB_SCREEN_MODE_TOUCH");
                        synchronized (this) {
                            EventAction.tapOnScreen(20, posScreenModeIcon[1] + 20);
                        }
                        TimeRange.setPauseTime(false);
                        waitTime.setTimeRange(20, 25, TimeRange.UNIT_SECOND);
                        actionState = PLAY_SLIDING;
                        break;
                }
                break;
            case PLAY_DUMMY:
                break;
        }
    }
}
