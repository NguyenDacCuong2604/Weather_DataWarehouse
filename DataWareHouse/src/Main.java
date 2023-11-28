import controller.Controller;
import dao.ForecastResultsDao;
import database.DBConnection;
import entity.Config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        DBConnection db = new DBConnection();
        ForecastResultsDao dao = new ForecastResultsDao();
        try (Connection connection = db.getConnection()) {
            List<Config> configs = dao.getConfigs(connection);
            Controller controller = new Controller();

            for(Config config : configs){
                String status = config.getStatus();

                //nếu lỗi thì không cần thực hiện
                if(status.equals("ERROR")){
                    continue;
                }
                //bước lấy dữ liệu từ API
                else if(status.equals("OFF")){
                    controller.getData(connection, config);
                } else if (status.equals("CRAWLED")) {
                    controller.extractToStaging(connection, config);
                } else if (status.equals("TRANSFORMING")) {
                    controller.transformData(connection, config);
                }
                else if (status.equals("WH_LOADING")){
                    controller.loadToWH(connection, config);
                }
            }
            db.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
