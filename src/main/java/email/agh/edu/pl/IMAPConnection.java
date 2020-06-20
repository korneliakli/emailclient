package email.agh.edu.pl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class IMAPConnection {
	private String serverName;
	private int serverPort;
	private String login;
	private String password;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;

	public IMAPConnection(String serverName, int serverPort, String login, String password) throws IOException {
		this.serverName = serverName;
		this.serverPort = serverPort;
		this.login = login;
		this.password = password;

		this.socket = new Socket(serverName, serverPort);
		this.writer = new PrintWriter(socket.getOutputStream(), true);
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void writetoServer(String message) {
		writer.println(message);
	}

	public String readResponse() throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append(reader.readLine()); 
        Thread.sleep(100);
        while(reader.ready())
            sb.append("\n").append(reader.readLine());
        Thread.sleep(100);
        return sb.toString();
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void beginConnection() throws IOException, InterruptedException {
		String response;
		writetoServer("1 login " + login + " " + password);
		response = readResponse();
		System.out.println(response);
	}
	
	public void closeConnection() throws IOException {
		socket.close();
	}
}
