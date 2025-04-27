package com.enicarthage.coulisses.Security.Email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String subject = "Réinitialisation de votre mot de passe";
        String text = "Pour réinitialiser votre mot de passe, Entrer ce code dans l'application " ;

        sendEmail(toEmail, subject, text);
    }

    @Override
    public void sendRegistrationEmail(String toEmail, String nom, String prenom) {
        String subject = "Bienvenue sur Coulisses 🎉";
        String text = "Bonjour " + prenom + " " + nom + ",\n\n" +
                "Votre compte a bien été créé sur l'application Coulisses.\n" +
                "Merci pour votre inscription ! Nous sommes ravis de vous avoir parmi nous.\n\n" +
                "À très bientôt,\n" +
                "L'équipe Coulisses.";
        sendEmail(toEmail, subject, text);
    }


    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@votredomaine.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
