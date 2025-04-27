package com.enicarthage.coulisses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.enicarthage.coulisses.R;
import com.enicarthage.coulisses.models.Spectacle;
import com.enicarthage.coulisses.util.BilletSelection;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.content.pm.PackageManager;

public class PaymentConfirmationActivity extends AppCompatActivity {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

    private static final int STORAGE_PERMISSION_CODE = 100;

    private Spectacle spectacle;
    private ArrayList<BilletSelection> selectedBillets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        if (getIntent() != null) {
            spectacle = getIntent().getParcelableExtra("spectacle");
            selectedBillets = getIntent().getParcelableArrayListExtra("selected_billets");

            if (spectacle == null || selectedBillets == null) {
                finish(); // Close activity if data is missing
                return;
            }
        } else {
            finish();
            return;
        }
        TextView tvConfirmation = findViewById(R.id.tvConfirmation);
        TextView tvEventName = findViewById(R.id.tvEventName);
        TextView tvTicketsInfo = findViewById(R.id.tvTicketsInfo);
        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount);
        Button btnDone = findViewById(R.id.btnDone);
        Button btnDownloadReceipt = findViewById(R.id.btnDownloadReceipt);

        // Set confirmation message
        tvConfirmation.setText("Paiement confirmé!");
        tvEventName.setText(spectacle.getTitre());

        // Display tickets info
        StringBuilder ticketsInfo = new StringBuilder();
        double total = 0;

        for (BilletSelection billet : selectedBillets) {
            double price = billet.getBillet().getPrix();
            int quantity = billet.getQuantity();
            double subtotal = price * quantity;
            total += subtotal;

            ticketsInfo.append(String.format("%s x%d - %s\n",
                    billet.getBillet().getCategorie(),
                    quantity,
                    formatPrice(subtotal)));
        }

        tvTicketsInfo.setText(ticketsInfo.toString());
        tvTotalAmount.setText(formatPrice(total));

        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentConfirmationActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });


        double finalTotal = total;
        btnDownloadReceipt.setOnClickListener(v -> {
            checkStoragePermission();
            try {
                File pdfFile = generateReceiptPDF(spectacle, selectedBillets, finalTotal);
                openPDF(pdfFile);
                Toast.makeText(this, "Reçu généré avec succès", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Erreur lors de la génération du reçu", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accordée", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File generateReceiptPDF(Spectacle spectacle, List<BilletSelection> billets, double total) throws Exception {
        // Créer le dossier s'il n'existe pas
        File folder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Coulisses");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Créer le fichier PDF
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Recu_" + timeStamp + ".pdf";
        File pdfFile = new File(folder, fileName);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();

        // Ajouter le contenu du PDF
        addMetaData(document);
        addTitle(document);
        addEventDetails(document, spectacle);
        addTicketsDetails(document, billets, total);
        addFooter(document);

        document.close();
        return pdfFile;
    }

    private void addMetaData(Document document) {
        document.addTitle("Reçu de paiement");
        document.addSubject("Confirmation d'achat de billets");
        document.addKeywords("Coulisses, Théâtre, Billets, Reçu");
        document.addAuthor("Coulisses App");
        document.addCreator("Coulisses App");
    }

    private void addTitle(Document document) throws Exception {
        Paragraph title = new Paragraph("Reçu de Paiement", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subTitle = new Paragraph("Confirmation d'achat", SUBTITLE_FONT);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subTitle);

        document.add(new Paragraph(" "));
    }

    private void addEventDetails(Document document, Spectacle spectacle) throws Exception {
        Paragraph eventTitle = new Paragraph("Spectacle: " + spectacle.getTitre(), SUBTITLE_FONT);
        document.add(eventTitle);


        Paragraph date = new Paragraph("Date: " + spectacle.getFormattedDate(), NORMAL_FONT);
        document.add(date);

        Paragraph time = new Paragraph("Heure: " + spectacle.getHeureDebut(), NORMAL_FONT);
        document.add(time);

        Paragraph location = new Paragraph("Lieu: " + spectacle.getLieu().getNom(), NORMAL_FONT);
        document.add(location);

        document.add(new Paragraph(" "));
    }

    private void addTicketsDetails(Document document, List<BilletSelection> billets, double total) throws Exception {
        Paragraph ticketsTitle = new Paragraph("Détails des billets:", SUBTITLE_FONT);
        document.add(ticketsTitle);

        for (BilletSelection billet : billets) {
            String line = String.format(Locale.FRANCE,
                    "- %s x%d: %s",
                    billet.getBillet().getCategorie(),
                    billet.getQuantity(),
                    formatPrice(billet.getBillet().getPrix() * billet.getQuantity()));
            document.add(new Paragraph(line, NORMAL_FONT));
        }

        document.add(new Paragraph(" "));
        Paragraph totalParagraph = new Paragraph("Total: " + formatPrice(total), SUBTITLE_FONT);
        document.add(totalParagraph);
        document.add(new Paragraph(" "));
    }

    private void addFooter(Document document) throws Exception {
        Paragraph thankYou = new Paragraph("Merci pour votre achat !", NORMAL_FONT);
        thankYou.setAlignment(Element.ALIGN_CENTER);
        document.add(thankYou);

        Paragraph contact = new Paragraph("Pour toute question, contactez-nous à contact@coulisses.tn", NORMAL_FONT);
        contact.setAlignment(Element.ALIGN_CENTER);
        document.add(contact);
    }

    private void openPDF(File pdfFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(
                FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", pdfFile)
                ,
                "application/pdf"
        );
        startActivity(intent);
    }

    private String formatPrice(double amount) {

        return String.valueOf(amount) + " DT";
    }
}
