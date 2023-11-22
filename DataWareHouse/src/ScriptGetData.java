import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptGetData {
    public static void runConfig(){
        List<Map<String, String>> configs = getConfigs();
        for(Map<String, String> map : configs){
            algo(map);
        }
    }

    private static void algo(Map<String, String> map) {
        try {
            //dua theo map lay ra file get data
            String path=null;

//        insertFileLog(id_config, status2, author, path);
        }catch (Exception e){
//            insertFileLog(id_config, status1, author, status5);
            PrintWriter pw = printErr();
            e.printStackTrace(pw);
            pw.close();
            // send mail to author
            String mail = config.get("mail");
            String file_name = config.get("file_name");
            String timenow = timenow();
            String subject = "err date: " + timenow;
            String message = "error in file_name: " + file_name + ", time: " + timenow;
            MailService.sendMail(mail, subject, message);
        }

    }
    public static void insertFileLog(int id_config, String status, String author, String path) {
        Connection con = ConnectionLogFile.getCon();
        String sql = "insert into controller.file_log(id_config,log_status,author,paths) values(?,?,?,?);";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id_config);
            ps.setString(2, status);
            ps.setString(3, author);
            ps.setString(4, path);
            ps.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static List<Map<String, String>> getConfigs() {
        List<Map<String, String>> configs = new ArrayList<Map<String, String>>();
        Connection con = ConnectionLogFile.getCon();

        //lay het all trong controller.config
        String sql = "";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Map<String, String> map = new HashMap<String, String>();
                //lay du lieu tu table config put vao map
                configs.add(map);
            }
        } catch (SQLException e) {
            MailService.sendMail("nguyendaccuong2002@gmail.com", "DataWarehouse 2023",e.getMessage());
            throw new RuntimeException(e);
        }
        return configs;
    }
    public static PrintWriter printErr() {
        Map<String, String> pathFile = loadDefaultConfig();
        String pathFileError = pathFile.get("PathFileError");

//		String pathFileError="datawarehousess\\ERROR.txt";
//		String pathFileError="D:\\testError\\ERROR.txt";
        File f = new File(pathFileError);
        OutputStream os;
        try {
            os = new FileOutputStream(f, true);
            PrintWriter pw = new PrintWriter(os);
            pw.println(timenow());
            return pw;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
