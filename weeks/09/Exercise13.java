import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Exercise13 {
	/*
	 * - Modify Exercise12 such that the main thread waits for the publicroducer
	 *     and the executor to terminate (in this order).
	 *     - Measure thread time it takes from when the producer starts producing to the
	 *       terminatenation of both the producer and the consumers.
	 */
	private static class Product {
		private final String name;
		private final String attr;
		public Product(String name, String attr) {
			this.name = name;
			this.attr = attr;
		}

		public String toString() {
			return name + ". " + attr;
		}
	}

	private static final int NUM_THREADS = 10;
	private static final ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);

	private static void consume(Product prod, String threadName) {
		System.out.println(threadName + " consuming " + prod.toString());
	}

	private static void produce (Integer i) {
		Product prod = new Product("Bottle " + i, "");
		exec.submit(() -> consume(prod, i.toString()));
	}

	public static void main (String[] args) {
		long start = System.currentTimeMillis();
		IntStream.range(1, 5000).forEach( i -> produce(i));
		long endProducer = System.currentTimeMillis();
		exec.shutdown();
		try {
			exec.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}
		long endConsumer = System.currentTimeMillis();
		System.out.println("Producer time: " + (endProducer - start));
		System.out.println("Consumer time: " + (endConsumer - start));
		System.out.println("Diff: " + ((endConsumer-start)-(endProducer-start)));
	}
}
