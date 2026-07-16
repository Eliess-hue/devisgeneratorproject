package fr.devisgenerator.devisgenerator.service.email;

import fr.devisgenerator.devisgenerator.dto.email.EmailAttachment;

public interface EmailSender {

    void send(
            String to,
            String subject,
            String html,
            EmailAttachment attachment
    );

}