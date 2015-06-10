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
package com.microsoftopentechnologies.aad.adal4j.samples.swingapp;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoftopentechnologies.aad.adal4j.AuthenticationContext;
import com.microsoftopentechnologies.aad.adal4j.AuthenticationResult;
import com.microsoftopentechnologies.aad.adal4j.PromptValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class AADAuthSample extends JFrame {
    private JPanel panelControls;
    private JTextField tenantName;
    private JTextField resource;
    private JTextField clientID;
    private JTextField redirectURI;
    private JButton signIn;
    private JTextArea result;
    private JTextField authority;
    private JComboBox promptValue;

    public AADAuthSample() {
        setContentPane(panelControls);
        promptValue.setModel(new DefaultComboBoxModel(new String[]{
                PromptValue.login, PromptValue.refreshSession, PromptValue.attemptNone}));

        signIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    result.setText("(loading...)");

                    AuthenticationContext context = new AuthenticationContext(authority.getText());
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
        AADAuthSample dialog = new AADAuthSample();
        dialog.setPreferredSize(new Dimension(640, 480));
        dialog.pack();
        dialog.setTitle("Azure Active Directory Interactive Auth Sample");
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