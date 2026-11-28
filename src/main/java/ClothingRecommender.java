/** Creates clothing recommendations based on how the weather will feel. */
public class ClothingRecommender {
    public String getRecommendation(WeatherData weather) {
        double temperature = weather.getApparentTemperature();
        String conditions = weather.getConditions().toLowerCase();
        boolean wetWeather = weather.getPrecipitation() > 0
                || conditions.contains("rain")
                || conditions.contains("drizzle")
                || conditions.contains("snow")
                || conditions.contains("thunderstorm");

        if (temperature >= 80) {
            return addWeatherAdvice(
                    "It's warm out. Wear a light shirt and shorts.", weather, wetWeather);
        } else if (temperature >= 60) {
            return addWeatherAdvice(
                    "Wear a T-shirt and comfortable pants.", weather, wetWeather);
        } else if (temperature >= 45) {
            return addWeatherAdvice(
                    "Wear a sweater or light jacket.", weather, wetWeather);
        } else {
            return addWeatherAdvice(
                    "Wear a warm coat and layers.", weather, wetWeather);
        }
    }

    private String addWeatherAdvice(String recommendation, WeatherData weather,
                                    boolean wetWeather) {
        String conditions = weather.getConditions().toLowerCase();
        if (conditions.contains("snow")) {
            recommendation += " Wear waterproof boots with good traction.";
        } else if (wetWeather) {
            recommendation += " Bring a waterproof jacket and umbrella.";
        }

        if (weather.getWindSpeed() >= 15) {
            recommendation += " A wind-resistant outer layer will help.";
        }

        if (conditions.contains("thunderstorm")) {
            recommendation += " Stay indoors during thunderstorms when possible.";
        }

        return recommendation;
    }
}
