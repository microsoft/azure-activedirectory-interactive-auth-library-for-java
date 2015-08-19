package com.microsoftopentechnologies.auth.samples.swingapp;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoftopentechnologies.auth.AuthenticationContext;
import com.microsoftopentechnologies.auth.AuthenticationResult;
import com.microsoftopentechnologies.auth.PromptValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class AADAuthSampleWithCustomBrowserLauncher extends JFrame {
    private JPanel panelControls;
    private JTextField tenantName;
    private JTextField resource;
    private JTextField clientID;
    private JTextField redirectURI;
    private JButton signIn;
    private JTextField authority;
    private JComboBox promptValue;
    private JTextArea result;

    public AADAuthSampleWithCustomBrowserLauncher() {
        setContentPane(panelControls);
        promptValue.setModel(new DefaultComboBoxModel(new String[]{
                PromptValue.login, PromptValue.refreshSession, PromptValue.attemptNone}));

        authority.setText("login.windows.net");
        tenantName.setText("common");
        resource.setText("https://management.core.windows.net/");
        clientID.setText("61d65f5a-6e3b-468b-af73-a033f5098c5c");
        redirectURI.setText("https://msopentech.com/");

        signIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    result.setText("(loading...)");

                    AuthenticationContext context = new AuthenticationContext(authority.getText());

                    // configure auth context with a custom browser launcher
                    context.setBrowserLauncher(new OutProcBrowserLauncher());

                    ListenableFuture<AuthenticationResult> future = context.acquireTokenInteractiveAsync(
                            tenantName.getText(),
                            resource.getText(),
                            clientID.getText(),
                            redirectURI.getText(),
                            (String) promptValue.getSelectedItem());

                    Futures.addCallback(future, new FutureCallback<AuthenticationResult>() {
                        @Override
                        public void onSuccess(AuthenticationResult authenticationResult) {
                            String resultText = "Authentication cancelled.";

                            if (authenticationResult != null) {
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                resultText = gson.toJson(authenticationResult);
                            }

                            result.setText(resultText);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            showError(t);
                            result.setText("ERROR: " + t.toString());
                        }
                    });
                } catch (IOException e1) {
                    showError(e1);
                }
            }
        });
    }

    private void showError(Throwable error) {
        JOptionPane.showMessageDialog(this, error.toString(), "Whoops!", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        AADAuthSampleWithCustomBrowserLauncher dialog = new AADAuthSampleWithCustomBrowserLauncher();
        dialog.setPreferredSize(new Dimension(640, 480));
        dialog.pack();
        dialog.setTitle("Azure Active Directory Interactive Auth Sample - Custom Browser Launcher");
        dialog.setVisible(true);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
    }
}
