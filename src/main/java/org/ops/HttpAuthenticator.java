package org.ops;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.util.TimeValue;

public class HttpAuthenticator implements CredentialAuthenticator {

    private final String host;
    private final int port;
    private final String path;

    public HttpAuthenticator(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;
    }

    @Override
    public boolean authenticate(Credential cred) {
        String targetUrl = "http://" + host + ":" + port + path;

        var credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(host, port),
                new UsernamePasswordCredentials(cred.user, cred.password.toCharArray())
        );

        var connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(20);

        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultCredentialsProvider(credsProvider)
                .setRetryStrategy(new DefaultHttpRequestRetryStrategy(0, TimeValue.ZERO_MILLISECONDS))
                .build();

        HttpGet request = new HttpGet(targetUrl);

        try (ClassicHttpResponse response = client.executeOpen(null, request, null)) {
            int statusCode = response.getCode();

            if (statusCode == HttpStatus.SC_OK) {
                // 200 OK
                return true;
            } else {
                System.out.printf("HTTP %d for %s:%s%n", statusCode, cred.user, cred.password);
                return false;
            }
        } catch (Exception e) {
            System.out.printf("⚠️ HTTP error for %s:%s → %s%n", cred.user, cred.password, e.getMessage());
            return false;
        }
    }
}
