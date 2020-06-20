package email.agh.edu.pl;

import java.io.IOException;

public class MailSender {
	private Mail mail;
	private SMTPConnection conn;
	
	public MailSender(String serverName, int serverPort, String login, String password, Mail mail) throws IOException {
		this.mail = mail;
		this.conn = new SMTPConnection(serverName, serverPort, login, password);
	}
	
	public void sendMail() throws IOException, InterruptedException {
		mail.buildMail();
		conn.beginConnection();
		conn.writeToServer("MAIL FROM:<" + mail.getSender() + ">");
		System.out.println(conn.readResponse());
		conn.writeToServer("RCPT TO:<" + mail.getRecipient() + ">");
		System.out.println(conn.readResponse());
		conn.writeToServer("DATA");
		System.out.println(conn.readResponse());
		conn.writeToServer(mail.getEncoded());
		conn.writeToServer(".");
		System.out.println(conn.readResponse());
		conn.writeToServer("QUIT");
		System.out.println(conn.readResponse());
	}
}
