package com.enicarthage.coulisses.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class Spectacle implements Parcelable {
    private Long id;
    private String titre;
    private String date; // peut rester en String si parsé dans l'adapter
    private BigDecimal heureDebut;
    private BigDecimal duree;
    private int nbSpectateurs;

    @SerializedName("idLieu")
    private Lieu lieu;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("siteWeb")
    private String siteWeb;

    @SerializedName("description")
    private String description;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(BigDecimal heureDebut) {
        this.heureDebut = heureDebut;
    }

    public BigDecimal getDuree() {
        return duree;
    }

    public void setDuree(BigDecimal duree) {
        this.duree = duree;
    }

    public int getNbSpectateurs() {
        return nbSpectateurs;
    }

    public void setNbSpectateurs(int nbSpectateurs) {
        this.nbSpectateurs = nbSpectateurs;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }

    public String getFormattedDate() {
        try {
            // Supposons que la date reçue est sous le format "yyyy-MM-dd"
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date parsedDate = inputFormat.parse(this.date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return date; // En cas d'erreur, retourner la date brute
        }
    }

    public String getFormattedDuree() {
        try {
            double d = duree.doubleValue();
            int heures = (int) d;
            int minutes = (int) ((d - heures) * 60);

            if (heures > 0 && minutes > 0) {
                return String.format(Locale.getDefault(), "%dh %02dm", heures, minutes);
            } else if (heures > 0) {
                return String.format(Locale.getDefault(), "%dh", heures);
            } else {
                return String.format(Locale.getDefault(), "%02dm", minutes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return duree.toString();
        }
    }


    public String getFormattedHeureDebut() {
        try {
            // heureDebut en BigDecimal, ex: 14.5 → 14:30
            int heures = heureDebut.intValue(); // partie entière
            int minutes = (int) ((heureDebut.remainder(BigDecimal.ONE)).multiply(new BigDecimal(60)).intValue());

            return String.format(Locale.getDefault(), "%02d:%02d", heures, minutes);
        } catch (Exception e) {
            e.printStackTrace();
            return heureDebut.toString(); // fallback brut
        }
    }



    protected Spectacle(Parcel in) {
        id = in.readLong();
        titre = in.readString();
        date = in.readString();
        heureDebut = new BigDecimal(in.readString());
        duree = new BigDecimal(in.readString());
        nbSpectateurs = in.readInt();
        lieu = in.readParcelable(Lieu.class.getClassLoader());
        imageUrl = in.readString();
        siteWeb = in.readString();
        description = in.readString();
    }

    public static final Creator<Spectacle> CREATOR = new Creator<Spectacle>() {
        @Override
        public Spectacle createFromParcel(Parcel in) {
            return new Spectacle(in);
        }

        @Override
        public Spectacle[] newArray(int size) {
            return new Spectacle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(titre);
        dest.writeString(date);
        dest.writeString(heureDebut.toString());
        dest.writeString(duree.toString());
        dest.writeInt(nbSpectateurs);
        dest.writeParcelable(lieu, flags);
        dest.writeString(imageUrl);
        dest.writeString(siteWeb);
        dest.writeString(description);
    }
}
