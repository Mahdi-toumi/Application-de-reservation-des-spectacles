package com.enicarthage.coulisses.Security.Email;

import com.enicarthage.coulisses.Security.Email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationEmailService {

    private final EmailService emailService;

    public void sendReservationConfirmation(String toEmail, String nom, String prenom, String titreSpectacle, int quantite) {
        String subject = "Confirmation de votre réservation 🎭";
        String text = "Bonjour " + prenom + " " + nom + ",\n\n" +
                "Votre réservation pour le spectacle \"" + titreSpectacle + "\" a bien été prise en compte.\n" +
                "Quantité de billets réservés : " + quantite + ".\n\n" +
                "Merci de votre confiance et à très bientôt sur Coulisses !\n\n" +
                "L'équipe Coulisses.";
        emailService.sendEmail(toEmail, subject, text);
    }
}
