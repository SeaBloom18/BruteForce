package org.ops;

public class BruteForceController {

    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando ataque...");

        BruteForceRunner.runInBatches(BruteForceHttp::tryCredential);
        //BruteForceRunner.runInBatches(BruteForceFtp::tryCredential);
        //BruteForceRunner.runInBatches(BruteForceSsh::tryCredential);

        if (!BruteForceRunner.isFound()) {
            System.out.println("No se encontró ninguna combinación válida.");
        } else {
            System.out.println("Combinación correcta encontrada.");
        }
    }
}
