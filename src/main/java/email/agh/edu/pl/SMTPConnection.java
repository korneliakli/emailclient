package email.agh.edu.pl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;


public class SMTPConnection {
	
	private String serverName;
	private int serverPort;
	private String login;
	private String password;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;

	
	public SMTPConnection(String serverName, int serverPort, String login, String password) throws IOException {
		this.serverName = serverName;
		this.serverPort = serverPort;
		this.login = login;
		this.password = password;
		
		this.socket = new Socket(serverName, serverPort);
		this.writer = new PrintWriter(socket.getOutputStream(), true);
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}
	
		
	public void beginConnection() throws IOException, InterruptedException, ConnectException {
		String response;
		writeToServer("HELO mailclient.agh.edu.pl");
		response = readResponse();
		System.out.println(response);
		writeToServer("AUTH LOGIN");
		response = readResponse();
		System.out.println(response);
		writeToServer(Base64.getEncoder().encodeToString(login.getBytes()));
		response = readResponse();
		System.out.println(response);
		writeToServer(Base64.getEncoder().encodeToString(password.getBytes()));
		response = readResponse();
		System.out.println(response);
		if(response.startsWith("235")) {
			System.out.println("Succesful"); 
		}
		else {
			throw new ConnectException("Authentication Failed");
		}
		
	}
	
	public void writeToServer(String message) {
		writer.println(message);
	}
	
	private void checkResponse(String response) throws ConnectException {
		if (response.startsWith("4") || response.startsWith("5")) {
			throw new ConnectException("Response error: " + response);
		}
	}
	
	public String readResponse() throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append(reader.readLine()); 
        Thread.sleep(100);
        while(reader.ready())
            sb.append("\n").append(reader.readLine());
        Thread.sleep(100);
        checkResponse(sb.toString());
        return sb.toString();
    }
	
	public void closeConnection() throws IOException {
		socket.close();
	}
}
