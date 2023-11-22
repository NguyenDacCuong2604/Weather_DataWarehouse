import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptGetData {

    /**
     * Load the default configuration, execute a specific algorithm using the configuration data,
     * and handle exceptions appropriately.
     *
     * This method serves as an entry point for running a predefined configuration algorithm.
     * It retrieves the default configuration, performs a specific algorithm, and handles exceptions
     * that may occur during the algorithm execution, logging errors and sending emails when necessary.
     *
     * @throws RuntimeException If an error occurs during the algorithm execution or exception handling.
     *                          This includes issues such as database access errors, file operations,
     *                          mail sending errors, or any other unexpected runtime issues.
     *                          The exception is caught, printed to the console, and appropriate logging is performed.
     */
    public static void runConfig() {
        // Load the default configuration.
        Map<String, String> config = loadDefaultConfig();
        // Execute the specific algorithm with the loaded configuration.
        algo(config);
    }

    /**
     * Perform a specific algorithm using configuration data, handle exceptions, and log results.
     *
     * @param config A map containing configuration data with key-value pairs:
     *               - "id_config": The identifier for the configuration (as a string).
     *               - "author": The author of the configuration.
     *               - "dateTimeNow": The date and time format for logging.
     *               - "PathFileError": The path to the error log file.
     *               - "pathFileCsv": The path to the CSV file.
     *               - "status1" to "status5": Various status values associated with the configuration.
     *
     * @throws RuntimeException If an error occurs during file operations or mail sending.
     *                          This includes issues such as file not found, IOException, or SMTP errors.
     *                          The exception is caught, printed to the console, and appropriate logging is performed.
     */
    private static void algo(Map<String, String> config) {
        // Extract data from the configuration.
        int id_config = Integer.parseInt(config.get("id_config"));
        String author = config.get("author");
        String dateTimeNow = config.get("dateTimeNow");
        String PathFileError = config.get("PathFileError");
        String pathFileCsv = config.get("pathFileCsv");
        String status1 = config.get("status1");
        String status2 = config.get("status2");
        String status3 = config.get("status3");
        String status4 = config.get("status4");
        String status5 = config.get("status5");
        try {
            // Perform the algorithm and get data.
            String path = Extract.getData();
            // Insert log with success status.
            insertFileLog(id_config, status2, author, path);
        } catch (IOException e) {
            // Handle IOException by logging the error and sending mail to the author.
            insertFileLog(id_config, status1, author, status5);
            PrintWriter pw = printErr();
            e.printStackTrace(pw);
            pw.close();
            // Send mail to the author with error details.
            String mail = config.get("mail");
            String file_name = config.get("file_name");
            String timeNow = timeNow();
            String subject = "Error Date: " + timeNow;
            String message = "Error in file_name: " + file_name + ", Time: " + timeNow;
            MailService.sendMail(mail, subject, message, PathFileError);
        }
    }

    /**
     * Inserts a file log record into the 'file_log' table in the database.
     *
     * @param id_config The identifier for the configuration.
     * @param status The status of the file log.
     * @param author The author of the file log.
     * @param path The path associated with the file log.
     *
     * @throws SQLException If a database access error occurs.
     *                     This includes SQL syntax errors, connection errors, etc.
     *                     It is important to handle SQLException appropriately in your application.
     */
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

    /**
     * Retrieves a list of configurations from the 'config' table in the database.
     *
     * @return A list of maps, where each map represents a configuration with key-value pairs:
     *         - "id_config": The identifier for the configuration (as a string).
     *         - "author": The author of the configuration.
     *         - "mail": The mail associated with the configuration.
     *         - "file_name": The file name associated with the configuration.
     *         - "dateTimeNow": The date and time of the configuration.
     *         - "PathFileError": The path to the error file associated with the configuration.
     *         - "pathFileCsv": The path to the CSV file associated with the configuration.
     *         - "status1" to "status5": Various status values associated with the configuration.
     *
     * @throws RuntimeException If a database access error occurs.
     *                          This includes SQL syntax errors, connection errors, etc.
     *                          It is important to handle SQLException appropriately in your application.
     */
    public static List<Map<String, String>> getConfigs() {
        List<Map<String, String>> configs = new ArrayList<>();
        Connection con = ConnectionLogFile.getCon();

        String sql = "select id_config,author,mail,file_name,dateTimeNow,PathFileError, pathFileCsv,status1,status2,status3,status4,status5 from controller.config;";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Map<String, String> map = new HashMap<>();
                map.put("id_config", Integer.toString(rs.getInt("id_config")));
                map.put("author", rs.getString("author"));
                map.put("mail", rs.getString("mail"));
                map.put("file_name", rs.getString("file_name"));

                map.put("dateTimeNow", rs.getString("dateTimeNow"));
                map.put("PathFileError", rs.getString("PathFileError"));
                map.put("pathFileCsv", rs.getString("pathFileCsv"));

                map.put("status1", rs.getString("status1"));
                map.put("status2", rs.getString("status2"));
                map.put("status3", rs.getString("status3"));
                map.put("status4", rs.getString("status4"));
                map.put("status5", rs.getString("status5"));
                configs.add(map);
            }
            return configs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the default configuration from the 'config' table in the database.
     *
     * @return A map containing default configuration values with key-value pairs:
     *         - "id_config": The identifier for the default configuration (as a string).
     *         - "author": The author of the default configuration.
     *         - "mail": The mail associated with the default configuration.
     *         - "file_name": The default file name associated with the configuration.
     *         - "dateTimeNow": The default date and time of the configuration.
     *         - "PathFileError": The default path to the error file associated with the configuration.
     *         - "pathFileCsv": The default path to the CSV file associated with the configuration.
     *         - "status1" to "status5": Default values for various status associated with the configuration.
     *
     * @throws RuntimeException If a database access error occurs.
     *                          This includes SQL syntax errors, connection errors, etc.
     *                          It is important to handle SQLException appropriately in your application.
     *                          Returns null if an exception occurs during the database query.
     */
    public static Map<String, String> loadDefaultConfig() {
        Connection con = ConnectionLogFile.getCon();
        String sql = "select id_config,author,mail,file_name,dateTimeNow,PathFileError,pathFileCsv,status1,status2,status3,status4,status5 from controller.config ;";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            Map<String, String> mp = new HashMap<>();

            while (rs.next()) {
                mp.put("id_config", Integer.toString(rs.getInt("id_config")));
                mp.put("author", rs.getString("author"));
                mp.put("mail", rs.getString("mail"));
                mp.put("file_name", rs.getString("file_name"));
                mp.put("dateTimeNow", rs.getString("dateTimeNow"));
                mp.put("PathFileError", rs.getString("PathFileError"));
                mp.put("pathFileCsv", rs.getString("pathFileCsv"));
                mp.put("status1", rs.getString("status1"));
                mp.put("status2", rs.getString("status2"));
                mp.put("status3", rs.getString("status3"));
                mp.put("status4", rs.getString("status4"));
                mp.put("status5", rs.getString("status5"));
            }
            return mp;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the error log file path from the default configuration, initializes a PrintWriter,
     * and returns it for printing error messages.
     *
     * @return A PrintWriter object for writing error messages to the error log file.
     *         Returns null if an exception occurs during file initialization or if the file is not found.
     *
     * @throws RuntimeException If a file access error occurs during PrintWriter initialization.
     *                          This includes issues such as file not found or permission problems.
     *                          The exception is caught and printed to the console.
     */
    public static PrintWriter printErr() {
        // Load the default configuration to get the error log file path
        Map<String, String> pathFile = loadDefaultConfig();
        String pathFileError = pathFile.get("PathFileError");
        String nameFile = pathFileError + "\\Error.txt";
        File f = new File(nameFile);

        try {
            // Initialize an OutputStream for appending to the error log file
            OutputStream os = new FileOutputStream(f, true);
            // Initialize a PrintWriter with the OutputStream
            PrintWriter pw = new PrintWriter(os);
            // Print the current time to the error log file
            pw.println(timeNow());
            // Return the initialized PrintWriter for writing error messages
            return pw;
        } catch (FileNotFoundException e) {
            // FileNotFound exception is caught and printed to the console.
            e.printStackTrace();
            // Return null to indicate that the PrintWriter could not be initialized.
            return null;
        }
    }

    /**
     * Retrieves the date and time format from the default configuration,
     * formats the current date and time accordingly, and returns it as a string.
     *
     * @return A string representing the current date and time formatted according to the configuration.
     *
     * @throws RuntimeException If an error occurs during the date and time formatting.
     *                          This includes issues such as an invalid date-time format pattern.
     *                          The exception is caught and printed to the console.
     */
    public static String timeNow() {
        // Load the default configuration to get the date and time format
        Map<String, String> pathFile = loadDefaultConfig();
        String dateTimeNow = pathFile.get("dateTimeNow");

        try {
            // Format the current date and time according to the specified pattern
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateTimeNow);
            LocalDateTime now = LocalDateTime.now();
            return dtf.format(now);
        } catch (DateTimeParseException e) {
            // DateTimeParseException is caught and printed to the console.
            e.printStackTrace();
            // Return an empty string to indicate that the date and time could not be formatted.
            return "";
        }
    }
}
