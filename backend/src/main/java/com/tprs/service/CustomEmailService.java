package com.tprs.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Sends custom branded HTML emails for account actions.
 */
public class CustomEmailService {

    private static final String TEMPLATE_PATH = "email-templates/account-action-email.html";

    private final String smtpHost;
    private final String smtpPort;
    private final String smtpUsername;
    private final String smtpPassword;
    private final boolean smtpAuth;
    private final boolean startTls;
    private final String fromEmail;
    private final String fromName;

    public CustomEmailService(Properties props) {
        this.smtpHost = props.getProperty("mail.smtp.host", "").trim();
        this.smtpPort = props.getProperty("mail.smtp.port", "587").trim();
        this.smtpUsername = props.getProperty("mail.smtp.username", "").trim();
        this.smtpPassword = props.getProperty("mail.smtp.password", "").trim();
        this.smtpAuth = Boolean.parseBoolean(props.getProperty("mail.smtp.auth", "true"));
        this.startTls = Boolean.parseBoolean(props.getProperty("mail.smtp.starttls.enable", "true"));
        this.fromEmail = props.getProperty("mail.from.email", "").trim();
        this.fromName = props.getProperty("mail.from.name", "TPRS").trim();
    }

    public void sendVerificationEmail(String toEmail, String firstName, String actionUrl) throws Exception {
        Map<String, String> values = new HashMap<>();
        values.put("SUBJECT_TITLE", "Verify Your Email");
        values.put("GREETING_NAME", normalizeName(firstName));
        values.put("BODY_TEXT", "Thanks for creating your TPRS account. Please verify your email address to activate your access.");
        values.put("BUTTON_TEXT", "Verify Email");
        values.put("ACTION_URL", actionUrl);
        values.put("ALT_TEXT", "If the button does not work, copy and paste this link into your browser:");

        sendHtml(toEmail, "Verify your email - TPRS", renderTemplate(values));
    }

    public void sendResetPasswordEmail(String toEmail, String firstName, String actionUrl) throws Exception {
        Map<String, String> values = new HashMap<>();
        values.put("SUBJECT_TITLE", "Reset Your Password");
        values.put("GREETING_NAME", normalizeName(firstName));
        values.put("BODY_TEXT", "We received a request to reset your password. Use the button below to choose a new password.");
        values.put("BUTTON_TEXT", "Reset Password");
        values.put("ACTION_URL", actionUrl);
        values.put("ALT_TEXT", "If the button does not work, copy and paste this link into your browser:");

        sendHtml(toEmail, "Reset your password - TPRS", renderTemplate(values));
    }

    private String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "there";
        }
        return name.trim();
    }

    private void sendHtml(String toEmail, String subject, String htmlBody) throws MessagingException {
        validateSmtpConfig();

        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", smtpHost);
        mailProps.put("mail.smtp.port", smtpPort);
        mailProps.put("mail.smtp.auth", String.valueOf(smtpAuth));
        mailProps.put("mail.smtp.starttls.enable", String.valueOf(startTls));

        Session session;
        if (smtpAuth) {
            session = Session.getInstance(mailProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUsername, smtpPassword);
                }
            });
        } else {
            session = Session.getInstance(mailProps);
        }

        Message message = new MimeMessage(session);
        InternetAddress fromAddress = new InternetAddress(fromEmail);
        if (fromName != null && !fromName.isBlank()) {
            try {
                fromAddress.setPersonal(fromName);
            } catch (UnsupportedEncodingException ignored) {
                // Fallback to plain address if personal name encoding is not supported.
            }
        }
        message.setFrom(fromAddress);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setContent(htmlBody, "text/html; charset=UTF-8");

        Transport.send(message);
    }

    private String renderTemplate(Map<String, String> values) throws IOException {
        String template = loadTemplate(TEMPLATE_PATH);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }

    private String loadTemplate(String resourcePath) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Template not found: " + resourcePath);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void validateSmtpConfig() {
        if (smtpHost.isEmpty() || fromEmail.isEmpty()) {
            throw new IllegalStateException("SMTP configuration is missing. Set mail.smtp.host and mail.from.email in db.properties.");
        }
        if (smtpAuth && (smtpUsername.isEmpty() || smtpPassword.isEmpty())) {
            throw new IllegalStateException("SMTP auth is enabled but mail.smtp.username or mail.smtp.password is missing.");
        }
    }
}
