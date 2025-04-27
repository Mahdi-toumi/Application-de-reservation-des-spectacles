package com.enicarthage.coulisses.network;

import com.enicarthage.coulisses.models.Spectacle;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SpectacleApi {

    // Tous les spectacles
    @GET("api/spectacle")
    Call<List<Spectacle>> getAllSpectacles();

    // Détail d’un spectacle
    @GET("api/spectacle/{spectacleId}")
    Call<Spectacle> getSpectacleById(@Path("spectacleId") Long id);

    @GET("api/spectacle/featured")
    Call<List<Spectacle>> getFeaturedSpectacles();

    @GET("api/spectacle/prixmin/{spectacleId}")
    Call<Double> getBilletPrixMin(@Path("spectacleId") Long spectacleId);



}