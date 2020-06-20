package email.agh.edu.pl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class Mail implements Serializable {
	private long id;
	private String sender;
	private String senderName;
	private String recipient;
	private String ccRecipient;
	private String bccRecipient;
	private String subject;
	private String date;
	private String text;
	private String buildText;


	private String encodedText;
	private File attachment;


	public Mail(int id) {
		this.id = id;
	}

	public Mail(int id, String encodedText) {
		this.id = id;
		this.encodedText = encodedText;
	}
	public String getEncoded() {
		return encodedText;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getCcRecipent() {
		return ccRecipient;
	}

	public String getBccRecipent() {
		return bccRecipient;
	}

	public void setCcRecipent(String ccRecipent) {
		this.ccRecipient = ccRecipent;
	}

	public void setBccRecipent(String bccRecipent) {
		this.bccRecipient = bccRecipent;
	}


	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean hasAttachment() {
		return attachment == null;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public void setAttachment(File attachment) {
		this.attachment = attachment;
	}

	public long getId() {
		return id;
	}
	
	public String getEncodedMail() {
		return encodedText;
	}

	@Override
	public String toString() {
		return "Mail " + id + "\r\nNadawca: " + sender + "\r\nTemat: " + subject + "\r\nData: " + date + "\r\nTreœæ: " + text;
	}

	public void buildMail() throws FileNotFoundException, IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("Date: ").append(DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)))
				.append("\n");
		sb.append("To: ").append(recipient).append("\n");
		sb.append("Cc: ").append(ccRecipient).append("\n");
		sb.append("Bcc: ").append(bccRecipient).append("\n");
		sb.append("From: ").append(senderName).append(" <").append(sender).append(">\n");
		sb.append("Subject: ").append(subject).append("\n");
		sb.append("MIME-Version: 1.0\n");
		sb.append("Content-Type: multipart/mixed; boundary=\"" + "BOUND" + "\"\n\n");
		sb.append("--" + "BOUND" + "\n");

		sb.append("\n");
		sb.append("--" + "BOUND" + "\n");
		sb.append("Content-Transfer-Encoding: 8bit\n");
		sb.append("Content-Type: text/html;\n\n");
		sb.append(text).append("\n\n");

		if (!hasAttachment()) {
			String attachmentName = attachment.getName();
			sb.append("--" + "BOUND" + "\n");
			sb.append(String.format("Content-Type: image/png; name=\"%s\"\n", attachmentName));
			sb.append(String.format("Content-Description: %s\n", attachmentName));
			sb.append(String.format("Content-Disposition: attachment; filename=\"%s\"\n", attachmentName));
			sb.append("Content-Transfer-Encoding: BASE64\n\n");
			sb.append(encodeAttachment());
		}

		this.buildText = sb.toString();

	}


	private String encodeAttachment() throws FileNotFoundException, IOException {
		try (InputStream is = new FileInputStream(attachment)) {
			long length = attachment.length();
			byte[] bytes = new byte[(int) length];
			int offset = 0;
			int numRead;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file " + attachment.getName());
			}

			return Base64.getEncoder().encodeToString(bytes);
		}
	}

	
	public void decodeMail() throws Exception {
		MailDecoder md = new MailDecoder(encodedText);
		
		this.sender = md.extractSender();
		this.date = md.extractDate();
		this.ccRecipient = md.extractCc();
		this.subject = md.extractSubject();
		this.text = md.extractText();
	}
	
}
