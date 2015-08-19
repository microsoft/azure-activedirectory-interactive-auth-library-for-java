package com.microsoftopentechnologies.auth.browser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class BrowserLauncherHelper {
    private static URLClassLoader loader = null;

    public static void launchExternalProcess(
            File appJar,
            String url,
            String redirectUrl,
            String callbackUrl,
            String windowTitle,
            boolean noShell) throws IOException {
        List<String> args = new ArrayList<String>();

        // fetch path to the currently running JVM
        File javaHome = new File(System.getProperty("java.home"));
        File javaExecutable = new File(javaHome, "bin" + File.separator + "java");
        args.add(javaExecutable.getAbsolutePath());

        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        if (isMac) {
            // swt on mac requires this argument in order for the swt dispatch
            // loop to be running on the UI thread
            args.add("-XstartOnFirstThread");
        }
        args.add("-cp");
        args.add(appJar.getAbsolutePath());
        args.add("com.microsoftopentechnologies.adinteractiveauth.Program");
        args.add(url);
        args.add(redirectUrl);
        args.add(callbackUrl);
        args.add(windowTitle);
        // process should exit after sign in is complete
        args.add("true");
        args.add(String.valueOf(noShell));

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.start();
    }

    public static void launchInProcess(
            File appJar,
            String url,
            String redirectUrl,
            String callbackUrl,
            String windowTitle,
            boolean noShell) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // WARNING: Potential race condition here? Maybe we should synchronize initialization of "loader"?
        if (loader == null) {
            loader = new URLClassLoader(new URL[]{
                    new URL("file:///" + appJar.getPath())
            }, BrowserLauncherDefault.class.getClassLoader());
        }

        Class<?> program = loader.loadClass("com.microsoftopentechnologies.adinteractiveauth.Program");
        final Method main = program.getDeclaredMethod("main", String[].class);
        final String[] args = new String[]{
                url, redirectUrl, callbackUrl, windowTitle, "false", String.valueOf(noShell)
        };

        main.invoke(null, (Object) args);
    }
}
