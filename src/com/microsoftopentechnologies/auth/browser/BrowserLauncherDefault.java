/**
 * Copyright 2014 Microsoft Open Technologies Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microsoftopentechnologies.auth.browser;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoftopentechnologies.auth.ADJarLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class BrowserLauncherDefault implements BrowserLauncher {
    private static URLClassLoader loader = null;
    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    @Override
    public ListenableFuture<Void> browseAsync(String url,
                                              String redirectUrl,
                                              String callbackUrl,
                                              String windowTitle,
                                              boolean noShell) {
        SettableFuture<Void> future = SettableFuture.create();
        executorService.submit(new LauncherTask(future, url, redirectUrl, callbackUrl, windowTitle, noShell));
        return future;
    }

    private class LauncherTask implements Callable<Void> {
        SettableFuture<Void> future;
        private final String url;
        private final String redirectUrl;
        private final String callbackUrl;
        private final String windowTitle;
        private final boolean noShell;

        public LauncherTask(SettableFuture<Void> future, String url, String redirectUrl, String callbackUrl, String windowTitle, boolean noShell) {
            this.future = future;
            this.url = url;
            this.redirectUrl = redirectUrl;
            this.callbackUrl = callbackUrl;
            this.windowTitle = windowTitle;
            this.noShell = noShell;
        }

        @Override
        public Void call() {
            try {
                // download the browser app jar
                File appJar = ADJarLoader.load();

                // popup auth UI
                launch(appJar);

                future.set(null);
            } catch (ExecutionException e) {
                reportError(e);
            } catch (MalformedURLException e) {
                reportError(e);
            } catch (ClassNotFoundException e) {
                reportError(e);
            } catch (NoSuchMethodException e) {
                reportError(e);
            } catch (InvocationTargetException e) {
                reportError(e);
            } catch (IllegalAccessException e) {
                reportError(e);
            } catch (IOException e) {
                reportError(e);
            } catch (InterruptedException e) {
                reportError(e);
            }

            return null;
        }

        private void launch(File appJar) throws NoSuchMethodException, InterruptedException, IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
            String osName = System.getProperty("os.name").toLowerCase();
            boolean isMac = osName.contains("mac");
            boolean isLinux = osName.contains("linux");

            // Launch the browser in-proc on Windows and out-proc on Mac and Linux.
            // We do this because:
            //  [1] On OSX, in order for SWT to work the -XstartOnFirstThread option must
            //      have been passed to the VM which unfortunately is not passed to IJ/AS
            //      when it is invoked. So we launch a new instance of the JVM with this
            //      option set.
            //  [2] On Linux, right now, launching SWT in-proc seems to cause intermittent
            //      crashes. So till we know why that's happening (will be fixed in a future
            //      SWT release perhaps?) we'll launch out-proc which appears to work well.
            //
            // You might wonder why we don't just launch out-proc on Windows as well. Turns
            // out, caching of AD auth session cookies are per-process. This means that
            // subsequent runs of the auth flow in the browser in the same process will
            // cause it to reuse session cookies which saves the user from having to enter
            // credentials over and over again.
            if (isMac || isLinux) {
                BrowserLauncherHelper.launchExternalProcess(appJar, url, redirectUrl, callbackUrl, windowTitle, noShell);
            } else {
                BrowserLauncherHelper.launchInProcess(appJar, url, redirectUrl, callbackUrl, windowTitle, noShell);
            }
        }

        private void reportError(Throwable err) {
            future.setException(err);
        }
    }
}