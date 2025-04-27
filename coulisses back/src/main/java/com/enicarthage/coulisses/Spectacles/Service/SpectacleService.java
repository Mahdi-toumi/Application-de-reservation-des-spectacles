package com.enicarthage.coulisses.Spectacles.Service;


import com.enicarthage.coulisses.Billet.Models.Billet;
import com.enicarthage.coulisses.Billet.Repository.BilletRepository;
import com.enicarthage.coulisses.Billet.Service.BilletService;
import com.enicarthage.coulisses.Spectacles.Models.Spectacle;
import com.enicarthage.coulisses.Spectacles.Repository.SpectacleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpectacleService {

    @Autowired
    private SpectacleRepository spectacleRepository;
    @Autowired
    private BilletRepository billetRepository;

    public List<Spectacle> getAllSpectacles() {
        return spectacleRepository.findAll();
    }

    public Double getMinPrix(Long spectacleId) {
        List<Billet> billets = billetRepository.findAll();
        Double prixMin = null;

        for (Billet billet : billets) {
            if (billet.getSpectacle().getId().equals(spectacleId)) {
                if (prixMin == null || billet.getPrix() < prixMin) {
                    prixMin = billet.getPrix();
                }
            }
        }

        return prixMin != null ? prixMin : 0.0;
    }


    public Spectacle getSpectacleById(Long id) {
        return spectacleRepository.findById(id).orElse(null);
    }

    public List<Spectacle> getFeaturedSpectacles() {
        List<Spectacle> allSpectacles = spectacleRepository.findAll();

        // Trier les spectacles par date décroissante
        allSpectacles.sort((s1, s2) -> s2.getDate().compareTo(s1.getDate()));

        // Prendre les 5 premiers
        List<Spectacle> featuredSpectacles = allSpectacles.stream()
                .limit(7)
                .collect(Collectors.toList());

        return featuredSpectacles;
    }



}


