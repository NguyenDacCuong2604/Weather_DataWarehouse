package dao;

import entity.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForecastResultsDao {

    public static List<Config> getConfigs(Connection connection) {
        List<Config> configs = new ArrayList<>();
        //Câu select lấy list config muốn run
        String query = "SELECT * FROM config WHERE flag = 1 ORDER BY update_at DESC";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String author = resultSet.getString("author");
                String email = resultSet.getString("email");
                String fileName = resultSet.getString("filename");
                String directory = resultSet.getString("directory_file");
                String status = resultSet.getString("status_config");
                int flag = resultSet.getInt("flag");
                String detailFilePath = resultSet.getString("detail_file_path");
                Timestamp timestamp = resultSet.getTimestamp("update_at");
                configs.add(new Config(id, author, email, fileName, directory, status, flag, timestamp, detailFilePath));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return configs;
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

    public static void updateDetailFilePath(Connection connection, int id, String detailFilePath) {
        try (CallableStatement callableStatement = connection.prepareCall("{CALL UpdatePathFileDetail(?,?)}")) {
            callableStatement.setInt(1, id);
            callableStatement.setString(2, detailFilePath);
            callableStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
