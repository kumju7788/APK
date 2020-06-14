package andhook.test;

import android.util.Log;

import java.util.Random;

public class TimeRange {
    private static final String TAG = HookInit.TAG;

    public static final long UNIT_HOUR = 60 * 60 * 1000;
    public static final long UNIT_MINUTE = 60 * 1000;
    public static final long UNIT_SECOND = 1000;
    public static final long UNIT_MILLISECOND = 1;

    static boolean isPause;
    static long pauseTime;
    static long resumeTime;

    int minValue;
    int maxValue;
    long baseTime;
    long unitTime;
    private Random r = new Random();
    long eventTime;

    TimeRange() {
        pauseTime = 0;
        resumeTime = 0;
        isPause = false;
    }

    public void setTimeRange(int min, int max, long unit) {
        minValue = min;
        maxValue = max;
        unitTime = unit;
        baseTime = System.currentTimeMillis();
        refreshEventTime();
    }

    /**
     * 어떤 이벤트동작을 실행한 다음 다음 이벤트가 일어날 시간을
     * 랜덤으로 재설정한다.
     */
    private void refreshEventTime() {
        eventTime = r.nextInt(maxValue - minValue + 1) + minValue;
        Log.d(TAG, "EventTime : " + eventTime);
        eventTime = eventTime * unitTime;
        pauseTime = 0;
        resumeTime = 0;
        isPause = false;
    }

    /**
     * 어떤 이베트동작이 일어날 시간이 되였는가를 검사한다.
     * 화면이 정지되였다든가 혹은 어떤 동작이 수행되는 시간을
     * 더 부가한다.
     * @return true: 시간이 됨. false: 시간이 아직 안됨.
     */
    public boolean isEventTime() {
        long curTime = System.currentTimeMillis();
        if(isPause)
            resumeTime = curTime;
        return curTime - baseTime >= (eventTime + resumeTime - pauseTime);
    }

    /**
     * 화면이 정지되거나 혹은 어떤 동작이 취해질때 다른 동작시간을 현재의 동작이
     * 시행되는 시간동안을 더 타산해야 할때 리용된다.
     * @param pause : true ~ false가 되는 동안의 타임이 더 부가된다.
     */
    public static void setPauseTime(boolean pause) {
        if(pause) {
            isPause = true;
            pauseTime = System.currentTimeMillis();
        }
        else {
            isPause = false;
            resumeTime = System.currentTimeMillis();
            Log.d(TAG, "pausedTime : " + (resumeTime - pauseTime)/1000 + "s");
        }
    }

}
