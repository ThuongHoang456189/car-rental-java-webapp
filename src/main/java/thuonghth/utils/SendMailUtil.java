package thuonghth.utils;

import java.io.Serializable;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author thuonghth
 */

public class SendMailUtil implements Serializable {
	public static Properties properties;
	// Init properties for SMTP session
	static {
		properties = new Properties();

		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "" + MyConstants.SMTP_PORT);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.socketFactory.port", "" + MyConstants.SMTP_PORT);
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

	}

	public static void sendMail(String toEmail, String confirmCode) {
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(MyConstants.HOST_EMAIL, MyConstants.HOST_EMAIL_PWD);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("from@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

			message.setSubject("carrental Email Verification Code");
			message.setText("Dear customer, here is your carrental Activate Code");
			message.setText("Note that the verification code is valid in " + MyConstants.SESSION_VALID_TIME_IN_MINUTE
					+ " minutes.");
			message.setText("Your verification code: " + confirmCode);

			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
