import java.io.IOException;
import java.util.Scanner;

/** Starts the CLIMate command-line application. */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Welcome to CLIMate!");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your city: ");
        String city = scanner.nextLine();

        System.out.println("You entered: " + city);

        WeatherAPI weatherAPI = new WeatherAPI();
        double[] coordinates = weatherAPI.getCoordinates(city);

        if (coordinates == null) {
            System.out.println("City not found.");
        } else {
            System.out.println("Latitude: " + coordinates[0]);
            System.out.println("Longitude: " + coordinates[1]);

            String weatherJson = weatherAPI.getCurrentWeatherJson(
                    coordinates[0], coordinates[1]);
            WeatherData weather = weatherAPI.parseCurrentWeather(weatherJson);

            System.out.println("Current weather:");
            System.out.println("Temperature: " + weather.getTemperature() + " °F");
            System.out.println("Feels like: " + weather.getApparentTemperature() + " °F");
            System.out.println("Conditions: " + weather.getConditions());
            System.out.println("Wind speed: " + weather.getWindSpeed() + " mph");
            System.out.println("Precipitation: " + weather.getPrecipitation() + " mm");
        }
    }
}
