import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.LinkedBlockingQueue;

public class Exercise25 {	
	private static final int N_THREADS = 4;
	private static final CounterThreadFactory ctf = new CounterThreadFactory();
	private static ExecutorService executor =
		new TimingThreadPool(N_THREADS,N_THREADS,10,TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(4), ctf,
				new ThreadPoolExecutor.CallerRunsPolicy());
	
	private static class CounterThreadFactory implements ThreadFactory {
		private int counter = 0;
		public Thread newThread(Runnable r) {
			counter++;
			return new Thread(r);
		}
		public int getCounter() {
			return counter;
		}
	}

	private static class TimingThreadPool extends ThreadPoolExecutor {
		private final ThreadLocal<Long> startTime
			= new ThreadLocal<Long>();
		private final AtomicLong numTasks = new AtomicLong(0);
		private final AtomicLong totalTime = new AtomicLong(0);

		public TimingThreadPool(int numCorePool,int numMaxPool,
				long await, TimeUnit unit, BlockingQueue<Runnable> work,
				ThreadFactory factory, RejectedExecutionHandler reh) {
			super(numCorePool,numMaxPool,await,unit,work,factory,reh);
		}

		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			startTime.set(System.nanoTime());
		}

		protected void afterExecute(Runnable r, Throwable t) {
			try {
				long endTime = System.nanoTime();
				long taskTime = endTime - startTime.get();
				numTasks.incrementAndGet();
				totalTime.addAndGet(taskTime);
			} finally {
				super.afterExecute(r, t);
			}
		}

		protected void terminated() {
			try {
				System.out.println(String.format("Terminated: avg time=%dns",
							totalTime.get() / numTasks.get()));
			} finally {
				super.terminated();
			}
		}
	}

	private static AtomicInteger counter = new AtomicInteger( 0 );
	
	public static void wordCount( String line )
	{
		String[] words = line.split( "\\s+" );
		counter.addAndGet( words.length );
	}
	
	public static void main (String[] args) {
		// Read bigtext.txt
		// read the lines
		// give each line to a new thread
		// the thread counts the words in the line
		// a shared int is updated.
		// latch and print
		Path path = Paths.get( "bigtext.txt" );
		try {
			BufferedReader reader = Files.newBufferedReader( path );
			String line;
			while( (line = reader.readLine()) != null ) {
				final String currentLine = line;
				executor.submit(
					() -> wordCount( currentLine )
				);
			}
			executor.shutdown();
			executor.awaitTermination( 1, TimeUnit.DAYS );
			System.out.println( counter.get() );
			
		} catch( IOException e ) {
			e.printStackTrace();
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
		System.out.println(ctf.getCounter());
	}
}
