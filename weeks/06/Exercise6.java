
public class Exercise6 {
    private static class PlayerThread extends Thread {
        private int pid;
        private Board b;

        public PlayerThread(int id,Board b) {
            pid = id;
            this.b = b;
        }

        public void run() {
            while (true) {
                if (b.getTurn() == pid) {
                    System.out.println("Player " + pid + " is playing.");
                    b.endTurn();
                }
                if (!b.isRunning()){
                    break;
                }
            }
        }
    }

    private static class Board {
        private int turn = 0, t = 4; int n;
        private int[][] b;
        private boolean running = true;
        
        public Board(int n) {
            this.n = n;
            b = new int[n][n];
            for (int i = 0; i < 0; i++) {
                b[i][i] = -1;
            }
        }

        private synchronized boolean isDone() {
            t = t-1;
           return (t==0); 
        }

        public synchronized boolean isRunning() {
            return running;
        }

        public synchronized int getTurn() {
            return turn;
        }

        public synchronized void endTurn() {
            running = !isDone();
            if (running) turn = (turn+1)%2;
        }

        public synchronized int get(int x, int y) {
            return b[x][y];
        }

        public synchronized void set(int x, int y, int v) {
            b[x][y] = v;
        }
    }
    public static void main (String[] args) {
        Board b = new Board(3);
        PlayerThread t1 = new PlayerThread(0,b);
        PlayerThread t2 = new PlayerThread(1,b);
        t1.start();
        t2.start();
    }
}
