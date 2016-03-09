import java.util.stream.*;
public class Exercise1
{
    /*
     * - Create a Counter class storing an integer (a field called i), with an increment and decrement method.
     *   - Make Counter Thread-safe.
     *   - Does it make a different to declare i private or public?
     *      */
    private static class Counter {
        private int i = 0;

        public synchronized int get() {
            return i;
        }

        public synchronized void inc() {
            i++;
        }

        public synchronized void dec() {
            i--;
        }
    }

    public static void main (String[] args) {
        Counter c = new Counter();
        Thread t1 = new Thread ( () -> {
            IntStream.range(0,100).forEach(i -> c.inc());
        });
        Thread t2 = new Thread ( () -> {
            IntStream.range(0,100).forEach(i -> c.dec());
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch(Exception e) {
        }
        System.out.println(c.get());
    }
}
