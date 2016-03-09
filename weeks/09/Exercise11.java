import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;

public class Exercise11 {
	/*
	 * - Create a Producer/Consumers program with a single producer thread that produces 5000 items.
	 * - Whenever an item is produceruced, the producer should create and start a consumer
	 *   thread that consumernsumes the product (e.g., print it on screen).
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

	private static void consume(Product prod, String threadName) {
		System.out.println(threadName + " consuming " + prod.toString());
	}

	private static void produce (Integer i) {
		Product prod = new Product("Bottle " + i, "");
		new Thread( () -> {
			consume(prod, i.toString());
		}).start();
	}

	public static void main (String[] args) {
		IntStream.range(1, 5000).forEach( i -> produce(i));
	}
}
