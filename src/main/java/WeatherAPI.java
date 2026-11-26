import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/** Looks up city coordinates using the Open-Meteo Geocoding API. */
public class WeatherAPI {
    private final HttpClient client = HttpClient.newHttpClient();

    /**
     * Returns the latitude and longitude for a city, or null when no city is found.
     */
    public double[] getCoordinates(String city) throws IOException, InterruptedException {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String url = "https://geocoding-api.open-meteo.com/v1/search?name="
                + encodedCity + "&count=1&language=en";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());

        JsonObject responseData = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray results = responseData.getAsJsonArray("results");

        if (results == null || results.size() == 0) {
            return null;
        }

        JsonObject firstResult = results.get(0).getAsJsonObject();
        double latitude = firstResult.get("latitude").getAsDouble();
        double longitude = firstResult.get("longitude").getAsDouble();

        return new double[] {latitude, longitude};
    }

    /** Returns the current weather response as raw JSON for the given coordinates. */
    public String getCurrentWeatherJson(double latitude, double longitude)
            throws IOException, InterruptedException {
        String url = "https://api.open-meteo.com/v1/forecast?latitude="
                + latitude + "&longitude=" + longitude
                + "&current=temperature_2m,apparent_temperature,precipitation,"
                + "weather_code,wind_speed_10m"
                + "&temperature_unit=fahrenheit&wind_speed_unit=mph";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
