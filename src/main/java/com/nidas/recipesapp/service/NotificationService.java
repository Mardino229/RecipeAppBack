package com.nidas.recipesapp.service;

import com.nidas.recipesapp.model.Validation;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificationService {

    //JavaMailSender javaMailSender;

    public void envoyer(Validation validation) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("no-Reply@nidas.tech");
        mailMessage.setTo(validation.getChief().getEmail());
        mailMessage.setSubject("Votre code d'activation");

        String texte = String.format("Bonjour %s , <br /> Votre code d'activation est : %s",
                validation.getChief().getUsername(),
                validation.getCode());
        mailMessage.setText(texte);

        //javaMailSender.send(mailMessage);
    }
}
