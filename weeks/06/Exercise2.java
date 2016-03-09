import java.util.*;
public class Exercise2  {
    private static class Cities {
        private static List<String> cityNames;
        static {
            cityNames = new LinkedList<>();
            cityNames.add( "Copenhagen" );
            cityNames.add( "Odense" );
        }
        public static List<String> cityNames()
        {
            return cityNames;
        }
        public static void addCity( String city )
        {
            cityNames.add( city );
        }
    }

    private static class LocalCities {
        private static ThreadLocal<List<String>> cityNames
            = new ThreadLocal<List<String>> () {
                public LinkedList<String> initialValue() {
                    return new LinkedList<String>();
                }
            };
        static {
            //cityNames = new LinkedList<>();
            cityNames.get().add( "Copenhagen" );
            cityNames.get().add( "Odense" );
        }
        public static List<String> cityNames()
        {
            return cityNames.get();
        }
        public static void addCity( String city )
        {
            cityNames.get().add( city );
        }
    }

    public static void main( String[] args )
    {
        // new Thread( () -> {
        //     Cities.addCity( "Rome" );
        //     Cities.addCity( "Florence" );
        //     Cities.cityNames().forEach( System.out::println );
        // } ).start();
        // new Thread( () -> {
        //     Cities.addCity( "Berlin" );
        //     Cities.addCity( "Stuttgart" );
        //     Cities.cityNames().forEach( System.out::println );
        // } ).start();
        new Thread( () -> {
            LocalCities.addCity( "Rome" );
            LocalCities.addCity( "Florence" );
            LocalCities.cityNames().forEach( System.out::println );
        } ).start();
        new Thread( () -> {
            LocalCities.addCity( "Berlin" );
            LocalCities.addCity( "Stuttgart" );
            LocalCities.cityNames().forEach( System.out::println );
        } ).start();
    }
}
