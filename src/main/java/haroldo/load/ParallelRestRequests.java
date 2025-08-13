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

        System.out.println("Starting virtual users");
        List<List<CompletableFuture<String>>> futuresList = IntStream.range(0, virtualUsers)
                .mapToObj((user) -> userTraffic(user, totalRequests / virtualUsers + (user < plusOneIndex ? 1 : 0)))
                .collect(Collectors.toList());
        System.out.println("Virtual users started");

        int vUser = 1;
        for (var futures : futuresList) {
            System.out.printf("Starting virtual user %d ?\n", vUser++);
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenAccept(v -> futures.forEach(f -> {
                        try {
                            System.out.println(f.get());
                        } catch (Exception e) {
                            System.err.println("Request failed: " + e.getMessage());
                        }
                    })).thenApply(response -> "Virtual user finished");
        }

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (var futures : futuresList) {
            System.out.printf("Waiting for virtual user %d to finish\n", vUser++);
            System.out.println(
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                            .thenAccept(v -> futures.forEach(f -> {
                                try {
                                    System.out.println(f.get());
                                } catch (Exception e) {
                                    System.err.println("Request failed: " + e.getMessage());
                                }
                            })).join());
        }
//                .thenAccept(v -> f.forEach()
//                        .(
//    {
////                .thenAccept(v -> futures.forEach(f -> {
//                    try {
//                        System.out.println(f.get());
//                    } catch (Exception e) {
//                        System.err.println("Request failed: " + e.getMessage());
//                    }
//                }))
//                .join();
    }


    public List<CompletableFuture<String>> userTraffic(int user, int requests) {
//        List<CompletableFuture<String>> futures = IntStream.range(0, requests)
        System.out.printf("User %d sending %d requests\n", user, requests);
        return IntStream.range(0, requests)
                .mapToObj((i) -> sendRequest(i, user))
                .collect(Collectors.toList());

//        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                .thenAccept(v -> futures.forEach(f -> {
//                    try {
//                        System.out.println(f.get());
//                    } catch (Exception e) {
//                        System.err.println("Request failed: " + e.getMessage());
//                    }
//                })).thenApply(response -> "Virtual user " + user + " finished!");
    }


    private CompletableFuture<String> sendRequest(int id, int user) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> "User " + user + " - response id " + id + ": " + response.body());
    }
}
