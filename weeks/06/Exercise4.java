import java.util.stream.*;
import java.util.*;

public class Exercise4 {

    public static class IntSet {
        private final Set<Integer> mySet = new HashSet<Integer>();
        public synchronized void addPerson(int p) {
            mySet.add(p);
        }
        public synchronized boolean containsPerson(Integer p) {
            return mySet.contains(p);
        }
        public synchronized final Set<Integer> getSet() {
            return mySet;
        }
    }

    public static void main (String[] args) {
        IntSet is = new IntSet();
        Thread t1 = new Thread( () -> {
            IntStream.range(0,1000).forEach(i -> is.getSet().add(i));
        });
        Thread t2 = new Thread( () -> {
            IntStream.range(0,1000).forEach(i -> {
                if (!is.getSet().contains(i)) {
                    if (is.getSet().contains(i)) {
                        System.out.println("OOOOOOH");
                    }
                }
            });
        });
        t1.start();
        t2.start();
        
    }
}
