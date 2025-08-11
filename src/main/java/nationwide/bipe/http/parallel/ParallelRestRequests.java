package nationwide.bipe.http.parallel;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelRestRequests {

    private static final String URL = "http://localhost:8080/alo";
    private static final int NUM_REQUESTS = 100; // Number of parallel requests

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();

        // Create a list of CompletableFutures for parallel requests
        List<CompletableFuture<String>> futures = IntStream.range(0, NUM_REQUESTS)
                .mapToObj(i -> sendRequest(client, i))
                .collect(Collectors.toList());

        // Wait for all requests to complete and print responses
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenAccept(v -> futures.forEach(f -> {
                    try {
                        System.out.println(f.get());
                    } catch (Exception e) {
                        System.err.println("Request failed: " + e.getMessage());
                    }
                }))
                .join();
    }

    private static CompletableFuture<String> sendRequest(HttpClient client, int id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> "Response " + id + ": " + response.body());
    }
}
