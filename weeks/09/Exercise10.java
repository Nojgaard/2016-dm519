import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;

public class Exercise10 {
/*
- Modify producer_consumer/BlockingQueue such that each consumer is notified
  of when the program terminated.
- Hint: 		Use a special class PoisonPill that extends Product and check if
  the product taken from the queue in the consumer is a PoisonPill.
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

	private static class PoisonPill extends Product {
		public PoisonPill() {
			super("","");
		}
	}

	private static final BlockingDeque<Product> THE_LIST= new LinkedBlockingDeque<>();
	private static final int NUM_PRODUCERS = 3;

	private static void produce( BlockingDeque< Product > list, String threadName ) {
		IntStream.range( 1, 10 ).forEach( i -> {
			Product prod = new Product( "Water Bottle", "Liters: " + i + ". By thread: " + threadName );
			list.add( prod );
			System.out.println( threadName + " producing " + prod );
		} );
	}

	private static void consume (BlockingDeque<Product> list, String threadName, CountDownLatch latch) {
		boolean keepRun = true;
		while (keepRun) {
			try {
				Product prod = list.takeFirst();
				if (prod instanceof PoisonPill) {
					System.out.println("Ate poison pill");
					keepRun = false;
				} else {
					System.out.println(threadName + " consuming " + prod.toString());
				}
			} catch(InterruptedException e) {}
			if (latch.getCount() == 0) {
				keepRun = false;
			}
		}
	}

	public static void main (String[] args) {
		CountDownLatch latch = new CountDownLatch( NUM_PRODUCERS );
		IntStream.range( 0,NUM_PRODUCERS ).forEach(
				i -> {
					new Thread( () -> {
						produce(THE_LIST, "Producer" + i );
						latch.countDown();
					} ).start();
					new Thread( () -> {
						consume( THE_LIST, "Consumer" + i, latch );
					}).start();
				} );
		try {
			latch.await();
		} catch (InterruptedException e) {}
		for (int i = 0; i < NUM_PRODUCERS; i++) {
			THE_LIST.add(new PoisonPill());
		}
	}
}
