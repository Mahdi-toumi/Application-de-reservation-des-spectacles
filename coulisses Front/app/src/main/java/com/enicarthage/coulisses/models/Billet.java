package com.enicarthage.coulisses.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Billet implements Parcelable {
    private Long id;
    private String categorie;
    private Double prix;
    private Spectacle spectacle;
    private String vendu;

    private int nombre ;

    public int getNombre() {
        return nombre;
    }

    public void setNombre(int nombre) {
        this.nombre = nombre;
    }

    public Billet() {
        // Constructeur vide
    }

    protected Billet(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        categorie = in.readString();
        if (in.readByte() == 0) {
            prix = null;
        } else {
            prix = in.readDouble();
        }
        spectacle = in.readParcelable(Spectacle.class.getClassLoader());
        vendu = in.readString();
        nombre = in.dataAvail();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(categorie);
        if (prix == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(prix);
        }
        dest.writeParcelable(spectacle, flags);
        dest.writeString(vendu);
        dest.writeInt(nombre);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Billet> CREATOR = new Creator<Billet>() {
        @Override
        public Billet createFromParcel(Parcel in) {
            return new Billet(in);
        }

        @Override
        public Billet[] newArray(int size) {
            return new Billet[size];
        }
    };

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Spectacle getSpectacle() {
        return spectacle;
    }

    public void setSpectacle(Spectacle spectacle) {
        this.spectacle = spectacle;
    }

    public String getVendu() {
        return vendu;
    }

    public void setVendu(String vendu) {
        this.vendu = vendu;
    }
}
