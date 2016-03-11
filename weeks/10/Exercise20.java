import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Exercise20
{	
	/*
	 * - Modify Exercise20 to use an Executor instead of manual Threads.
	 */

	private static HashMap<Character,Integer> letterCount( String str, char ch, int start, int end )
	{
		HashMap<Character,Integer> map = new HashMap<>();
		if ( end - start <= 0 ) {
			return map;
		}
		
		int counter = 0;
		
		for( int i = start; i < end; i++ ) {
			char c = str.charAt(i);
			if (map.get(c) == null) {
				map.put(c, 1);
			} else {
				map.put(c, map.get(c)+1);
			}
		}
		return map;
	}

	private static void printMap(HashMap<Character,Integer> map) {
		SortedSet<Character> keys = new TreeSet<Character>(map.keySet());
		System.out.println("Printint new map!");
		for (Character c: keys) {
			System.out.print(c.toString() + ": " + map.get(c).toString() + ", ");
		}
		System.out.println();
	}

	private static void addMap(HashMap<Character,Integer> dmap, HashMap<Character,Integer> smap) {
		for (Character c : smap.keySet()) {
			synchronized(dmap) {
				if (dmap.get(c) == null) {
					dmap.put(c,smap.get(c));
				} else {
					dmap.put(c,dmap.get(c) + smap.get(c));
				}
			}
		}
	}
	
	private static int wordCount( String str, int start, int end )
	{
		// The right total number is: 14801631
		if ( end - start <= 0 ) {
			return 0;
		}
		
		boolean wasWhitespace = true;

		int counter = 0;

		for( int i = start; i < end; i++ ) {
			if ( Character.isWhitespace( str.charAt( i ) ) && !wasWhitespace ) {
				counter++;
				wasWhitespace = true;
			} else if ( !Character.isWhitespace( str.charAt( i ) ) ) {
				wasWhitespace = false;
			}
		}
		
		return counter;
	}

	public static void main (String[] args) {
		// Contains bugs!
		try {
			File file = new File( "bigtext.txt" );
			// File file = new File( "test.txt" );
			FileInputStream is = new FileInputStream( file );
			final int size = (int)file.length();
			byte[] content = new byte[ size ];
			is.read( content );
			final String str = new String( content );
			doThat( "Sequential one slice", () -> printMap(letterCount( str, 'e', 0, size ) ) );
			doThat( "Multithreaded N slices", () -> {
				final int numCores = 5;
				ExecutorService exec = Executors.newFixedThreadPool(numCores);
				HashMap<Character,Integer> map = new HashMap<>();
			
				for( int i = 0; i < numCores; i++ ) {
					final int threadIndex = i;
					if ( i != numCores - 1 ) {
						exec.submit( () -> {
							addMap(map,
								letterCount(
									str, 'e', size/numCores * threadIndex,
									((size/numCores)*(threadIndex + 1)) ) );
						} );
					} else {
						exec.submit( () -> {
							addMap(map,
								letterCount(
									str, 'e', size/numCores * threadIndex, size ) );
						} );
					}
					
				}
				exec.shutdown();
				try {
					exec.awaitTermination(60,TimeUnit.SECONDS);
				} catch( InterruptedException e ) {}
				printMap(map);
			} );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private static void doThat( String caption, Runnable runnable )
	{
		long tStart = System.currentTimeMillis();
		runnable.run();
		System.out.println( caption + " took " + (System.currentTimeMillis() - tStart) + "ms" );
	}
}
