package com.enicarthage.coulisses.network;

import com.enicarthage.coulisses.models.Reservation;
import com.enicarthage.coulisses.models.ReservationRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiReservation {

    @POST("/api/reservation")
    Call<Reservation> createReservation(@Body ReservationRequest reservation);

    @GET("/api/reservation")
    Call<List<Reservation>> getAllReservations();

    @GET("/api/reservation/{id}")
    Call<Reservation> getReservationById(@Path("id") Long id);

    @GET("/api/reservation/client/{idClient}")
    Call<List<Reservation>> getReservationsByClientId(@Path("idClient") Long idClient);
}
