package com.enicarthage.coulisses.User.Service;

import com.enicarthage.coulisses.User.Model.Guest;
import com.enicarthage.coulisses.User.Repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    @Autowired
    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    // Créer un nouvel invité
    public Guest createGuest(Guest guest) {
        return guestRepository.save(guest);
    }

    // Récupérer un invité par son ID
    public Optional<Guest> getGuestById(Long id) {
        return guestRepository.findById(id);
    }

    // Récupérer tous les invités
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }



}
