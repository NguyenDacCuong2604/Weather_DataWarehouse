import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVWriter;
import util.SendMail;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class demonstrates an example of extracting and loading properties from a configuration file.
 */

public class Extract {
    // Configuration file path
    private static final String FILE_CONFIG = "\\config.properties";

    // API Key, URL, and list of cities
    static String apiKey;
    static String url;
    static List<String> cities;

    // ScriptGetData instance and configuration data list
    static ScriptGetData scriptGetData;
    static Map<String, String> config = scriptGetData.loadDefaultConfig();

    // Load attributes from the configuration file
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

    /**
     * Extracts data from a weather API for multiple cities and writes the results to a CSV file.
     *
     * @return The path of the CSV file containing the extracted data.
     * @throws IOException If an I/O error occurs during the data extraction or CSV writing.
     */
    public static String getData() throws IOException {
        //Create file datasource with pathSource
        DateTimeFormatter dtf_file = DateTimeFormatter.ofPattern("dd-MM-yy_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String fileName = dtf_file.format(now);
        Map<String, String> configData = config;
        String pathFileCsv = configData.get("pathFileCsv");
        String pathSource = pathFileCsv + "_" + fileName + ".csv";
        CSVWriter writer = new CSVWriter(new FileWriter(pathSource));

        // loop i (city)
        Iterator<String> iterator = cities.iterator();

        while (iterator.hasNext()){
            String city = iterator.next();
            //Connect URL API with city
            String urlCity = String.format(url, city.replace(" ", "%20"), apiKey);
            URL url = new URL(urlCity);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

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

                //Parse JSON response with Gson
                JsonParser parser = new JsonParser();
                JsonObject jsonResponse = parser.parse(response.toString()).getAsJsonObject();

                //Loop through forecast data and write to CSV
                JsonArray forecasts = jsonResponse.getAsJsonArray("list");
                for (int i = 0; i < forecasts.size(); i++) {
                    //Create an ArrayList to hold all the data for each forecast entry
                    List<String> data = new ArrayList<>();

                    //Add data des
                    data.add(jsonResponse.get("cod").getAsString());
                    data.add(jsonResponse.get("message").getAsString());
                    data.add(jsonResponse.get("cnt").getAsString());

                    //Add data of forecast to arraylist
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
                    data.add(forecast.get("dt_txt").getAsString());
                    JsonObject mainData = forecast.getAsJsonObject("main");
                    data.add(mainData.get("temp").getAsString());
                    data.add(mainData.get("feels_like").getAsString());
                    data.add(mainData.get("temp_min").getAsString());
                    data.add(mainData.get("temp_max").getAsString());
                    data.add(mainData.get("pressure").getAsString());
                    data.add(mainData.get("sea_level").getAsString());
                    data.add(mainData.get("grnd_level").getAsString());
                    data.add(mainData.get("humidity").getAsString());
                    data.add(mainData.get("temp_kf").getAsString());

                    JsonArray weatherArray = forecast.getAsJsonArray("weather");
                    JsonObject weatherData = weatherArray.get(0).getAsJsonObject();
                    data.add(weatherData.get("id").getAsString());
                    data.add(weatherData.get("main").getAsString());
                    data.add(weatherData.get("description").getAsString());
                    data.add(weatherData.get("icon").getAsString());

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

                    JsonObject sysData = forecast.getAsJsonObject("sys");
                    data.add(sysData.get("pod").getAsString());

                    //Time now
                    LocalDateTime dtf = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    data.add(dtf.format(formatter));

                    //Write data from arraylist to CSV
                    writer.writeNext(data.toArray(new String[0]));
                }
            }
            else {
                // Handle IOException by logging the error and sending mail to the author.
                ScriptGetData.insertFileLog(Integer.parseInt(config.get("id_config")), config.get("status1"), config.get("author"), config.get("status5"));
                PrintWriter pw = ScriptGetData.printErr();
                pw.println("Failed to fetch data. Response code: 404 with city: "+city);
                pw.close();
                // Send mail to the author with error details.
                String mail = config.get("mail");
                String file_name = config.get("file_name");
                String timeNow = ScriptGetData.timeNow();
                String subject = "Error Date: " + timeNow;
                String message = "Error in file_name: " + file_name + ", Time: " + timeNow;
                SendMail.sendMail(mail, subject, message, config.get("PathFileError"));
            }
        }
        writer.close();
        return pathSource;
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

    /**
     * Test method for extracting data. Uncomment and run to test data extraction.
     *
     * @param args Command line arguments.
     * @throws IOException If an I/O error occurs during the data extraction or CSV writing.
     */
    public static void main(String[] args) throws IOException {
//        getData();
        getDataDefault("D:\\Test");
    }


    /**
     * Retrieves weather data for multiple cities from a weather API, parses the response, and writes the results to a CSV file.
     *
     * @param pathFileCsv The path where the CSV file will be saved.
     * @return The path of the CSV file containing the extracted data.
     * @throws IOException If an I/O error occurs during the data extraction or CSV writing.
     */
    private static String getDataDefault(String pathFileCsv) throws IOException {
        //Create file datasource with pathSource
        DateTimeFormatter dtf_file = DateTimeFormatter.ofPattern("dd-MM-yy_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String fileName = dtf_file.format(now);
        String pathSource = pathFileCsv + "_" + fileName + ".csv";
        CSVWriter writer = new CSVWriter(new FileWriter(pathSource));

        // loop i (city)
        Iterator<String> iterator = cities.iterator();

        while (iterator.hasNext()){
            String city = iterator.next();
            //Connect URL API with city
            String urlCity = String.format(url, city.replace(" ", "%20"), apiKey);
            URL url = new URL(urlCity);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

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

                //Parse JSON response with Gson
                JsonParser parser = new JsonParser();
                JsonObject jsonResponse = parser.parse(response.toString()).getAsJsonObject();

                //Loop through forecast data and write to CSV
                JsonArray forecasts = jsonResponse.getAsJsonArray("list");
                for (int i = 0; i < forecasts.size(); i++) {
                    //Create an ArrayList to hold all the data for each forecast entry
                    List<String> data = new ArrayList<>();

                    //Add data of forecast to arraylist
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
                    //Time now
                    LocalDateTime dtf = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    data.add(dtf.format(formatter));

                    //Write data from arraylist to CSV
                    writer.writeNext(data.toArray(new String[0]));
                }
            }
            else {
                //Print failed fetch data
                System.out.println("Failed to fetch data. Response code: " + responseCode);
            }
        }
        writer.close();
        return pathSource;
    }
}
