import haroldo.load.ParallelRestRequests;

public class Main {
    public static void main(String args[]) {
        ParallelRestRequests req = new ParallelRestRequests("localhost:8080/alo");

        req.sendTraffic(10, 211);

    }
}
