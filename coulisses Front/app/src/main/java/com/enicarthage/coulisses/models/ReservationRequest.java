package com.enicarthage.coulisses.models;

public class ReservationRequest {

    private Long idClient;
    private Long quantite;

    private Long idBillet;

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public ReservationRequest(Long idClient, Long quantite, Long idBillet) {
        this.idClient = idClient;
        this.quantite = quantite;
        this.idBillet = idBillet;
    }

    public ReservationRequest() {
    }

    public Long getQuantite() {
        return quantite;
    }

    public void setQuantite(Long quantite) {
        this.quantite = quantite;
    }

    public Long getIdBillet() {
        return idBillet;
    }

    public void setIdBillet(Long idBillet) {
        this.idBillet = idBillet;
    }
}