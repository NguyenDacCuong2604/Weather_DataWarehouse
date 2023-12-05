import DB.DBConnection;
import Entity.DataItem;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@MultipartConfig
@WebServlet(name = "HomePageController", value = "/HomePage")
public class HomePage extends HttpServlet {
    private DataSource dataSource = DBConnection.getDataSource();

    /**
     * Handles HTTP GET requests.
     *
     * @param request  The HttpServletRequest object containing the client's request.
     * @param response The HttpServletResponse object for sending the response.
     * @throws ServletException If there's a servlet-related issue.
     * @throws IOException      If there's an input/output issue.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieving selected city from request parameter
        String selectedCity = request.getParameter("selectedCity");

        // Getting list of cities and setting it as an attribute in the request scope
        List<String> listCity = getListCity();
        request.setAttribute("listCity", listCity);
        request.setAttribute("cityName", selectedCity); // Setting cityName based on selectedCity

        // Fetching data from the database for the selected city and setting it as an attribute in the request scope
        List<DataItem> dataList = new ArrayList<>();
        getDataFromDatabase(dataList, selectedCity);
        request.setAttribute("dataList", dataList);

        // Forwarding the request to the index.jsp page
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Retrieves data from the database based on the selected city and populates the dataList.
     *
     * @param dataList The list to populate with DataItems from the database.
     * @param city     The selected city for retrieving data.
     */
    private void getDataFromDatabase(List<DataItem> dataList, String city) {
        if (city == null) return;
        try (Connection connection = dataSource.getConnection()) {
            dataList.clear();
            String sql = "SELECT * FROM forecast where city_name = ?";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, city);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    // Đọc dữ liệu từ ResultSet và thêm vào danh sách
                    int id = resultSet.getInt("id");
                    String dataOfWeek = resultSet.getString("date_of_week");
                    Date dateForecast = resultSet.getDate("date_forecast");
                    Time timeForecast = resultSet.getTime("time_forecast");
                    String cityName = resultSet.getString("city_name");
                    double mainTemp = resultSet.getDouble("main_temp");
                    int mainPressure = resultSet.getInt("main_pressure");
                    int mainHumidity = resultSet.getInt("main_humidity");
                    int cloudsAll = resultSet.getInt("clouds_all");
                    double windSpeed = resultSet.getDouble("wind_speed");
                    int visibility = resultSet.getInt("visibility");
                    int rain3h = resultSet.getInt("rain_3h");
                    String weatherDescription = resultSet.getString("weather_description").toUpperCase();
                    String weatherIcon = resultSet.getString("weather_icon");
                    DataItem item = new DataItem(id, dataOfWeek, dateForecast, timeForecast, cityName, mainTemp, mainPressure, mainHumidity, cloudsAll, windSpeed, visibility, rain3h, weatherDescription, weatherIcon);
                    dataList.add(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles HTTP POST requests.
     *
     * @param request  The HttpServletRequest object containing the client's request.
     * @param response The HttpServletResponse object for sending the response.
     *                 (Currently empty as no specific POST operation is defined.)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Currently empty as no specific POST operation is defined.
    }

    /**
     * Retrieves the list of cities from the database.
     *
     * @return A List containing names of cities available in the database.
     */
    private List<String> getListCity() {
        List<String> listCity = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT city_name FROM forecast group by (city_name)";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String city = resultSet.getString("city_name");
                    listCity.add(city);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listCity;
    }

}
