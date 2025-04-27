package com.enicarthage.coulisses.network;


import com.enicarthage.coulisses.models.Guest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiGuest {

    // Endpoint to get a list of guests
    @GET("api/guest")
    Call<List<Guest>> getAllGuests();

    // Endpoint to get a single guest by id
    @GET("api/guest/{id}")
    Call<Guest> getGuestById(@Path("id") Long id);

    // Endpoint to create a new guest
    @POST("api/guest")
    Call<Guest> createGuest(@Body Guest guest);

    // Endpoint to update an existing guest
    @PUT("api/guest/{id}")
    Call<Guest> updateGuest(@Path("id") Long id, @Body Guest guest);
}
