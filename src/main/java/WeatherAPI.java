import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Looks up city coordinates using the Open-Meteo Geocoding API. */
public class WeatherAPI {
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Returns the latitude and longitude for a city, or null when no city is found.
     */
    public double[] getCoordinates(String city) throws IOException, InterruptedException {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String url = "https://geocoding-api.open-meteo.com/v1/search?name="
                + encodedCity + "&count=1&language=en";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());

        ensureSuccessfulResponse(response, "Geocoding");

        JsonObject responseData = parseObject(response.body(), "Geocoding");
        JsonElement resultsElement = responseData.get("results");
        if (resultsElement == null || resultsElement.isJsonNull()) {
            return null;
        }
        if (!resultsElement.isJsonArray()) {
            throw new IOException("Geocoding service returned invalid city results.");
        }
        JsonArray results = resultsElement.getAsJsonArray();

        if (results.size() == 0) {
            return null;
        }

        JsonElement firstElement = results.get(0);
        if (!firstElement.isJsonObject()) {
            throw new IOException("Geocoding service returned invalid city coordinates.");
        }
        JsonObject firstResult = firstElement.getAsJsonObject();
        double latitude = getRequiredNumber(firstResult, "latitude");
        double longitude = getRequiredNumber(firstResult, "longitude");

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
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());

        ensureSuccessfulResponse(response, "Weather");
        return response.body();
    }

    /** Parses the current weather values from an Open-Meteo JSON response. */
    public WeatherData parseCurrentWeather(String weatherJson) throws IOException {
        JsonObject response = parseObject(weatherJson, "Weather");
        JsonElement currentElement = response.get("current");
        if (currentElement == null || !currentElement.isJsonObject()) {
            throw new IOException("Weather response is missing current conditions.");
        }
        JsonObject current = currentElement.getAsJsonObject();

        double temperature = getRequiredNumber(current, "temperature_2m");
        double apparentTemperature = getRequiredNumber(current, "apparent_temperature");
        double precipitation = getRequiredNumber(current, "precipitation");
        double windSpeed = getRequiredNumber(current, "wind_speed_10m");
        int weatherCode = getRequiredInteger(current, "weather_code");
        String conditions = getWeatherDescription(weatherCode);

        return new WeatherData(
                temperature, apparentTemperature, conditions, windSpeed, precipitation);
    }

    private void ensureSuccessfulResponse(HttpResponse<String> response, String service)
            throws IOException {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException(service + " service returned HTTP " + response.statusCode() + ".");
        }
    }

    private JsonObject parseObject(String json, String service) throws IOException {
        try {
            JsonElement parsed = JsonParser.parseString(json);
            if (!parsed.isJsonObject()) {
                throw new IOException(service + " service returned an invalid response.");
            }
            return parsed.getAsJsonObject();
        } catch (JsonParseException e) {
            throw new IOException(service + " service returned invalid JSON.", e);
        }
    }

    private double getRequiredNumber(JsonObject object, String field) throws IOException {
        JsonElement value = object.get(field);
        if (value == null || value.isJsonNull() || !value.isJsonPrimitive()
                || !value.getAsJsonPrimitive().isNumber()) {
            throw new IOException("API response is missing numeric data for '" + field + "'.");
        }

        try {
            double number = value.getAsDouble();
            if (!Double.isFinite(number)) {
                throw new IOException("API response contains invalid data for '" + field + "'.");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new IOException("API response contains invalid data for '" + field + "'.", e);
        }
    }

    private int getRequiredInteger(JsonObject object, String field) throws IOException {
        double value = getRequiredNumber(object, field);
        if (value != Math.rint(value) || value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new IOException("API response contains invalid data for '" + field + "'.");
        }
        return (int) value;
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
