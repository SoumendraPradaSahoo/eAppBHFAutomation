package bhfUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {
	
	// supply your SMTP host name
	private static String host = "smtp.gmail.com";
	
	// supply a static sendTo list
	private static String to = "recipient@host.com";
	
	// supply the sender email
	private static String from = "sender@host.com";
	
	// default cc list
	private static String cc = "";
	
	// default bcc list
	private static String bcc = "";
	
	public static void send(String from, String to, String subject, HashMap<String, String> testResult) throws MessagingException {		
		
		String contents="";
		/*Properties prop = System.getProperties();
		prop.setProperty("mail.smtp.host", host);
		Session session = Session.getDefaultInstance(prop);*/
		
		Log.info("Preparing e-mail send procedure");
		Log.info("To address: " + to);
		Log.info("From address: " + from);
		Log.info("Subject Line: " + subject);
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.ssl.trust", host);
		props.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("username", "password"); //need to provide username and password
			}
		});
		
		contents = "<h3>Automation Test Result</h3>\n";
		
		if (testResult.size()>0) {
			contents = contents + "<table style='height: 85px; border-color: black;' width='300'>\n" + 
					"<tbody>\n" + 
					"<tr bgcolor='black'>\n" + 
					"<td style='width: 87.2px;'><span style='color: #ffffff;'>Test Case Id</span></td>\n" + 
					"<td style='width: 88.8px;'><span style='color: #ffffff;'>Status</span></td>\n" + 
					"</tr>\n" + 
					"<tr>";	
		}
		for(Map.Entry<String, String> entry: testResult.entrySet()) {
			String testcase = entry.getKey();
			String result = entry.getValue();
			if (result.equalsIgnoreCase("PASS")){
				contents += "<tr bgcolor='lightGreen'>";
			}else if (result.equalsIgnoreCase("FAIL")) {
				contents += "<tr bgcolor='FC7A7A'>";
			}else if (result.equalsIgnoreCase("DATA NOT FOUND")) {
				contents += "<tr bgcolor='FC7A7A'>";
			}else if (result.equalsIgnoreCase("SKIPPED")) {
				contents += "<tr bgcolor='8AAAFC'>";
			}else
				contents += "<tr>";
			contents += "\n";
			contents += "<td style='width: 160px;'>" + testcase + "</td>\n";
			contents += "<td style='width: 235px;'>" + result + "</td>\n";
			contents += "</tr>\n";
		}
		
		if (testResult.size()>0) {
			contents = contents + "</tbody>\n</table>\n";
		}
		
		contents += "<br>Thanks,<br>BHF eApp Automation";
		
		Log.info("Email Content \n" + contents);
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.setSubject(subject);
		message.setContent(contents, "text/html");
		
		Log.info("Message composed; ready to send");
		
		List<String> toList = getAddress(to);
		for (String address : toList) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
		}
		
		List<String> ccList = getAddress(cc);
		for (String address : ccList) {
			message.addRecipient(Message.RecipientType.CC, new InternetAddress(address));
		}
		
		List<String> bccList = getAddress(bcc);
		for (String address : bccList) {
			message.addRecipient(Message.RecipientType.BCC, new InternetAddress(address));
		}
		
		Transport.send(message);
	}
	
	public static void send(String to, String subject, HashMap<String, String> testResult) throws MessagingException {
		send(from, to, subject, testResult);
	}
	
	public static void send(String subject, HashMap<String, String> testResult) throws MessagingException {
		send(from, to, subject, testResult);
	}
	
	private static List<String> getAddress(String address) {
		List<String> addressList = new ArrayList<String>();
		
		if (address.isEmpty())
			return addressList;
		
		if (address.indexOf(";") > 0) {
			String[] addresses = address.split(";");
			
			for (String a : addresses) {
				addressList.add(a);
			}
		} else {
			addressList.add(address);
		}
		
		return addressList;
	}

}