package com.enicarthage.coulisses.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.models.Spectacle;
import android.provider.CalendarContract;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class DetailSpectacleActivity extends BaseActivity {

    private ImageView imageView;
    private TextView titre, date, heure, lieu, description, duree ,prixMin , spectateur;
    private Button btnReserve;
    private Double lat = null ;
    private Double lng = null ;
    private Date eventDate;




    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_spectacle);

        imageView = findViewById(R.id.detailImage);
        titre = findViewById(R.id.detailTitre);
        date = findViewById(R.id.detailDate);
        heure = findViewById(R.id.detailHeure);
        lieu = findViewById(R.id.detailLieu);
        spectateur = findViewById(R.id.detailSpectateurs);
        description = findViewById(R.id.detailDescription);
        duree = findViewById(R.id.detailDuree);
        btnReserve = findViewById(R.id.btnReserver);
        prixMin = findViewById(R.id.detailPrice) ;

        setupUserMenu();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Utilise l'id correct pour le bouton

        Spectacle spectacle = getIntent().getParcelableExtra("spectacle");



        if (spectacle != null) {
            titre.setText(spectacle.getTitre());
            date.setText("Date : " + spectacle.getFormattedDate());
            heure.setText("Heure : " + spectacle.getFormattedHeureDebut() + "h");
            lieu.setText("Lieu : " + spectacle.getLieu().getNom());
            description.setText(spectacle.getDescription());
            duree.setText("Duree : " + spectacle.getFormattedDuree());
            spectateur.setText("Spectateurs : " + spectacle.getNbSpectateurs());

            Bundle bundle = getIntent().getBundleExtra("prixMinMapBundle");
            Map<Long, Double> prixMinMap = new HashMap<>();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    try {
                        Long id = Long.parseLong(key);
                        double prix = bundle.getDouble(key);
                        prixMinMap.put(id, prix);
                    } catch (NumberFormatException e) {
                        Log.e("PRIX_MAP", "Error parsing key: " + key);
                    }
                }
            }
            Double prix = prixMinMap.get(spectacle.getId());
            prixMin.setText(prix != null ? String.format("%.2f DT", prix) : "Prix non disponible");







            Glide.with(this)
                    .load(spectacle.getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);

            // Set the clickable action for the title to navigate to the website
            titre.setOnClickListener(v -> {
                String url = spectacle.getSiteWeb();
                if (!url.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Aucun site web disponible", Toast.LENGTH_SHORT).show();
                }
            });

            // Setup for the reservation button
            btnReserve.setOnClickListener(v -> {
                if (spectacle != null) {
                    Intent intent = new Intent(DetailSpectacleActivity.this, BilletSelectionActivity.class);
                    intent.putExtra("spectacle", spectacle);  // On passe l'objet spectacle à BilletSelectionActivity
                    startActivity(intent);
                } else {
                    Toast.makeText(DetailSpectacleActivity.this, "Spectacle non disponible", Toast.LENGTH_SHORT).show();
                }
            });


            Log.d("Cordinate",spectacle.getLieu().getAdresse()) ;

            if (spectacle.getLieu() != null && spectacle.getLieu().getPositionGps() != null) {
                try {
                    String[] coordinates = spectacle.getLieu().getPositionGps().split(",");

                    if (coordinates.length == 2) {
                        lat = Double.parseDouble(coordinates[0].trim());
                        lng = Double.parseDouble(coordinates[1].trim());
                        Log.d("COORDINATES", "Lat: " + lat + ", Lng: " + lng);
                    }
                } catch (NumberFormatException e) {
                    Log.e("COORDINATES", "Erreur parsing: " + e.getMessage());
                }
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
            eventDate = formatter.parse(spectacle.getDate());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




        CardView locationCard = findViewById(R.id.cardlocation);
        locationCard.setOnClickListener(v -> {
            if (lat != null && lng != null) {
                Log.d("MAPS", "Ouverture maps avec: " + lat + "," + lng);
                openGoogleMaps(lat, lng);
            } else {
                Log.e("MAPS", "Coordonnées non valides");
                Toast.makeText(this, "Localisation non disponible", Toast.LENGTH_SHORT).show();
            }
        });

        CardView dateCard = findViewById(R.id.cardDate);
        dateCard.setOnClickListener(v -> {
            if (eventDate != null) {
                addToCalendar(spectacle);
            } else {
                Toast.makeText(this, "Date non disponible", Toast.LENGTH_SHORT).show();
            }
        });

    }


    // Ajoutez cette méthode
    private void openGoogleMaps(double latitude, double longitude) {
        try {
            // Intent pour Google Maps
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f", latitude, longitude, latitude, longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");

            // Vérifier si Google Maps est installé
            PackageManager pm = getPackageManager();
            if (intent.resolveActivity(pm) != null) {
                startActivity(intent);
            } else {
                // Fallback : ouvrir dans navigateur web
                String url = String.format(Locale.ENGLISH, "https://www.google.com/maps/search/?api=1&query=%f,%f", latitude, longitude);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Aucune application de cartes installée", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToCalendar(Spectacle spectacle) {
        try {
            // Parse the date (assuming spectacle.getDate() returns a String in "yyyy-MM-dd" format)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date eventDate = dateFormat.parse(spectacle.getDate());

            if (eventDate == null) {
                Toast.makeText(this, "Date format invalide", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert heureDebut (BigDecimal) to hours and minutes
            BigDecimal heureDebut = spectacle.getHeureDebut();
            int hours = heureDebut.intValue();  // Gets the integer part (hours)
            BigDecimal minutesDecimal = heureDebut.subtract(new BigDecimal(hours))
                    .multiply(new BigDecimal(60));
            int minutes = minutesDecimal.intValue();

            // Set begin time
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(eventDate);
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            long beginTime = calendar.getTimeInMillis();

            // Calculate end time (duration in minutes)
            long durationMillis = spectacle.getDuree()
                    .multiply(new BigDecimal(60000))
                    .longValue();
            long endTime = beginTime + durationMillis;

            // Create calendar event intent
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, spectacle.getTitre())
                    .putExtra(CalendarContract.Events.DESCRIPTION, spectacle.getDescription())
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, spectacle.getLieu().getNom())
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                offerToInstallCalendarApp();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erreur de création d'événement", Toast.LENGTH_SHORT).show();
            Log.e("CALENDAR", "Error adding event", e);
        }
    }

    private void offerToInstallCalendarApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Application de calendrier requise")
                .setMessage("Voulez-vous installer Google Calendar ?")
                .setPositiveButton("Installer", (dialog, which) -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=com.google.android.calendar")));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.calendar")));
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

}
