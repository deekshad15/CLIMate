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
        }
    }
}
