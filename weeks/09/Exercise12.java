import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Exercise12 {
	/*
	 * - Modify Exercise11 such that the producer uses a fixed thread pool Executor
	 *   to consume the products.
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

	private static final ExecutorService exec = Executors.newFixedThreadPool(10);

	private static void consume(Product prod, String threadName) {
		System.out.println(threadName + " consuming " + prod.toString());
	}

	private static void produce (Integer i) {
		Product prod = new Product("Bottle " + i, "");
		exec.submit(() -> consume(prod, i.toString()));
	}

	public static void main (String[] args) {
		IntStream.range(1, 5000).forEach( i -> produce(i));
		exec.shutdown();
	}
}
