package email.agh.edu.pl;

import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.invoke.StringConcatException;
import java.util.Base64;
import java.util.List;

public class MailDecoder {

	private String content;
	private String boundary;
	private boolean hasBoundary;

	public MailDecoder(String content) {
		this.content = content;
		this.hasBoundary = false;
		extractBoundary();
	}

	private void extractBoundary() {
		for (String line : content.split("\n")) {
			if (line.contains("Content-Type: multipart/alternative; boundary=")) {
				boundary = StringUtils.substringsBetween(line, "boundary=\"", "\"")[0];
				hasBoundary = true;

			}
		}
	}

	public String extractSender() throws Exception {
		for (String line : content.split("\n")) {
			if (line.contains("From:")) {
				return StringUtils.substringAfter(line, "From: ");
			}
		}
		throw new Exception();
	}

	public String extractSubject() throws Exception {
		for (String line : content.split("\n")) {
			if (line.contains("Subject:")) {
				return StringUtils.substringAfter(line, "Subject: ");
			}
		}
		throw new Exception();
	}

	public String extractText() throws Exception {
		if (hasBoundary) {
			String boundary = "--" + this.boundary;
			for (String line : content.split("\n")) {
				if (line.contains("Content-Type: text/plain;")) {
					String text = StringUtils.substringBetween(content, "Content-Type: text/plain; charset=\"UTF-8\"",
							boundary);
					if (text.contains("Content-Transfer-Encoding: quoted-printable"))
						return StringUtils.substringAfter(text, "Content-Transfer-Encoding: quoted-printable");
					else if (text.contains("Content-Transfer-Encoding: base64")) {
						String encoded = StringUtils.substringAfter(text, "Content-Transfer-Encoding: base64");
						byte[] encodedText = Base64.getMimeDecoder().decode(encoded.replaceAll("\n", ""));
						return new String(encodedText);
					} else
						return text;
				}
			}
			return "Nie mo�na wy�wietli� tre�ci";
		} else
			return StringUtils.substringBetween(content, "X-ONET_PL-MDA-Spam: NO", ")");
	}

	public String extractCc() {
		String cc = " ";
		for (String line : content.split("\n")) {
			if (line.contains("Cc:")) {
				cc = StringUtils.substringAfter(line, "Cc: ");
			}
		}
		return cc;
	}

	public String extractDate() throws Exception {
		for (String line : content.split("\n")) {
			if (line.contains("Date:")) {
				return StringUtils.substringAfter(line, "Date: ");
			}
		}
		throw new Exception();
	}

	public String extractAttachment() throws Exception {
		String filename = " ";
		String encodedContent = " ";
		for (String line : content.split("\n")) {
			if (line.contains("Content-Disposition: attachment;")) {
				filename = StringUtils.substringBetween(content, "filename=\"", "\"");
				String text = StringUtils.substringAfter(content, "Content-Transfer-Encoding: base64");
				List<String> list = new ArrayList<String>(Arrays.asList(text.split("\n")));
				for (int i = 0; i < 4; i++) {
					list.remove(0);
				}
				encodedContent = String.join("\n", list);
				byte[] encodedText = Base64.getDecoder().decode(encodedContent.replaceAll("\n", ""));
				File file = new File("attachments/", filename);
				FileOutputStream os = new FileOutputStream(file);
				os.write(encodedText);
				return encodedContent;
			}
		}
		throw new Exception();
	}

}