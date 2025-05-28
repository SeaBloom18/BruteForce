package org.ops;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.*;

public class BruteForceRunner {

    private static final int THREAD_COUNT_PER_BATCH = 8;
    private static final int TOTAL_BATCHES = 5;
    private static final int TIMEOUT_MINUTES = 60;

    private static volatile boolean found = false;

    public static void runInBatches(BruteForceTask task) throws Exception {
        List<Credential> credentials = loadCredentials("credentials.json");
        int batchSize = credentials.size() / TOTAL_BATCHES;
        ExecutorService batchExecutor = Executors.newFixedThreadPool(TOTAL_BATCHES);

        for (int i = 0; i < TOTAL_BATCHES; i++) {
            int start = i * batchSize;
            int end = (i == TOTAL_BATCHES - 1) ? credentials.size() : (i + 1) * batchSize;
            List<Credential> sublist = credentials.subList(start, end);

            batchExecutor.submit(() -> runBatch(task, sublist));
        }

        batchExecutor.shutdown();
        batchExecutor.awaitTermination(TIMEOUT_MINUTES, TimeUnit.MINUTES);

        if (!found) {
            System.out.println("❌ No valid credentials found.");
        }
    }

    private static void runBatch(BruteForceTask task, List<Credential> credentials) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT_PER_BATCH);

        for (Credential cred : credentials) {
            if (found) break;
            executor.submit(() -> {
                if (!found) task.tryCredential(cred);
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

    public static void markFound() {
        found = true;
    }

    private static List<Credential> loadCredentials(String resourceName) throws Exception {
        InputStream input = BruteForceRunner.class.getClassLoader().getResourceAsStream(resourceName);
        if (input == null) throw new RuntimeException("❌ No se encontró '" + resourceName + "' en resources");

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Credential>>() {}.getType();
        return gson.fromJson(new InputStreamReader(input), listType);
    }

}
