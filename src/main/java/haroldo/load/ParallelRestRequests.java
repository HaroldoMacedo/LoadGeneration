package haroldo.load;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelRestRequests {

    private final String url;

    public ParallelRestRequests(String url) {
        this.url = "http://" + url;
    }

    public void sendTraffic(int virtualUsers, int totalRequests) {
        int plusOneIndex = totalRequests % virtualUsers;

        List<CompletableFuture<String>> futures = IntStream.range(0, virtualUsers)
                .mapToObj((user) -> userTraffic(user, totalRequests / virtualUsers + (user < plusOneIndex ? 1 : 0)))
                .collect(Collectors.toList());

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


    public CompletableFuture<String> userTraffic(int user, int requests) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<CompletableFuture<String>> futures = IntStream.range(0, requests)
                .mapToObj((i) -> sendRequest(user))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenAccept(v -> futures.forEach(f -> {
                    try {
                        System.out.println(f.get());
                    } catch (Exception e) {
                        System.err.println("Request failed: " + e.getMessage());
                    }
                }))
                .join();

        return new CompletableFuture<>();
    }


    private CompletableFuture<String> sendRequest(int id) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> "Response " + id + ": " + response.body());
    }
}
