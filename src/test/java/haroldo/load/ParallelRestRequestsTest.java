package haroldo.load;

import org.junit.jupiter.api.Test;

public class ParallelRestRequestsTest {
    @Test
    public void simpleLoadTest() {
        ParallelRestRequests req = new ParallelRestRequests("localhost:8080/alo");

        req.sendTraffic(10, 100);

    }
}
