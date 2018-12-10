package com.embraiz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.embraiz.model.MessageLog;
import com.embraiz.service.BaseService;
import com.sun.mail.util.MailSSLSocketFactory;

@Component
public class SendMail {

	private static SendMail sendMail;

	@Resource
	private BaseService baseService;

	@PostConstruct
	public void init() {
		sendMail = this;
		sendMail.baseService = this.baseService;
	}

	public boolean sendMail(String email_to, String email_subbject, String email_body) throws IOException {
		Properties propMail = new Properties();

		String path = this.getClass().getResource("/mailInfo.properties").getPath();
		path = path.replaceAll("%20", " ");

		FileInputStream fis = new FileInputStream(path);
		propMail.load(fis);

		String smtp = propMail.getProperty("smtp");
		String userName = propMail.getProperty("userName");
		String password = propMail.getProperty("password");
		String mailFrom = propMail.getProperty("mailForm");
		String nameFrom = propMail.getProperty("nameForm");

		boolean smtpAuth = true;

		if (userName == null || "".equals(userName)) {
			userName = "";
			smtpAuth = false;
		}
		if (password == null) {
			password = "";
		}
		if (mailFrom == null || "".equals(mailFrom)) {

		}

		try {
			JavaMailSenderImpl javaMail = new JavaMailSenderImpl();

			javaMail.setHost(smtp);

			javaMail.setPassword(password);
			javaMail.setUsername(userName);

			// 端口
			if (userName.length() > 0) {
				javaMail.setPort(465);
			}

			Properties prop = new Properties();
			if (smtpAuth) {
				prop.setProperty("mail.smtp.auth", "true");
				prop.setProperty("mail.smtp.ssl.enable", "true");
				prop.setProperty("mail.transport.protocol", "smtps");
			}

			javaMail.setJavaMailProperties(prop);

			MyAuthenticator myauth = new MyAuthenticator(userName, password);
			Session mysession = Session.getInstance(prop, myauth);

			MimeMessage mailMessage = new MimeMessage(mysession);
			MimeMessageHelper msg = new MimeMessageHelper(mailMessage, true, "utf-8");

			if (nameFrom != null && !"".equals(nameFrom)) {
				InternetAddress fromAddress = new InternetAddress(mailFrom, nameFrom);
				msg.setFrom(fromAddress);
			} else {
				msg.setFrom(mailFrom);
			}

			mailMessage.setRecipients(MimeMessage.RecipientType.TO, email_to);
			mailMessage.setHeader("bouceEmailMailId", "bouceEmailMailIdEnd");
			msg.setSubject(email_subbject);

			StringBuffer sb = new StringBuffer();
			sb.append(email_body + "</br>");

			msg.setText(sb.toString(), true);

			javaMail.send(mailMessage);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 自动发送邮件的调用方法
	 * 
	 * @param mailFrom
	 * @param mailTo
	 * @param mailCc
	 * @param mailBcc
	 * @param mailSubject
	 * @param mailContent
	 * @param smtp
	 * @param userName
	 * @param password
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public boolean sendMail(String mailFrom, String mailTo, String mailCc, String mailBcc, String mailSubject, String mailContent, String smtp,
			String userName, String password, Integer port ,String fileName) throws Exception {

		boolean smtpAuth = true;

		if (userName == null || "".equals(userName)) {
			userName = "";
			smtpAuth = false;
		}
		if (password == null) {
			password = "";
		}

		// 记录发送邮件信息
		MessageLog messageLog = new MessageLog();
		messageLog.setAction("sent");
		messageLog.setLogTime(MainUtil.getTime());
		messageLog.setMsgType("email");
		messageLog.setSentBy(1);
		messageLog.setTitle(mailSubject);
		sendMail.baseService.save(messageLog);

		JavaMailSenderImpl javaMail = new JavaMailSenderImpl();

		javaMail.setHost(smtp);

		javaMail.setPassword(password);
		javaMail.setUsername(userName);

		// 端口
		if (userName.length() > 0) {
			javaMail.setPort(port);
		}

		Properties prop = new Properties();
		if (smtpAuth) {
			prop.setProperty("mail.smtp.auth", "true");
			prop.setProperty("mail.smtp.ssl.enable", "true");
			prop.setProperty("mail.transport.protocol", "smtps");
		}

		javaMail.setJavaMailProperties(prop);

		MyAuthenticator myauth = new MyAuthenticator(userName, password);
		Session mysession = Session.getInstance(prop, myauth);

		MimeMessage mailMessage = new MimeMessage(mysession);
		MimeMessageHelper msg = new MimeMessageHelper(mailMessage, true, "utf-8");

		msg.setFrom(mailFrom);

		mailMessage.setRecipients(MimeMessage.RecipientType.TO, mailTo);
		mailMessage.setHeader("bouceEmailMailId", "bouceEmailMailIdEnd");
		msg.setSubject(mailSubject);

		// 增加CC功能
		if (mailCc != null && !("").equals(mailCc)) {
			mailMessage.addRecipients(MimeMessage.RecipientType.CC, mailCc);
		}

		// 增加BCC功能
		if (mailBcc != null && !("").equals(mailBcc)) {
			mailMessage.addRecipients(MimeMessage.RecipientType.BCC, mailBcc);
		}

		if (fileName != null && !("").equals(fileName)) {
			String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("/WEB-INF/classes/", "");
			String mailPath = "/mail_file/";
			FileSystemResource mailFile = new FileSystemResource(new File(filePath + mailPath + fileName));
			msg.addAttachment(MimeUtility.encodeWord(fileName), mailFile);
		}

		StringBuffer sb = new StringBuffer();
		sb.append(mailContent + "</br>");

		msg.setText(sb.toString(), true);

		javaMail.send(mailMessage);

		return true;

	}

	public static void sendMailcc(String mailto, String mailSubject, String mailContent, String url) throws Exception {
		String from = "646818692@qq.com";
		String host = "smtp.qq.com";
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);

		properties.put("mail.smtp.auth", "true");
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.ssl.socketFactory", sf);

		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("646818692@qq.com", "idwwzlqpdvnqbdfh");
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "Embraiz"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailto));
			message.setSubject("" + mailSubject + "");
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent("" + mailContent + "", "text/html;charset=gbk");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			messageBodyPart = new MimeBodyPart();
			String pdfurl = "" + url + "";
			DataSource source = new FileDataSource(pdfurl);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(MimeUtility.encodeText("PDF.pdf"));
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	

}
