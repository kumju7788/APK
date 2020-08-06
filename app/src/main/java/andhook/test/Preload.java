package andhook.test;

public class Preload extends Thread {
    Class<?>  preloadClass;
    @Override
    public void run() {
        ClasspathScanner.setPreloadClasses();
        ClasspathScanner.PreLoadClass();
    }
}