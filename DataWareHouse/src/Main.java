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
        //3. Kết nối với với database Controller
        try (Connection connection = db.getConnection()) {
            //4. Lấy danh sách config trong table config có flag = 1
            List<Config> configs = dao.getConfigs(connection);
            Controller controller = new Controller();
            for (Config config : configs) {

                int maxWait = 0;
                while (dao.getProcessingCount(connection) != 0 && maxWait <= 3) {
                    System.out.println("Wait...");
                    maxWait++;
                    Thread.sleep(60000); //60s
                }
                if (dao.getProcessingCount(connection) == 0) {
                    System.out.println("Start");
                    String status = config.getStatus();
                    //nếu lỗi thì không cần thực hiện
                    if (status.equals("ERROR")) {
                        continue;
                    }
                    //bước lấy dữ liệu từ API
                    else if (status.equals("OFF") || status.equals("FINISHED")) {
                        controller.getData(connection, config);
                    } else if (status.equals("CRAWLED")) {
                        controller.extractToStaging(connection, config);
                    } else if (status.equals("EXTRACTED")) {
                        controller.transformData(connection, config);
                    } else if (status.equals("TRANSFORMED")) {
                        controller.loadToWH(connection, config);
                    } else if (status.equals("WH_LOADED")) {
                        controller.loadToAggregate(connection, config);
                    } else if (status.equals("AGGREGATED")) {
                        controller.loadToDataMart(connection, config);
                    }
                }
            }
            db.closeConnection();
        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
