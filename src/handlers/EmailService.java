package handlers;

import com.sun.mail.util.MailConnectException;
import guiComponents.InputButton;
import guiComponents.InputField;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

/*
 @author Daniel Allen
 9-Nov-2019
 */
public final class EmailService {

    //this is really insecure but I don't exactly have a server I could relay to to send these for me
    private static String email = "ics4u1javafileioassignmentbot@gmail.com";
    private static String password = "isc4uisthebest";
    private static BufferedReader br;
    private static OutputStream os;

    /**
     * Sends a user an email
     *
     * @param recipient the recipient of the email
     * @param subject the subject of the email
     * @param content the content or body of the email
     * @throws MailConnectException
     * @throws MessagingException
     */
    public static void sendEmail(String recipient, String subject, String content) throws MessagingException, MailConnectException{
        Properties properties = System.getProperties();
        String host = "smtp.gmail.com";
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", email);
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);
        message.setSubject(subject);
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setContent(content, "text/html");
        Transport transport = session.getTransport("smtp");
        transport.connect(host, email, password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
