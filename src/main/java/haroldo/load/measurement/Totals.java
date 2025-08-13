package haroldo.load.measurement;

public class Totals {
    private int count = 0;
    private long total = 0;
    private long max = Long.MIN_VALUE;
    private long min = Long.MAX_VALUE;

    public synchronized void add(long value) {
        count++;
        total += value;
        if (max < value)
            max = value;
        if (min > value)
            min = value;
    }

    public void addInitialMs(long intialMs) {
        add(System.currentTimeMillis() - intialMs);
    }

    public double getAverage() {
        if (count == 0)
            return 0;
        return (double) total / count;
    }

    public int getCount() {
        return count;
    }

    public long getTotal() {
        return total;
    }

    public long getMax() {
        if (count == 0)
            return 0;
        return max;
    }

    public long getMin() {
        if (count == 0)
            return 0;
        return min;
    }

    @Override
    public String toString() {
        return "Count: " + getCount() + ", Sum: " + getTotal() + ", Average: " + getAverage() + ", Max: " + getMax() + ", Min: " + getMin();
    }
}
