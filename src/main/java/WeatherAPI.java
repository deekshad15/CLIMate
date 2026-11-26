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

    /** Parses the current weather values from an Open-Meteo JSON response. */
    public WeatherData parseCurrentWeather(String weatherJson) {
        JsonObject response = JsonParser.parseString(weatherJson).getAsJsonObject();
        JsonObject current = response.getAsJsonObject("current");

        double temperature = Double.parseDouble(
                current.get("temperature_2m").getAsString());
        double apparentTemperature = Double.parseDouble(
                current.get("apparent_temperature").getAsString());
        double precipitation = Double.parseDouble(
                current.get("precipitation").getAsString());
        double windSpeed = Double.parseDouble(
                current.get("wind_speed_10m").getAsString());

        int weatherCode = current.get("weather_code").getAsInt();
        String conditions = getWeatherDescription(weatherCode);

        return new WeatherData(
                temperature, apparentTemperature, conditions, windSpeed, precipitation);
    }

    /** Converts an Open-Meteo weather code into a readable description. */
    private String getWeatherDescription(int code) {
        switch (code) {
            case 0: return "Clear sky";
            case 1: return "Mainly clear";
            case 2: return "Partly cloudy";
            case 3: return "Overcast";
            case 45: return "Fog";
            case 48: return "Depositing rime fog";
            case 51: return "Light drizzle";
            case 53: return "Moderate drizzle";
            case 55: return "Dense drizzle";
            case 56: return "Light freezing drizzle";
            case 57: return "Dense freezing drizzle";
            case 61: return "Slight rain";
            case 63: return "Moderate rain";
            case 65: return "Heavy rain";
            case 66: return "Light freezing rain";
            case 67: return "Heavy freezing rain";
            case 71: return "Slight snowfall";
            case 73: return "Moderate snowfall";
            case 75: return "Heavy snowfall";
            case 77: return "Snow grains";
            case 80: return "Slight rain showers";
            case 81: return "Moderate rain showers";
            case 82: return "Violent rain showers";
            case 85: return "Slight snow showers";
            case 86: return "Heavy snow showers";
            case 95: return "Thunderstorm";
            case 96: return "Thunderstorm with slight hail";
            case 97: return "Heavy thunderstorm";
            case 99: return "Thunderstorm with heavy hail";
            default: return "Unknown conditions";
        }
    }
}
