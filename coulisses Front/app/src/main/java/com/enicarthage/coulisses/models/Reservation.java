package com.enicarthage.coulisses.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Reservation implements Parcelable {

    @SerializedName("id")
    private Long id;

    @SerializedName("idClient")
    private Long idClient;

    @SerializedName("quantite")
    private Long quantite;

    @SerializedName("billet")
    private Billet billet; // Billet est une autre classe que vous devez aussi définir (Parcelable)

    public Reservation() {
    }

    public Reservation(Long id, Long idClient, Long quantite, Billet billet) {
        this.id = id;
        this.idClient = idClient;
        this.quantite = quantite;
        this.billet = billet;
    }

    protected Reservation(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            idClient = null;
        } else {
            idClient = in.readLong();
        }
        if (in.readByte() == 0) {
            quantite = null;
        } else {
            quantite = in.readLong();
        }
        billet = in.readParcelable(Billet.class.getClassLoader());
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        @Override
        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        if (idClient == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(idClient);
        }
        if (quantite == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(quantite);
        }
        parcel.writeParcelable(billet, i);
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public Long getQuantite() {
        return quantite;
    }

    public void setQuantite(Long quantite) {
        this.quantite = quantite;
    }

    public Billet getBillet() {
        return billet;
    }

    public void setBillet(Billet billet) {
        this.billet = billet;
    }
}
