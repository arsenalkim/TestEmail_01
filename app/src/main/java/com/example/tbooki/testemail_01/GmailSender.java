package com.example.tbooki.testemail_01;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Tbooki on 2015-02-17.
 */
public class GmailSender extends javax.mail.Authenticator {
    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final String SMTP_HOST_PORT = "465";     // SMTPS (over SSL)

    private final String user;
    private final String password;
    private final Session session;

    public GmailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtps");

        props.setProperty("mail.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", SMTP_HOST_PORT);
//		props.put("mail.smtp.port", "465");
//		props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.port", SMTP_HOST_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized boolean sendMail(String sender, String recipients, String subject, String body) throws Exception {
        MimeMessage message = new MimeMessage(session);

        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
		message.setText(body);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        message.setDataHandler(handler);
        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
        Transport.send(message);
        return true;
    }

    public class ByteArrayDataSource implements DataSource {
        private final byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        @Override
        public String getName() {
            return "ByteArrayDataSource";
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}