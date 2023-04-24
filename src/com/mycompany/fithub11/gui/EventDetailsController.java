/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fithub11.gui;

import com.mycompany.fithub11.entities.Event;
import com.mycompany.fithub11.entities.Ticket;
import com.mycompany.fithub11.services.TicketService;
import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.image.Image;


public class EventDetailsController {

    private Event selectedEvent;
    private Ticket selectedTicket;

    @FXML
    private ResourceBundle resources;

    @FXML
private ImageView imageView;

    @FXML
    private URL location;

    @FXML
    private Label titleLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label ticketNameLabel;

    @FXML
    private Label ticketPriceLabel;

    @FXML
    private Label ticketAvailabilityLabel;

    @FXML
    private Label ticketMaxLabel;

    @FXML
    private Button bookButton;

   @FXML
void bookButtonAction(ActionEvent event) throws IOException {
    // Get the number of tickets for the selected event from the TicketService
    TicketService ticketService = new TicketService();
    int numTickets = 0;
    try {
        numTickets = ticketService.getNumTicketsByEvent(selectedEvent);
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Disable the book button if the number of tickets already equals the maximum
    // number of tickets allowed
    if (numTickets >= selectedTicket.getNombreMax() || !selectedTicket.isDisponibilite()) {
        bookButton.setDisable(true);
               Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Booking Error");
        alert.setHeaderText("Tickets are sold out");
        alert.setContentText("Sorry, all tickets for this event have been sold out. Please try again later.");
        alert.showAndWait();
        return;
 
    }

    // Load the reservation.fxml file for the selected event
    FXMLLoader loader = new FXMLLoader(getClass().getResource("Reservation.fxml"));
    Parent root = loader.load();

    // Set the selected event and ticket in the reservation controller
    ReservationController reservationController = loader.getController();
    reservationController.setSelectedEvent(selectedEvent);

    // Create and show the reservation stage
    Scene scene = new Scene(root);
    Stage stage = new Stage();
    stage.setScene(scene);
    stage.show();
}


    
    public void setSelectedEvent(Event event) {
    this.selectedEvent = event;

    ImageView imageView = new ImageView();
imageView.setFitWidth(150);
imageView.setFitHeight(150);

String imagePath = "C:/Users/Lenovo/Documents/Fithub/public/images/events/" + selectedEvent.getImage();
File imageFile = new File(imagePath);
if (imageFile.exists()) {
    try {
        Image image = new Image(imageFile.toURI().toString());
        imageView.setImage(image);
    } catch (Exception e) {
        e.printStackTrace();
    }
} else {
    System.out.println("Image file not found: " + imagePath);
}

    titleLabel.setText(event.getNomEvent());
    descriptionLabel.setText(event.getDescription());
    locationLabel.setText(event.getLocation());
    dateLabel.setText(event.getDate().toString());

    // Get the tickets for the selected event from the TicketService
    TicketService ticketService = new TicketService();
    List<Ticket> tickets = null;
    try {
        tickets = ticketService.getTicketsByEvent(selectedEvent);
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Update the ticket labels with the first ticket details
    if (!tickets.isEmpty()) {
        setSelectedTicket(tickets.get(0));
    }
}

    public void setSelectedTicket(Ticket ticket) {
        this.selectedTicket = ticket;

        // Update the ticket labels with the ticket details
        ticketPriceLabel.setText(String.valueOf(ticket.getPrix()));
        ticketMaxLabel.setText(String.valueOf(ticket.getNombreMax()));
        ticketAvailabilityLabel.setText(ticket.isDisponibilite() ? "Available" : "Not available");
  // Check ticket availability
    if (ticket.isDisponibilite()) {
        TicketService ticketService = new TicketService();
        int numTickets = 0;
        try {
            numTickets = ticketService.getNumTicketsByEvent(selectedEvent);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (numTickets >= ticket.getNombreMax()) {
            ticketAvailabilityLabel.setText("Unavailable");
            bookButton.setDisable(true);
                    } else {
            ticketAvailabilityLabel.setText("Available");
            bookButton.setDisable(false);
        }
    } else {
        ticketAvailabilityLabel.setText("Unavailable");
        bookButton.setDisable(true);
    }        
    }

    @FXML
    void initialize() {
        
        
           }
}