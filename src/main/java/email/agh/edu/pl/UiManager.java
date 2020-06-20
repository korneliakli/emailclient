package email.agh.edu.pl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class UiManager {

	public void buildUI() throws NumberFormatException, Exception {
		boolean condition = true;
		while (condition) {
			System.out.println("MAIL CLIENT");
			System.out.println("Wyslij poczte, wpisz: 1");
			System.out.println("Odbierz poczte, wpisz: 2");
			System.out.println("Wyjscie, wpisz: 3");
			Scanner scanner = new Scanner(System.in);
			int choice = scanner.nextInt();
			switch (choice) {
			case 1:
				buildMail();
				break;
			case 2:
				readMail();
				break;
			case 3:
				condition = false;
				break;
			default:
				System.out.println("Wybrano nieznane polecenie");
				break;
			}
		}

		System.out.println("Zamykam...");
		Thread.sleep(500);
		System.exit(0);
	}

	private void buildMail() throws IOException, InterruptedException {
		String condition = "t";
		Mail mail = new Mail(1);
		MailSender ms = new MailSender("smtp.poczta.onet.pl", 587, "test.ask@onet.pl", "AskAgh1", mail);
		Scanner scanner = new Scanner(System.in);
		mail.setSender("test.ask@onet.pl");
		while (condition.equals("t")) {
			System.out.println("Podaj swoje imieô: ");
			mail.setSenderName(scanner.nextLine());
			System.out.println("Podaj odbiorce: ");
			mail.setRecipient(scanner.nextLine());
			System.out.println("Podaj odbiorce CC: ");
			mail.setCcRecipent(scanner.nextLine());
			System.out.println("Podaj odbiorce BCC: ");
			mail.setBccRecipent(scanner.nextLine());
			System.out.println("Podaj temat: ");
			mail.setSubject(scanner.nextLine());
			System.out.println("Wpisz tresc: ");
			mail.setText(scanner.nextLine());
			System.out.println("Dodac zalacznik(t/n)?");
			if (scanner.nextLine().equals("t")) {
				System.out.println("Podaj nazwe zalacznika");
				File attach = new File(scanner.nextLine());
				mail.setAttachment(attach);
			}
			System.out.println("Chcesz edytowac?(t/n)");
			condition = scanner.nextLine();
		}
		System.out.println(
				"Wysy≈Çam maila do " + mail.getRecipient() + " " + mail.getCcRecipent() + " " + mail.getBccRecipent());
		ms.sendMail();
	}

	private void readMail() throws NumberFormatException, Exception {
		MailReceiver mr = new MailReceiver("imap.poczta.onet.pl", 143, "test.ask@onet.pl", "AskAgh1");
		Scanner scanner = new Scanner(System.in);
		System.out.println("Wybierz skrzynke:");
		ArrayList<String> mailboxes = (ArrayList<String>) mr.getMailboxes();
		for (String mb : mailboxes) {
			System.out.println(mb);
		}

		String mailbox = scanner.nextLine();
		while (!mailboxes.contains(mailbox)) {
			System.out.println("Nieprawidlowa nazwa skrzynki, wybierz ponownie: ");
			mailbox = scanner.nextLine();
		}
		System.out.println("Wybierz mail do wyswietlenia:");
		System.out.println(mr.getMailSubjects(mailbox));

		String mailID = scanner.nextLine();
		
		System.out.println(mr.getEmail(mailbox, Integer.parseInt(mailID)));

	}

}
