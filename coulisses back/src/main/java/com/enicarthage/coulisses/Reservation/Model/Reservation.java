package com.enicarthage.coulisses.Reservation.Model;


import com.enicarthage.coulisses.Billet.Models.Billet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RESERVATION")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_seq")
    @SequenceGenerator(name = "reservation_seq", sequenceName = "SEQ_RES", allocationSize = 1)
    @Column(name = "IDRESER ")
    private Long id;

    @Column(name = "IDCLINET ")
    private Long idClient;

    @ManyToOne
    @JoinColumn(name = "IDBILLET", nullable = false)
    private Billet billet;

    @Column(name = "QUANTITE ")
    private int quantite;



}
