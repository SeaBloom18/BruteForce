package org.ops;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshAuthenticator implements CredentialAuthenticator {

    private final String host;
    private final int port;

    public SshAuthenticator(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean authenticate(Credential cred) {
        Session session = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(cred.user, host, port);
            session.setPassword(cred.password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(3000);
            session.disconnect();
            return true;

        } catch (JSchException e) {
            String msg = e.getMessage();
            if (msg.contains("Auth fail")) {
                System.out.printf("❌ Auth fail for %s:%s%n", cred.user, cred.password);
            } else if (msg.contains("session is down") || msg.contains("Connection refused")) {
                System.out.printf("⚠️ Conexión rechazada SSH para %s:%s → %s%n",
                        cred.user, cred.password, msg);
            } else if (msg.contains("timeout")) {
                System.out.printf("⚠️ Timeout SSH para %s:%s → %s%n",
                        cred.user, cred.password, msg);
            } else {
                System.out.printf("⚠️ Error SSH para %s:%s → %s%n",
                        cred.user, cred.password, msg);
            }
            return false;
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}