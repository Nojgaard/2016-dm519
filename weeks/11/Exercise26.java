import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;

public class Exercise26 {
	private static final int N_THREADS = 4;
	private static ExecutorService exec = Executors.newCachedThreadPool();

	private static Integer visitor(File dir) {
		ArrayList<Future<Integer>> dirs = new ArrayList<>();
		int counter = 0;
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				dirs.add(exec.submit(() -> {return visitor(f);}));
			} else {
				if (f.getPath().endsWith(".pdf")) {
					System.out.println(f.getPath());
					counter++;
				}
			}
		}
		for (Future<Integer> fdir : dirs) {
			try {
				counter += fdir.get();
			} catch (Exception e) {}
		}
		return counter;
	}

	public static void main (String[] args) {
		Integer files = visitor(new File(args[0]));
		exec.shutdown();
		System.out.println("PDF Files: " + files.toString());
	}
}
