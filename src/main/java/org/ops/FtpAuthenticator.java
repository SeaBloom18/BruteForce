package org.ops;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

public class FtpAuthenticator implements CredentialAuthenticator {

    private final String host;
    private final int port;

    public FtpAuthenticator(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean authenticate(Credential cred) {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(host, port);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                System.out.printf("⚠️ Conexión rechazada [%d] para %s:%s%n",
                        reply, cred.user, cred.password);
                return false;
            }

            ftp.enterLocalPassiveMode();

            boolean success = ftp.login(cred.user, cred.password);
            if (success) {
                ftp.logout();
                return true;
            } else {
                int replyLogin = ftp.getReplyCode();
                System.out.printf("❌ Login failed [%d] for %s:%s%n",
                        replyLogin, cred.user, cred.password);
                return false;
            }

        } catch (IOException e) {
            System.out.printf("⚠️ Error de conexión para %s:%s → %s%n",
                    cred.user, cred.password, e.getMessage());
            return false;
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
    }
}