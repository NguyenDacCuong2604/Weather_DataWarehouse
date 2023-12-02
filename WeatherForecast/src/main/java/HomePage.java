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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedCity = request.getParameter("selectedCity");
        List<String> listCity = getListCity();
        request.setAttribute("listCity", listCity);
        request.setAttribute("cityName", selectedCity); // Set lại cityName theo selectedCity

        List<DataItem> dataList = new ArrayList<>();
        getDataFromDatabase(dataList, selectedCity);
        request.setAttribute("dataList", dataList);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    private void getDataFromDatabase(List<DataItem> dataList, String city){
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
            }catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

    }

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
