/** Creates clothing recommendations based on temperature. */
public class ClothingRecommender {
    public String getRecommendation(WeatherData weather) {
        double temperature = weather.getTemperature();

        if (temperature >= 80) {
            return "It's warm out. Wear a light shirt and shorts.";
        } else if (temperature >= 60) {
            return "Wear a T-shirt and comfortable pants.";
        } else if (temperature >= 45) {
            return "Wear a sweater or light jacket.";
        } else {
            return "Wear a warm coat and layers.";
        }
    }
}
