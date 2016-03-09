public class Exersice3 {
    
    private static class SynchronizedRGB {
        // Values must be between 0 and 255.
        private int red;
        private int green;
        private int blue;
        private String name;

        private void check(int red,
            int green,
            int blue) {
            if (red < 0 || red > 255
            || green < 0 || green > 255
            || blue < 0 || blue > 255) {
                throw new IllegalArgumentException();
            }
        }

        public SynchronizedRGB(int red,
            int green,
            int blue,
            String name) {
            check(red, green, blue);
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.name = name;
        }

        public void set(int red,
            int green,
            int blue,
            String name) {
            check(red, green, blue);
            synchronized (this) {
                this.red = red;
                this.green = green;
                this.blue = blue;
                this.name = name;
            }
        }

        public synchronized int getRGB() {
            return ((red << 16) | (green << 8) | blue);
        }

        public synchronized String getName() {
            return name;
        }

        public synchronized void invert() {
            red = 255 - red;
            green = 255 - green;
            blue = 255 - blue;
            name = "Inverse of " + name;
        }
    }

    final public static class ImmutableRGB {

        //Values must be between 0 and 255.
            final private int red;
        final private int green;
        final private int blue;
        final private String name;

        private void check(int red,
                int green,
                int blue) {
            if (red < 0 || red > 255
                    || green < 0 || green > 255
                    || blue < 0 || blue > 255) {
                throw new IllegalArgumentException();
                    }
        }

        public ImmutableRGB(int red,
                int green,
                int blue,
                String name) {
            check(red, green, blue);
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.name = name;
        }


        public int getRGB() {
            return ((red << 16) | (green << 8) | blue);
        }

        public String getName() {
            return name;
        }

        public ImmutableRGB invert() {
            return new ImmutableRGB(255 - red,
                    255 - green,
                    255 - blue,
                    "Inverse of " + name);
        }
    }

    public static void main (String[] args) {
       SynchronizedRGB r = new SynchronizedRGB(0,0,0,"black");
        Thread t1 = new Thread( () -> {
            int mc = r.getRGB();
            System.out.println(mc);
            String mn = r.getName();
            System.out.println(mn);
        });

        Thread t2 = new Thread ( () -> {
            r.set(255,255,255,"white");
        });
        t1.start();
        t2.start();
        /*ImmutableRGB r = new ImmutableRGB(0,0,0,"white");
        Thread t1 = new Thread( () -> {
            int mc = r.getRGB();
            System.out.println(mc);
            String mn = r.getName();
            System.out.println(mn);
        });

        Thread t2 = new Thread ( () -> {
            r = new ImmutableRGB(255,255,255,"black");
        });
        t1.start();
        t2.start();
        */

    }
}
