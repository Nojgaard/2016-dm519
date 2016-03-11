import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Exercise21 {	
	/*
	 * ! (Exercises marked with ! are harder than usual)
	 *
	 * 				- Make it so the starting run() method of the letter counter simply needs to invoke a static method,
	 * 				  e.g., "count" to start the letter counting of an array of statictrings.
	 * 				- Method count should check if the array is bigger than SLICE_SIZESIZE (defined as a static constant);
	 * 				  if so, it should split the arrayy in two and invoke count on these
	 * 				  two slices in parallel (using an 	executor).
	 * 				- When the two sub-computations are done, collect the resultts.
	 * 				- At the end, output the total count of words.
	 */
	private static int wordCount( String str, int start, int end ) {
		// The right total number is: 14801631
		if ( end - start <= 0 ) {
			return 0;
		}
		
		boolean wasWhitespace =true;

		int counter = 0;

		for( int i = start; i < end; i++ ) {
			if ( Character.isWhitespace( str.charAt( i ) ) && !wasWhitespace ) {
				counter++;
				wasWhitespace = true;
			} else if ( !Character.isWhitespace( str.charAt( i ) ) ) {
				wasWhitespace = false;
			}
		}
		if (!wasWhitespace) counter++;
		
		return counter;
	}

	private static final int SLICE_SIZE = 2;
	// private static ExecutorService exec = Executors.newFixedThreadPool(2);
	private static ExecutorService exec = Executors.newCachedThreadPool();

	private static Integer count(String[] str) {
		int words = count(str, 0, str.length);
		exec.shutdown();
		return words;
	}

	private static Integer count (String[] strs, Integer start, Integer end) {
		int slice = end-start;
		int words = 0;
		if (slice <= SLICE_SIZE) {
			for (int i = start; i < end; i++) {
				words += wordCount(strs[i], 0, strs[i].length());
			}
		} else {
			Future<Integer> f1 = exec.submit(()->{return count(strs,start,start+(slice/2));});
			Future<Integer> f2 = exec.submit(()->{return count(strs,start+(slice/2),end);});
			try {
				words = f1.get() + f2.get();
			} catch (Exception e) {}
		}
		return words;
	}

	public static void main (String[] args) {
		// Contains bugs!
		try {
			String[] str = {
				"This is line 1", "This is line 2", "This is line 3",
				"This is line 4", "This is line 5", "This is line 6",
				"This is line 7", "This is line 8", "This is line 9"};

			doThat( "Multithread one slice", () -> System.out.println(count(str)));
		} catch (Exception e) {}
	}
	
	private static void doThat( String caption, Runnable runnable )
	{
		long tStart = System.currentTimeMillis();
		runnable.run();
		System.out.println( caption + " took " + (System.currentTimeMillis() - tStart) + "ms" );
	}
}
