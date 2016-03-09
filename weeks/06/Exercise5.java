public class Exercise5 {
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

    private static class Point {
        public Counter c1,c2;
        public Point() {
            c1 = new Counter();
            c2 = new Counter();
        }

        public boolean areEqual() {
            synchronized(c1) {
                synchronized(c2) {
                    return c1.get() == c2.get();
                }
            }
        }
    }

    public static void main (String[] args) {
        
    }
}
