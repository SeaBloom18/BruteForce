package org.ops;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.*;

public class BruteForceRunner {

    private static final int THREAD_COUNT_PER_BATCH = 5;
    private static final int TOTAL_BATCHES = 5;
    private static final int TIMEOUT_MINUTES = 10;

    private static volatile boolean found = false;

    public static void runInBatches(CredentialAuthenticator authenticator, List<Credential> credentials) {
        found = false; // reset
        int batchSize = credentials.size() / TOTAL_BATCHES;
        ExecutorService batchExecutor = Executors.newFixedThreadPool(TOTAL_BATCHES);

        for (int i = 0; i < TOTAL_BATCHES; i++) {
            int start = i * batchSize;
            int end = (i == TOTAL_BATCHES - 1) ? credentials.size() : (i + 1) * batchSize;
            List<Credential> subList = credentials.subList(start, end);

            batchExecutor.submit(() -> runBatch(authenticator, subList));
        }

        batchExecutor.shutdown();
        try {
            batchExecutor.awaitTermination(TIMEOUT_MINUTES, TimeUnit.MINUTES);
        } catch (InterruptedException ignored) {}
    }

    private static void runBatch(CredentialAuthenticator authenticator, List<Credential> credentials) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT_PER_BATCH);

        for (Credential cred : credentials) {
            if (found) break;
            executor.submit(() -> {
                if (!found && authenticator.authenticate(cred)) {
                    found = true;
                    System.out.printf("✅ FOUND → %s:%s%n", cred.user, cred.password);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(TIMEOUT_MINUTES, TimeUnit.MINUTES);
        } catch (InterruptedException ignored) {}
    }

    public static boolean isFound() {
        return found;
    }

    public static List<Credential> loadCredentialsFromFile(String path) throws Exception {
        InputStream input = new FileInputStream(path);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Credential>>() {}.getType();
        return gson.fromJson(new InputStreamReader(input), listType);
    }
}