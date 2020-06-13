package andhook.test;

import andhook.lib.HookHelper;
import andhook.lib.HookHelper.Hook;
import andhook.test.ui.MainActivity;

public final class Constructor {
    private static boolean passed;

    private Constructor() {
        final Object thiz = this; // dummy code to avoid inlining
        MainActivity.output("original constructor hit, this = " + thiz +
                ", hash = " + thiz.hashCode());
    }

    @SuppressWarnings("unused")
    @Hook(clazz = Constructor.class, name = "<init>")
    private static void faked_Constructor(final Object objConstructor) {
        MainActivity.output("faked constructor hit, this = " + objConstructor);
        MainActivity.output(new RuntimeException("test"));
        HookHelper.invokeVoidOrigin(objConstructor);
        passed = true;
    }

    public static void test() {
        MainActivity.clear();
        MainActivity.output("constructor hook test...");

        HookHelper.applyHooks(Constructor.class);

        passed = false;
        new Constructor();

        if (passed)
            MainActivity.info("constructor hook test passed");
        else
            MainActivity.alert("failed to hook constructor!");
    }
}
