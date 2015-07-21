package com.microsoftopentechnologies.auth.browser;

import com.google.common.util.concurrent.ListenableFuture;

public interface BrowserLauncher {
    ListenableFuture<Void> browseAsync(String url,
                                       String redirectUrl,
                                       String callbackUrl,
                                       String windowTitle,
                                       boolean noShell);
}
