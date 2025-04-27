package com.enicarthage.coulisses.Reservation.Repository;

import com.enicarthage.coulisses.Lieu.Models.Lieu;
import com.enicarthage.coulisses.Reservation.Model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    public List<Reservation> findReservationsByIdClient(Long idClient) ;
}
