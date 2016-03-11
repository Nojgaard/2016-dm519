import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.SortedSet;

public class Exercise19
{	
	/*
	 * - Change the letter counter to count the occurrences of every letter in the text, using a Map.
	 *   - For example, for the text "hello", the resulting map should contain:
	 *   "h" -> 1
	 *   "e" -> 1
	 *   "l" -> 2
	 *   "o" -> 1
	 *   */
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
			doThat( "Sequential two slices", () -> {
				HashMap<Character,Integer> map = new HashMap<>();
				addMap(map,letterCount( str, 'e', 0, size/2 +1 ));
				addMap(map,letterCount( str, 'e', size/2 + 1, size ));
				printMap(map);
			} );
			doThat( "Multithreaded two slices", () -> {
				HashMap<Character,Integer> map = new HashMap<>();
				Thread t1 = new Thread( () -> {
					addMap(map, letterCount( str, 'e', 0, size/2 +1) );
				} );
				Thread t2 = new Thread( () -> {
					addMap(map, letterCount( str, 'e', size/2 + 1, size ) );
				} );
				t1.start();
				t2.start();
				try {
					t1.join();
					t2.join();
				} catch( InterruptedException e ) {}
				printMap(map);
			} );
			doThat( "Multithreaded three slices", () -> {
				HashMap<Character,Integer> map = new HashMap<>();
				Thread t1 = new Thread( () -> {
					addMap(map, letterCount( str, 'e', 0, size/3+1 ) );
				} );
				Thread t2 = new Thread( () -> {
					addMap(map, letterCount( str, 'e', size/3 + 1, (size/3)*2+1 ) );
				} );
				Thread t3 = new Thread( () -> {
					addMap(map, letterCount( str, 'e', (size/3)*2 + 1, size ) );
				} );	
				// System.out.println("t1: " + 0 + ", " + size/3);
				// System.out.println("t2: " + size/3 + 1 + ", " + (size/3)*2);
				// System.out.println("t3: " + (size/3)*2+1, size);
				t1.start();
				t2.start();
				t3.start();
				try {
					t1.join();
					t2.join();
					t3.join();
				} catch( InterruptedException e ) {}
				printMap(map);
			} );
			doThat( "Multithreaded N slices", () -> {
				final int numCores = 5;
				HashMap<Character,Integer> map = new HashMap<>();
				final Thread[] tt = new Thread[ numCores ];
			
				for( int i = 0; i < numCores; i++ ) {
					final int threadIndex = i;
					if ( i != numCores - 1 ) {
						tt[i] = new Thread( () -> {
							addMap(map,
								letterCount(
									str, 'e', size/numCores * threadIndex,
									((size/numCores)*(threadIndex + 1)) ) );
						} );
					} else {
						tt[i] = new Thread( () -> {
							addMap(map,
								letterCount(
									str, 'e', size/numCores * threadIndex, size ) );
						} );
					}
					
				}
				for( int i = 0; i < numCores; i++ ) {
					tt[i].start();
				}
				try {
					for( int i = 0; i < numCores; i++ ) {
						tt[i].join();
					}
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
