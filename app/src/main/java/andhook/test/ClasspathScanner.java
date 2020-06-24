package andhook.test;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;

import andhook.lib.AndHook;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class ClasspathScanner {
    private static final String TAG = HookInit.TAG;


    public static Class<?> FindClassForName(String className){
        Class<?> entryClass = null;
        ApplicationInfo ai = AppInfo.GetAppInfo();
        String classPath = ai.sourceDir;
        Log.d(TAG, " PathClass :" + classPath);

        DexFile dex = null;

        try {
            PathClassLoader classLoader = (PathClassLoader) Thread.currentThread().getContextClassLoader();
            dex = new DexFile(classPath);
            Enumeration<String> entries = dex.entries();
            while (entries.hasMoreElements()) {
                String entry = entries.nextElement();
                if(entry.equals(className)) {
                    Log.d(TAG, "Entry: " + entry);
                    entryClass = dex.loadClass(entry, classLoader);
                    if (entryClass != null) {
                       // Log.d(TAG, className + " Class found in dex file. ClassName is " + entryClass.getSimpleName());
                    }
                    else
                        Log.d(TAG, className + " not found.");
                    return entryClass;
                }
            }
        } catch (Exception e) {
            // TODO (5): more precise error handling
            Log.e(TAG, "Error", e);
        }
        return null;
    }

    public static void AddClassPath(String clsPath)
    {
        Class<?> entryClass = null;
        try {
            //ApplicationInfo ai = AppInfo.GetAppInfo();
            String classPath = "/data/local/tmp/app-debug.apk";
            DexFile dex = null;
            PathClassLoader classLoader = (PathClassLoader) Thread.currentThread().getContextClassLoader();
            dex = new DexFile(classPath);
            //String slib = classLoader.findLibrary("/data/local/tmp/libAK.so");
            //Log.d(TAG, "slib :" + slib);

            Enumeration<String> entries = dex.entries();
            while (entries.hasMoreElements()) {
                String entry = entries.nextElement();
                Log.d(TAG, "Entry: " + entry);
                entryClass = dex.loadClass(entry, classLoader);
                //Log.d(TAG, "Entry: " + entryClass.getClassLoader().getClass().getName());
            }
            AndHook.ensureNativeLibraryLoaded(null);

            Class<?> pathCls = Class.forName("dalvik.system.DexClassLoader");
            Field[] fids = pathCls.getFields();
            Log.d(TAG, pathCls.getName() + " fields count :" + fids.length);
            for (Field f: fids) {
                Log.d(TAG, "pathclass list :" + f.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
