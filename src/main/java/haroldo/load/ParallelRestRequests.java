package haroldo.load;

import haroldo.load.measurement.Totals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ParallelRestRequests {

    private final String url;

    public ParallelRestRequests(String url) {
        this.url = "http://" + url;
    }

    public void sendTraffic(int virtualUsers, int totalRequests, long thinkTimeMs) throws InterruptedException {
        int requestCount = totalRequests / virtualUsers;
        int plusOneIndex = totalRequests % virtualUsers;

        List<Thread> threadList = new ArrayList<>();

        System.out.println("Start calling '" + url + "' " + totalRequests + " with " +
                virtualUsers + " virtual users using " + thinkTimeMs + "ms of think time");
        for (int user = 0; user < virtualUsers; user++) {
            Thread thread = Thread.startVirtualThread(new User(user, requestCount + (user < plusOneIndex ? 1 : 0), thinkTimeMs));
            threadList.add(thread);
        }

        System.out.println("Waiting for all threads to finish");

        for (Thread thread : threadList) {
            thread.join();
        }
        System.out.println("End");
    }

    class User implements Runnable {
        private final int user;
        private final int requests;
        private final long thinkTimeMs;

        User(int user, int requests, long thinkTimeMs) {
            this.user = user;
            this.requests = requests;
            this.thinkTimeMs = thinkTimeMs;
        }

        @Override
        public void run() {
            Totals responseMs = new Totals();
            System.out.println("Starting user " + user + " calling " + requests + " times!");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url)).GET().build();

            for (int i = 0; i < requests; i++) {
                if (user % 10 == 0 && i % 100 == 0)
                    System.out.println("User " + user + " calling " + i);

                try (HttpClient client = HttpClient.newHttpClient()) {
                    long ms = System.currentTimeMillis();
                    client.send(request, HttpResponse.BodyHandlers.ofString());
                    responseMs.addInitialMs(ms);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                sleep(thinkTimeMs);
            }
            System.out.println("Ending user " + user + " - " + responseMs);
        }

        void sleep(long sleepMs) {
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
