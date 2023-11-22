import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MailService {
    private static final String FILE_CONFIG = "\\config.properties";
    private static String email;
    private static String password;
    static{
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            String currentDir = System.getProperty("user.dir");
            inputStream = new FileInputStream(currentDir + FILE_CONFIG);
            // load properties from file
            properties.load(inputStream);
            // get property by name
            email = properties.getProperty("email_service");
            password = properties.getProperty("password_mail_service");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close objects
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static Session connect(){
        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        //465, 587
        properties.put("mail.smtp.port", "587");
        //ssl to starttls
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email,password);
            }
        });
        // Used to debug SMTP issues
        session.setDebug(true);
        return session;

    }
    public static boolean sendMail(String to, String subject, String content){
        Session session = connect();
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(email));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText(content);

            // Create a multipart message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart,"UTF-8");

            // Send message
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();

        }
        return false;
    }

    public static void main(String[] args) {
        String email = "20130072@st.hcmuaf.edu.vn";
        String content = "Lỗi rôì. fix đi!";
        MailService.sendMail(email,"Data Warehouse 2023",content);
    }
}
