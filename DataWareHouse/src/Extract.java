import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVWriter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class demonstrates an example of extracting and loading properties from a configuration file.
 */

public class Extract {
    private static final String FILE_CONFIG = "\\config.properties";
    static String apiKey;
    static String url;
    static List<String> cities;
    static String pathSource;

    //1. Load attributes from the configuration file
    static{
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            String currentDir = System.getProperty("user.dir");
            inputStream = new FileInputStream(currentDir + FILE_CONFIG);
            // load properties from file
            properties.load(inputStream);
            // get property by name
            apiKey = properties.getProperty("apiKey");
            url = properties.getProperty("url");
            pathSource = properties.getProperty("path.source");
            cities = convertCities(properties.getProperty("cities"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close objects
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        //2. Create file datasource with pathSource
        CSVWriter writer = new CSVWriter(new FileWriter(pathSource));
        // 3. loop i (city)
        Iterator<String> iterator = cities.iterator();

        while (iterator.hasNext()){
            String city = iterator.next();
            //4. Connect URL API with city
            String urlCity = String.format(url, city.replace(" ", "%20"), apiKey);
            URL url = new URL(urlCity);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //5. Get Response Code
            int responseCode = connection.getResponseCode();
            //Get ResponseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //6. Get Data from response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                //7. Parse JSON response with Gson
                JsonParser parser = new JsonParser();
                JsonObject jsonResponse = parser.parse(response.toString()).getAsJsonObject();

                //8. Loop through forecast data and write to CSV
                JsonArray forecasts = jsonResponse.getAsJsonArray("list");
                for (int i = 0; i < forecasts.size(); i++) {
                    // 9.Create an ArrayList to hold all the data for each forecast entry
                    List<String> data = new ArrayList<>();

                    //10. add data of forecast to arraylist
                    JsonObject forecast = forecasts.get(i).getAsJsonObject();
                    JsonObject cityInfo = jsonResponse.getAsJsonObject("city");

                    // Add city information
                    data.add(cityInfo.get("id").getAsString());
                    data.add(cityInfo.get("name").getAsString());
                    data.add(cityInfo.getAsJsonObject("coord").get("lat").getAsString());
                    data.add(cityInfo.getAsJsonObject("coord").get("lon").getAsString());
                    data.add(cityInfo.get("country").getAsString());
                    data.add(cityInfo.get("population").getAsString());
                    data.add(cityInfo.get("timezone").getAsString());
                    data.add(cityInfo.get("sunrise").getAsString());
                    data.add(cityInfo.get("sunset").getAsString());

                    // Add forecast information
                    data.add(forecast.get("dt").getAsString());
                    JsonObject mainData = forecast.getAsJsonObject("main");
                    data.add(mainData.get("temp").getAsString());
                    data.add(mainData.get("feels_like").getAsString());
                    data.add(mainData.get("temp_min").getAsString());
                    data.add(mainData.get("temp_max").getAsString());
                    data.add(mainData.get("pressure").getAsString());
                    data.add(mainData.get("humidity").getAsString());

                    JsonArray weatherArray = forecast.getAsJsonArray("weather");
                    JsonObject weatherData = weatherArray.get(0).getAsJsonObject();
                    data.add(weatherData.get("id").getAsString());
                    data.add(weatherData.get("main").getAsString());
                    data.add(weatherData.get("description").getAsString());

                    JsonObject cloudsData = forecast.getAsJsonObject("clouds");
                    data.add(cloudsData.get("all").getAsString());

                    JsonObject windData = forecast.getAsJsonObject("wind");
                    data.add(windData.get("speed").getAsString());
                    data.add(windData.get("deg").getAsString());
                    data.add(windData.get("gust").getAsString());

                    data.add(forecast.get("visibility").getAsString());
                    data.add(forecast.get("pop").getAsString());

                    JsonObject rainData = forecast.getAsJsonObject("rain");
                    if (rainData != null) {
                        data.add(rainData.get("3h").getAsString());
                    } else {
                        data.add(""); // If "rain" data is null, add an empty string
                    }
                    //11.Write data from arraylist to CSV
                    writer.writeNext(data.toArray(new String[0]));
                }
            }
            else {
                //12. Print failed fetch data
                System.out.println("Failed to fetch data. Response code: " + responseCode);
            }
        }
        writer.close();
    }

    /**
     * Converts a string containing city names into a list of strings.
     *
     * @param cities A string containing city names, separated by commas.
     * @return A list of strings representing the city names after removing leading and trailing spaces from each string.
     *         Returns an empty list if the input string is null or doesn't contain any cities.
     * @throws NullPointerException If the input is null.
     */
    private static List<String> convertCities(String cities){
        // Split the string into an array of strings, trim each string, and then collect into a list
        return Arrays.stream(cities.split(",")).map(String::trim).collect(Collectors.toList());
    }
}
