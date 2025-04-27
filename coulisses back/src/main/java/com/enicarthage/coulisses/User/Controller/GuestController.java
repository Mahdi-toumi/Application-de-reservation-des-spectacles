package com.enicarthage.coulisses.User.Controller;

import com.enicarthage.coulisses.User.Model.Guest;
import com.enicarthage.coulisses.User.Service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/guest")
public class GuestController {

    private final GuestService guestService;

    @Autowired
    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    // Créer un invité
    @PostMapping
    public ResponseEntity<Guest> createGuest(@RequestBody Guest guest) {
        Guest createdGuest = guestService.createGuest(guest);
        return new ResponseEntity<>(createdGuest, HttpStatus.CREATED);
    }

    // Récupérer un invité par ID
    @GetMapping("/{id}")
    public ResponseEntity<Guest> getGuestById(@PathVariable Long id) {
        Optional<Guest> guest = guestService.getGuestById(id);
        return guest.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Récupérer tous les invités
    @GetMapping
    public List<Guest> getAllGuests() {
        return guestService.getAllGuests();
    }

}
