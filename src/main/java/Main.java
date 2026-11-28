import java.io.IOException;
import java.util.Scanner;

/** Starts the CLIMate command-line application. */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("========================================");
        System.out.println("          Welcome to CLIMate");
        System.out.println(" Get current weather and outfit advice");
        System.out.println(" Type 'exit' at any time to quit.");
        System.out.println("========================================");

        Scanner scanner = new Scanner(System.in);
        WeatherAPI weatherAPI = new WeatherAPI();
        ClothingRecommender recommender = new ClothingRecommender();

        while (true) {
            System.out.print("\nCity: ");
            String city = scanner.nextLine().trim();

            if (isExitCommand(city)) {
                System.out.println("Goodbye!");
                break;
            }
            if (city.isEmpty()) {
                System.out.println("Please enter a city name or type 'exit'.");
                continue;
            }

            double[] coordinates = weatherAPI.getCoordinates(city);
            if (coordinates == null) {
                System.out.println("City not found. Try another city or type 'exit'.");
                continue;
            }

            String weatherJson = weatherAPI.getCurrentWeatherJson(
                    coordinates[0], coordinates[1]);
            WeatherData weather = weatherAPI.parseCurrentWeather(weatherJson);
            printWeather(city, weather, recommender);
        }
    }

    private static boolean isExitCommand(String input) {
        return input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit");
    }

    private static void printWeather(String city, WeatherData weather,
                                     ClothingRecommender recommender) {
        System.out.println("\n----------------------------------------");
        System.out.println(" Weather for " + city);
        System.out.println("----------------------------------------");
        System.out.printf("  Temperature:      %.1f °F%n", weather.getTemperature());
        System.out.printf("  Feels like:       %.1f °F%n", weather.getApparentTemperature());
        System.out.println("  Conditions:       " + weather.getConditions());
        System.out.printf("  Wind speed:       %.1f mph%n", weather.getWindSpeed());
        System.out.printf("  Precipitation:    %.1f mm%n", weather.getPrecipitation());
        System.out.println("\n  What to wear");
        System.out.println("  " + recommender.getRecommendation(weather));
        System.out.println("----------------------------------------");
    }
}
