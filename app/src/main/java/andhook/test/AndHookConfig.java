package andhook.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import andhook.lib.HookHelper;
import andhook.test.ui.MainActivity;

/**
 * @author <a href="mailto:qq2325690622@gmail.com>Deng Chao</a> on 2019/5/23
 */
@SuppressWarnings("unused")
public class AndHookConfig {
    private static final String TAG = AndTest.LOG_TAG;
    public static boolean passed = false;

    /**
     * 메소드 Hook:Activity 의 onCreate (번들) 메소드를 연결
     *
     * @param objActivity        Hook된 activity 오브젝트
     * @param savedInstanceState onCreate(Bundle)의 bundle 파라메터
     * @since v3.6.2
     */
    @HookHelper.Hook(clazz = Activity.class, name = "onCreate")// 指定 Hook 的类与方法 ;후크 클래스 및 메소드 지정
    private static void Activity_onCreate(final Object objActivity, final Bundle savedInstanceState) {
        Log.i(AndTest.LOG_TAG, "HookedActivity::onCreate start, this is " + objActivity.getClass());
        HookHelper.invokeVoidOrigin(objActivity, savedInstanceState);
        Log.i(AndTest.LOG_TAG, "HookedActivity::onCreate end");
        passed = true;
    }

    /**
     * 현재 메소드의 메소드이름으로 후크
     * <p>
     * 첫 번째 매개 변수는 후크 유형을 사용할수 있다
     *
     * @since v3.6.2
     */
    @HookHelper.Hook(clazz = Activity.class)
    private static void onStart(Activity activity) {
        Log.d(TAG, "onStart: HookedActivity::onStart start, this is " + activity.getClass());
        HookHelper.invokeVoidOrigin(activity);
        Log.d(TAG, "onStart: HookedActivity::onStart end, this is " + activity.getClass());
    }

    /**
     * 정확한 메소드명으로 구성해야 한다.
     * <p>
     * 잘못된 메소드 서명으로 구성 할 때 오유로그가 나타납니다.
     * AndHook: failed to find method onResume of class android.app.Activity
     *
     * @since v3.6.2
     */
    @HookHelper.Hook(clazz = Activity.class)
    private static void onResume(Object activity, Bundle bundle) {
        Log.d(TAG, "onResume: HookedActivity::onResume start, this is " + activity.getClass() + " with bundle " + bundle);
        HookHelper.invokeVoidOrigin(activity);
        Log.d(TAG, "onResume: HookedActivity::onResume end, this is " + activity.getClass());
    }

    /**
     * 매개 변수 수정
     */
    @HookHelper.Hook(clazz = MainActivity.class)
    private static void logTheNumber(MainActivity activity, int number) {
        HookHelper.invokeVoidOrigin(activity, 2);
    }

    /**
     * static method를 사용하여 구성해야합니다.
     * <p>
     * non-static method 사용하는 경우 오류 로그
     * AndHook: method andhook.test.AndHookConfig@onResume must be static and its first argument must be Class<?> or Object!
     *
     * @since v3.6.2
     */
    @HookHelper.Hook(clazz = Activity.class, name = "onResume")
    private void nonStaticConfig(Object activity) {
        Log.d(TAG, "nonStaticConfig: This is a actually not called");
        HookHelper.invokeVoidOrigin(activity);
    }

}
