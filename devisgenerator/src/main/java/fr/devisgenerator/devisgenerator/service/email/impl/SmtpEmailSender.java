package fr.devisgenerator.devisgenerator.service.email.impl;

import fr.devisgenerator.devisgenerator.dto.email.EmailAttachment;
import fr.devisgenerator.devisgenerator.exception.EmailSendingException;
import fr.devisgenerator.devisgenerator.service.email.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Profile({"dev", "test", "docker"})
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Override
    public void send(
            String to,
            String subject,
            String html,
            EmailAttachment attachment
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            StandardCharsets.UTF_8.name()
                    );

            helper.setTo(
                    to
            );

            helper.setSubject(
                    subject
            );

            helper.setText(
                    html,
                    true
            );

            helper.addAttachment(
                    attachment.filename(),
                    new ByteArrayResource(
                            attachment.content()
                    )
            );

            mailSender.send(
                    message
            );

        } catch (
                MessagingException |
                MailException e
        ) {

            throw new EmailSendingException(
                    "Unable to send email via SMTP",
                    e
            );

        }

    }

}