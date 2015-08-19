package com.microsoftopentechnologies.auth.samples.swingapp;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoftopentechnologies.auth.ADJarLoader;
import com.microsoftopentechnologies.auth.browser.BrowserLauncher;
import com.microsoftopentechnologies.auth.browser.BrowserLauncherHelper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public class OutProcBrowserLauncher implements BrowserLauncher {
    @Override
    public ListenableFuture<Void> browseAsync(String url, String redirectUrl, String callbackUrl, String windowTitle, boolean noShell) {
        try {
            // download the browser app jar
            File appJar = ADJarLoader.load();

            // launch the browser out-process
            BrowserLauncherHelper.launchExternalProcess(appJar, url, redirectUrl, callbackUrl, windowTitle, noShell);
            return Futures.immediateFuture(null);
        } catch (ExecutionException e) {
            return Futures.immediateFailedFuture(e);
        } catch (MalformedURLException e) {
            return Futures.immediateFailedFuture(e);
        } catch (IOException e) {
            return Futures.immediateFailedFuture(e);
        }
    }
}
