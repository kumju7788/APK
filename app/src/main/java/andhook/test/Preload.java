package andhook.test;

public class Preload extends Thread {
    Class<?>  preloadClass;
    @Override
    public void run() {
        preloadClass = ClasspathScanner.FindClassForName("okhttp3.Request");
        AppHooking.appClassTest(preloadClass, 1);
        preloadClass = ClasspathScanner.FindClassForName("j.a.f0.w0");
        preloadClass = ClasspathScanner.FindClassForName("androidx.slidingpanelayout.widget.SlidingPaneLayout");
    }
}