package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVWriter;
import dao.ForecastResultsDao;
import database.DBConnection;
import entity.Config;
import util.SendMail;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
    // Configuration file path
    private static final String FILE_CONFIG = "\\config.properties";

    // API Key, URL, and list of cities
    static String apiKey;
    static String url;
    static List<String> cities;
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


    public void getData(Connection connection, Config config) {
        ForecastResultsDao dao = new ForecastResultsDao();
        dao.updateStatus(connection, config.getId(), "CRAWLING");

        //Create file datasource with pathSource
        DateTimeFormatter dtf_file = DateTimeFormatter.ofPattern("dd-MM-yy_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String fileName = config.getFileName();
        String pathFileCsv = config.getPath();
        String pathSource = pathFileCsv + "\\" + fileName +dtf_file.format(now)+ ".csv";
        try {


            CSVWriter writer = new CSVWriter(new FileWriter(pathSource));

            // loop i (city)
            Iterator<String> iterator = cities.iterator();

            while (iterator.hasNext()) {
                String city = iterator.next();
                //Connect URL API with city
                String urlCity = String.format(url, city.replace(" ", "%20"), apiKey);
                URL url = new URL(urlCity);
                HttpURLConnection connectionHTTP = (HttpURLConnection) url.openConnection();
                connectionHTTP.setRequestMethod("GET");

                int responseCode = connectionHTTP.getResponseCode();
                //Get ResponseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    //6. Get Data from response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connectionHTTP.getInputStream()));
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
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        data.add(dtf.format(formatter));

                        //Write data from arraylist to CSV
                        writer.writeNext(data.toArray(new String[0]));
                    }
                } else {
                    String mail = config.getEmail();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss dd/MM/yyyy");
                    LocalDateTime nowTime = LocalDateTime.now();
                    String timeNow = nowTime.format(dtf);
                    String subject = "Error Date: " + timeNow;
                    String message = "Error getData with city: "+city;
                    SendMail.sendMail(mail, subject, message);
                }
            }
            writer.close();
        } catch (IOException e) {
            dao.updateStatus(connection, config.getId(), "ERROR");
        }
        System.out.println("CRAWLED success");
        dao.updateDetailFilePath(connection, config.getId(), pathSource);
        config.setDetailPathFile(pathSource);
        dao.updateStatus(connection, config.getId(), "CRAWLED");
        extractToStaging(connection, config);
    }

    public static void extractToStaging(Connection connection, Config config){
        ForecastResultsDao dao = new ForecastResultsDao();
        dao.updateStatus(connection, config.getId(), "EXTRACTING");
        //truncate table
        truncateTable(connection, config);
        //load data to staging
        String sqlLoadData = "LOAD DATA INFILE ? INTO TABLE staging.staging FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\\n' IGNORE 0 LINES (\n" +
                "    cod, message, cnt, city_id, city_name, city_latitude, city_longitude, city_country_code, city_population,\n" +
                "    city_timezone, city_sunrise, city_sunset, dt, dt_txt, main_temp, main_feels_like, main_temp_min,\n" +
                "    main_temp_max, main_pressure, main_sea_level, main_grnd_level, main_humidity, main_temp_kf, weather_id,\n" +
                "    weather_main, weather_description, weather_icon, clouds_all, wind_speed, wind_deg, wind_gust, visibility,\n" +
                "    pop, rain_3h, sys_pod, created_date)";
        try {
            //Load data to staging
            PreparedStatement psLoadData = connection.prepareStatement(sqlLoadData);
            psLoadData.setString(1, config.getDetailPathFile());
            psLoadData.execute();

            System.out.println("Load staging success");

            dao.updateStatus(connection, config.getId(), "EXTRACTED");
            transformData(connection, config);

        } catch (SQLException e) {
            e.printStackTrace();
            dao.updateStatus(connection, config.getId(), "ERROR");
            String mail = config.getEmail();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss dd/MM/yyyy");
            LocalDateTime nowTime = LocalDateTime.now();
            String timeNow = nowTime.format(dtf);
            String subject = "Error Date: " + timeNow;
            String message = "Error with message: "+e.getMessage();
            SendMail.sendMail(mail, subject, message);
        }
    }

    public static void truncateTable(Connection connection, Config config) {
        try (CallableStatement callableStatement = connection.prepareCall("{CALL truncate_staging_table()}")) {
        } catch (SQLException e) {
            e.printStackTrace();
            String mail = config.getEmail();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss dd/MM/yyyy");
            LocalDateTime nowTime = LocalDateTime.now();
            String timeNow = nowTime.format(dtf);
            String subject = "Error Date: " + timeNow;
            String message = "Error with message: "+e.getMessage();
            SendMail.sendMail(mail, subject, message);
        }
    }

    public static void transformData(Connection connection, Config config){
        ForecastResultsDao dao = new ForecastResultsDao();
        dao.updateStatus(connection, config.getId(), "TRANSFORMING");

        try (CallableStatement callableStatement = connection.prepareCall("{CALL TransformData()}")) {
            // Thực hiện stored procedure
            callableStatement.execute();

            dao.updateStatus(connection, config.getId(), "TRANSFORMED");
            System.out.println("transform success!");
            loadToWH(connection, config);
        } catch (SQLException e) {
            // Xử lý lỗi khi thực hiện stored procedure
            e.printStackTrace();
            dao.updateStatus(connection, config.getId(), "ERROR");
            String mail = config.getEmail();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss dd/MM/yyyy");
            LocalDateTime nowTime = LocalDateTime.now();
            String timeNow = nowTime.format(dtf);
            String subject = "Error Date: " + timeNow;
            String message = "Error with message: "+e.getMessage();
            SendMail.sendMail(mail, subject, message);
        }
    }

    public static void loadToWH(Connection connection, Config config){
        ForecastResultsDao dao = new ForecastResultsDao();
        dao.updateStatus(connection, config.getId(), "WH_LOADING");

        try (CallableStatement callableStatement = connection.prepareCall("{CALL LoadDataToWH()}")) {
            // Thực hiện stored procedure
            callableStatement.execute();

            dao.updateStatus(connection, config.getId(), "WH_LOADED");
            dao.updateStatus(connection, config.getId(), "FINISHED");
            System.out.println("load to warehouse success!");
        } catch (SQLException e) {
            // Xử lý lỗi khi thực hiện stored procedure
            e.printStackTrace();
            dao.updateStatus(connection, config.getId(), "ERROR");
            //send mail
            String mail = config.getEmail();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss dd/MM/yyyy");
            LocalDateTime nowTime = LocalDateTime.now();
            String timeNow = nowTime.format(dtf);
            String subject = "Error Date: " + timeNow;
            String message = "Error with message: "+e.getMessage();
            SendMail.sendMail(mail, subject, message);
        }
    }

    public static void loadToAggregate(){
        DBConnection db = new DBConnection();
        try (Connection connection = db.getConnection()) {
            CallableStatement callableStatement = connection.prepareCall("{CALL LoadDataToAggregate()}");
            callableStatement.execute();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
