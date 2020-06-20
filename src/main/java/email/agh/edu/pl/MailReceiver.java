package email.agh.edu.pl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailReceiver {
	private IMAPConnection conn; 
	
	public MailReceiver(String serverName, int serverPort, String login, String password) throws IOException {
		this.conn = new IMAPConnection(serverName, serverPort, login, password);
	}
	
	public List<String> getMailboxes() throws IOException, InterruptedException {
		conn.beginConnection();
		String response;
		conn.writetoServer("1 LIST \"\"*");
		response = conn.readResponse();

		List<String> mailboxes = new ArrayList<String>();
		Matcher m = Pattern.compile("\\*.*\"/\" .*").matcher(response);
		while (m.find()) {
			mailboxes.add(m.group().substring(26));
		}

		conn.closeConnection();
		return mailboxes;
	}
	
	public String getMailSubjects(String mailbox) throws IOException, InterruptedException {
		conn.beginConnection();
		String response;
		conn.writetoServer("1 SELECT " + mailbox);
		response = conn.readResponse();
		conn.writetoServer("1 FETCH 1:* (BODY[HEADER.FIELDS (SUBJECT)])");
		response = conn.readResponse();
		conn.closeConnection();
		return response;
	}
	
	public Mail getEmail(String mailbox, int id) throws Exception {
		conn.beginConnection();
		String response;
		conn.writetoServer("1 SELECT " + mailbox);
		conn.writetoServer("1 FETCH " + id + " RFC822");
		response = conn.readResponse();
		//System.out.println(response);
		Mail mail = new Mail(id, response);
		mail.decodeMail();
		return mail;
	}
}
