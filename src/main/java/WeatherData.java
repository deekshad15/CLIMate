/** Stores current weather measurements for CLIMate. */
public class WeatherData {
    private final double temperature;
    private final double apparentTemperature;
    private final String conditions;
    private final double windSpeed;
    private final double precipitation;

    /**
     * Creates weather data. Temperatures are in Fahrenheit and wind speed is
     * in miles per hour.
     */
    public WeatherData(double temperature, double apparentTemperature,
                       String conditions, double windSpeed, double precipitation) {
        this.temperature = temperature;
        this.apparentTemperature = apparentTemperature;
        this.conditions = conditions;
        this.windSpeed = windSpeed;
        this.precipitation = precipitation;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getApparentTemperature() {
        return apparentTemperature;
    }

    public String getConditions() {
        return conditions;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getPrecipitation() {
        return precipitation;
    }
}
