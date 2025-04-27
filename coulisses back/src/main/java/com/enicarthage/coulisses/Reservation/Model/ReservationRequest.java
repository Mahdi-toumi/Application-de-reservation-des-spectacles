package com.enicarthage.coulisses.Reservation.Model;


import lombok.Data;

@Data
public class ReservationRequest {
    private Long idClient;
    private Long idBillet;
    private int quantite;
}
