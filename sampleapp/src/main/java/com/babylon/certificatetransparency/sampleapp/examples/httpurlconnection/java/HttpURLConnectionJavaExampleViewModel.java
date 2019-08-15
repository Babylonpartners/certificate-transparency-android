/*
 * Copyright 2019 Babylon Partners Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.babylon.certificatetransparency.sampleapp.examples.httpurlconnection.java;

import android.content.Context;

import com.babylon.certificatetransparency.CTHostnameVerifierBuilder;
import com.babylon.certificatetransparency.CTLogger;
import com.babylon.certificatetransparency.cache.AndroidDiskCache;
import com.babylon.certificatetransparency.sampleapp.examples.BaseExampleViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class HttpURLConnectionJavaExampleViewModel extends BaseExampleViewModel {

    public HttpURLConnectionJavaExampleViewModel(@NotNull Context context) {
        super(context);
    }

    @NotNull
    @Override
    public String getSampleCodeTemplate() {
        return "httpurlconnection-java.txt";
    }

    private void enableCertificateTransparencyChecks(
            HttpURLConnection connection,
            Set<String> hosts,
            boolean isFailOnError,
            CTLogger defaultLogger
    ) {
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;

            // Create a hostname verifier wrapping the original
            CTHostnameVerifierBuilder builder = new CTHostnameVerifierBuilder(httpsConnection.getHostnameVerifier())
                    .setFailOnError(isFailOnError)
                    .setLogger(defaultLogger)
                    .setDiskCache(new AndroidDiskCache(getApplication()));

            for (String host : hosts) {
                builder.includeHost(host);
            }

            httpsConnection.setHostnameVerifier(builder.build());
        }
    }

    @Override
    public void openConnection(@NotNull String connectionHost, @NotNull Set<String> hosts, boolean isFailOnError, @NotNull CTLogger defaultLogger) {
        // Quick and dirty way to push the network call onto a background thread, don't do this is a real app
        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://" + connectionHost).openConnection();
                //noinspection unchecked
                enableCertificateTransparencyChecks(connection, hosts, isFailOnError, defaultLogger);

                connection.connect();
            } catch (IOException e) {
                sendException(e);
            }
        }).start();
    }
}
