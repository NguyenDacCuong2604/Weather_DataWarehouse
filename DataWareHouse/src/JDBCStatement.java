import java.sql.*;
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
            for (String p : processes) {
                boolean isLoadFileToStaging = loadFileToStaging(p);
                if(isLoadFileToStaging){
                    updateStatus(map.get("status3"), p);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception

        }
    }

    public static boolean loadFileToStaging(String paths){
        Connection connection = ConnectionLogFile.getCon();
        String sqlTruncate = "Truncate table staging.staging";
        String sqlLoadData = "LOAD DATA INFILE ? INTO TABLE staging.staging FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\\n' IGNORE 0 LINES (\n" +
                "    cod, message, cnt, city_id, city_name, city_latitude, city_longitude, city_country_code, city_population,\n" +
                "    city_timezone, city_sunrise, city_sunset, dt, dt_txt, main_temp, main_feels_like, main_temp_min,\n" +
                "    main_temp_max, main_pressure, main_sea_level, main_grnd_level, main_humidity, main_temp_kf, weather_id,\n" +
                "    weather_main, weather_description, weather_icon, clouds_all, wind_speed, wind_deg, wind_gust, visibility,\n" +
                "    pop, rain_3h, sys_pod, timeGet)";
        try {
            //Truncate table staging.staging
            PreparedStatement psTruncate = connection.prepareStatement(sqlTruncate);
            psTruncate.execute();

            //Load data to staging
            PreparedStatement psLoadData = connection.prepareStatement(sqlLoadData);
            psLoadData.setString(1, paths);
            psLoadData.execute();

            return true;
        } catch (SQLException e) {
            //send mail
            return false;
        }
    }


    public static void updateStatus(String status, String paths) {
        Connection con = ConnectionLogFile.getCon();
        String sql = "update controller.file_log set log_status =? where paths =?;";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, paths);
            ps.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
