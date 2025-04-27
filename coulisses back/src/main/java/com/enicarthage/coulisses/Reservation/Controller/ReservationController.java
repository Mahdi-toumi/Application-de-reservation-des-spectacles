package com.enicarthage.coulisses.Reservation.Controller;

import com.enicarthage.coulisses.Reservation.Model.Reservation;
import com.enicarthage.coulisses.Reservation.Model.ReservationRequest;
import com.enicarthage.coulisses.Reservation.Service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // Créer une réservation
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest reservationRequest) {
        Reservation reservation = reservationService.createReservation(reservationRequest);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    // Récupérer toutes les réservations
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    // Récupérer une réservation par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{idClient}")
    public ResponseEntity<List<Reservation>> getReservationsByClientId(@PathVariable Long idClient) {
        List<Reservation> reservations = reservationService.getReservationByClientId(idClient);
        return ResponseEntity.ok(reservations);
    }
}
