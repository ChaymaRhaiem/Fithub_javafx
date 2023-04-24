package com.mycompany.fithub11.gui;

import com.google.zxing.qrcode.QRCodeWriter;
import com.mycompany.fithub11.Utils.MyDB;
import com.mycompany.fithub11.entities.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.awt.*;
import java.awt.image.BufferedImage;

import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;


import javax.imageio.ImageIO;
import java.io.File;
import java.sql.*;

public class ReservationController {


        @FXML
        private TextField nomField;

        @FXML
        private TextField emailField;

    private Event selectedEvent;

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }


    @FXML
    public void initialize() {
        selectedEvent = clientEventsController.selectedEvent;
        System.out.println(selectedEvent);
    }
        @FXML
        public void addReservation(ActionEvent actionEvent) {
            if (nomField.getText().isEmpty() || emailField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all fields.");
                alert.showAndWait();
                return;
            }
            try {
                String nom = nomField.getText();
                String email = emailField.getText();

                // Get ticket details for selected event
                Connection connection = MyDB.getInstance().getConx();
                PreparedStatement stmt = connection.prepareStatement("SELECT prix, nombre_max, disponibilite FROM ticket WHERE event_id = ?");
                stmt.setInt(1, selectedEvent.getId());
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new SQLException("Failed to get ticket details.");
                }

                int prix = rs.getInt("prix");
                int nombreMax = rs.getInt("nombre_max");
                boolean disponibilite = rs.getBoolean("disponibilite");

                // Add reservation
                stmt = connection.prepareStatement("INSERT INTO ticket (nom, email, event_id, prix, nombre_max, disponibilite, booking_date) VALUES (?, ?, ?, ?, ?, ?,?)");
                stmt.setString(1, nom);
                stmt.setString(2, email);
                stmt.setInt(3, selectedEvent.getId());
                stmt.setInt(4, prix);
                stmt.setInt(5, nombreMax);
                stmt.setBoolean(6, disponibilite);
                stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 1) {
                    generateQRCodeAndSaveAsPDF(nom, new Timestamp(System.currentTimeMillis()), selectedEvent.getId());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Réservation ajoutée");
                    alert.setHeaderText(null);
                    alert.setContentText("La réservation a été ajoutée avec succès.");
                    alert.showAndWait();
                } else {
                    throw new SQLException("Failed to add reservation.");
                }
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Impossible d'ajouter la réservation.");
                alert.showAndWait();
            }
        }

    private void generateQRCodeAndSaveAsPDF(String nom, Timestamp bookingDate, int eventId) {


        try {
            // Generate QR code content
            String qrCodeContent =
                    "Nom: " + nom + "\n" +
                            "Date de réservation: " + bookingDate.toString() + "\n" +
                            "Event ID: " + eventId;

            // Generate QR code image
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 200, 200);

            // Convert BitMatrix to BufferedImage
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            // Create PDF document
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Add QR code image to PDF
            PDImageXObject qrCodeImage = LosslessFactory.createFromImage(document, qrImage);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(qrCodeImage, 50, 500, 200, 200);
                // Add event name, ticket name, date, and location to PDF
                // Set font and font size
                //PDFont font = PDType1Font.HELVETICA_BOLD;
                //int fontSize = 12;


                // Write event details to PDF
                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_BOLD, 16);
                contentStream.newLineAtOffset(50, 450);
                contentStream.showText("Nom de l'événement: " + selectedEvent.getNomEvent());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Nom : " + nom);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Date de l'événement: " + selectedEvent.getDate().toString());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Lieu de l'événement: " + selectedEvent.getLocation());
                contentStream.endText();
                // Load image from file
                File imageFile = new File("C:/Users/Lenovo/Documents/Fithub/public/images/events/241784440_403251608026937_5066154985632320081_n.jpg");
                BufferedImage bufferedImage = ImageIO.read(imageFile);

// Convert BufferedImage to PDImageXObject
                PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);

// Add image to PDF document
                contentStream.drawImage(pdImage, 30, 30, width, height);
            }

            // Save PDF file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le fichier PDF");
            fileChooser.setInitialFileName("reservation_" + nom + ".pdf");
            File file = fileChooser.showSaveDialog(null);
            String fileName = "reservation_" + nom + ".pdf";
            document.save(file);
            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("PDF généré");
            alert.setHeaderText(null);
            alert.setContentText("Le fichier " + fileName + " a été généré avec succès.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de générer le PDF.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

}


