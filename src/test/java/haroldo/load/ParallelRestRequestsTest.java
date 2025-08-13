package haroldo.load;

import org.junit.jupiter.api.Test;

public class ParallelRestRequestsTest {
    @Test
    public void simpleTest() throws InterruptedException {
        ParallelRestRequests requests = new ParallelRestRequests("localhost:8080/hello");
        requests.sendTraffic(10, 1002, 250);
    }
}
