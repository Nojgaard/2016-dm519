import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Exercise15 {
	/*
	 * - Experiment with different kinds of producer and consumer code.
	 *   Have more than 1 producer
	 *   Make it so a product is a list of (random) numbers that the consumer
	 *   has to sum together.
	 */

	private static class Product {
		private final List<Integer> list;
		public Product (List<Integer> list) {
			this.list = list;
		}

		public Integer sum() {
			return list.stream().mapToInt(Integer::intValue).sum();
		}
	}

	private static final int NUM_THREADS = 4;
	private static final ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);

	private static void consume(Product prod) {
		System.out.println(prod.sum().toString());
	}

	private static void produce () {
		Random r = new Random();
		List<Integer> list = new ArrayList<>();
		int n = r.nextInt(50);
		IntStream.range(0,n).forEach(i -> list.add(r.nextInt(100)));
		
		Product prod = new Product(list);
		exec.submit(() -> consume(prod));
	}
	
	public static void main (String[] args) {
		long start = System.currentTimeMillis();
		IntStream.range(1, 5000).forEach( i -> produce());
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
