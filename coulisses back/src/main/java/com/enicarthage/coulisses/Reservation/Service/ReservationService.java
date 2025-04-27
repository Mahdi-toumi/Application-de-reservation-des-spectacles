package com.enicarthage.coulisses.Reservation.Service;

import com.enicarthage.coulisses.Billet.Models.Billet;
import com.enicarthage.coulisses.Billet.Repository.BilletRepository;
import com.enicarthage.coulisses.Reservation.Model.Reservation;
import com.enicarthage.coulisses.Reservation.Model.ReservationRequest;
import com.enicarthage.coulisses.Reservation.Repository.ReservationRepository;
import com.enicarthage.coulisses.Security.Email.ReservationEmailService;
import com.enicarthage.coulisses.User.Model.Guest;
import com.enicarthage.coulisses.User.Model.User;
import com.enicarthage.coulisses.User.Repository.GuestRepository;
import com.enicarthage.coulisses.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    @Autowired
    private final ReservationRepository reservationRepository;
    @Autowired
    private BilletRepository billetRepository;
    @Autowired
    private final ReservationEmailService reservationEmailService;
    @Autowired
    private final UserRepository userRepository; // déjà existant ?
    @Autowired
    private final GuestRepository guestRepository; // il faut que tu crées GuestRepository


    public Reservation createReservation(ReservationRequest request) {
        Billet billet = billetRepository.findById(request.getIdBillet())
                .orElseThrow(() -> new RuntimeException("Billet not found"));

        if (billet.getNombre() < request.getQuantite()) {
            throw new RuntimeException("Not enough billets available");
        }

        // Diminuer le nombre de billets disponibles
        billet.setNombre(billet.getNombre() - request.getQuantite());
        if (billet.getNombre() - request.getQuantite() == 0) {
            billet.setVendu("Oui");
        }
        billetRepository.save(billet); // Sauvegarder le billet mis à

        Reservation reservation = Reservation.builder()
                .idClient(request.getIdClient())
                .billet(billet)
                .quantite(request.getQuantite())
                .build();

        Optional<User> optionalUser = userRepository.findById(request.getIdClient());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            reservationEmailService.sendReservationConfirmation(
                    user.getEmail(),
                    user.getNom(),
                    user.getPrenom(),
                    billet.getSpectacle().getTitre(),
                    request.getQuantite()
            );
        } else {
            // Sinon, chercher dans Guest
            Guest guest = guestRepository.findById(request.getIdClient())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            reservationEmailService.sendReservationConfirmation(
                    guest.getEmail(),
                    guest.getNom(),
                    guest.getPrenom(),
                    billet.getSpectacle().getTitre(),
                    request.getQuantite()
            );
        }

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getReservationByClientId(Long id) {
        return reservationRepository.findReservationsByIdClient(id);
    }

}
