import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JDBCStatement {
    public static Map<String, String> map = ScriptGetData.loadDefaultConfig();
    public static void processing(){
        //Load file to Staging
        Connection connection = ConnectionLogFile.getCon();

        List<String> processes = loadProcesses();
        System.out.println(processes.size());
        try {
            CallableStatement callableStatement = connection.prepareCall("{call load_file_to_staging (?)}");
            for (String p : processes) {
                System.out.println(p);
                callableStatement.setString(1, p);
                ResultSet rs = callableStatement.executeQuery();
            }
        } catch (Exception e) {
            // TODO: handle exception

        }
    }

    public static List<String> loadProcesses(){
        Connection connection = ConnectionLogFile.getCon();
        List<String> mp = new ArrayList<>();
        try {
            //Call procedure
            CallableStatement callableStatement = connection.prepareCall("{call today (?)}");
            callableStatement.setString(1, map.get("status2"));
            ResultSet rs = callableStatement.executeQuery();
            while (rs.next()) {
                mp.add(rs.getString("paths"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // {Send Mail} thong bao loi
        }
        return mp;
    }
}
