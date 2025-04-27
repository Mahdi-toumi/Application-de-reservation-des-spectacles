package com.enicarthage.coulisses.User.Model;

import com.enicarthage.coulisses.Security.DTOs.userDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;


@Entity
@Table(name = "GUEST")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guest {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
@SequenceGenerator(name = "client_seq", sequenceName = "SEQ_IDCLT", allocationSize = 1)
@Column(name = "IDGUEST ")
private Long id;

@Column(name = "NOM", nullable = false)
private String nom;

@Column(name = "PRENOM", nullable = false)
private String prenom;

@Column(name = "tel", nullable = false)
private String tel;

@Column(name = "EMAIL", nullable = false, unique = true)
private String email;


public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public String getNom() {
    return nom;
}

public void setNom(String nom) {
    this.nom = nom;
}

public String getPrenom() {
    return prenom;
}

public void setPrenom(String prenom) {
    this.prenom = prenom;
}

public String getTel() {
    return tel;
}

public void setTel(String tel) {
    this.tel = tel;
}

public String getEmail() {
    return email;
}

public void setEmail(String email) {
    this.email = email;
}



}

