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
        List<DataItem> dataList = getDataFromDatabase();
        request.setAttribute("dataList", dataList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    private List<DataItem> getDataFromDatabase() {
        List<DataItem> dataList = new ArrayList<DataItem>();

        DBConnection dbConnection = new DBConnection();
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM forecast where city_name = 'Cao Lanh'";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
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
                    String weatherDescription = resultSet.getString("weather_description");
                    String weatherIcon = resultSet.getString("weather_icon");
                    DataItem item = new DataItem(id, dataOfWeek, dateForecast, timeForecast, cityName, mainTemp, mainPressure, mainHumidity, cloudsAll, windSpeed, visibility, rain3h, weatherDescription, weatherIcon);
                    dataList.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){

    }
}
