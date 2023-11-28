package dao;

import entity.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForecastResultsDao {

    public static List<Config> getConfigs(Connection connection) {
        List<Config> configs = new ArrayList<>();
        //Câu select lấy list config muốn run
        String query = "SELECT * FROM config WHERE flag = 1";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                configs.add(new Config());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return configs;
    }

    public static String getStatus(Connection connection, int id) {
        String status = "";
        //Lấy thuộc tính status có trong table config
        String query = "SELECT `status` FROM config WHERE id_config=? LIMIT 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    status = resultSet.getString("status");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    public static void updateStatus(Connection connection, int id, String status) {
        try (CallableStatement callableStatement = connection.prepareCall("{CALL UpdateStatus(?,?)}")) {
            callableStatement.setInt(1, id);
            callableStatement.setString(2, status);
            callableStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
