package com.mycompany.fithub11.gui;

import com.mycompany.fithub11.Utils.MyDB;
import com.mycompany.fithub11.entities.Event;
import com.mycompany.fithub11.entities.Ticket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class TicketController {
    @FXML
    private final Connection connection = MyDB.getInstance().getConx();
    @FXML
    private TextField nomTicketField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField prixField;
    @FXML
    private TextField nombreMaxField;
    @FXML
    private CheckBox disponibiliteCheckBox;

    @FXML
    private ListView<Ticket> ticketListView;

    private Event selectedEvent;


    @FXML
private Button    removeButton ;

    @FXML
    public void initialize() {
        
        //removeButton.disableProperty().bind(ticketListView.getSelectionModel().selectedItemProperty().isNull());

        selectedEvent = DisplayEventsController.selectedEvent;
        System.out.println(selectedEvent);
        if (ticketListView == null) {
            System.out.println("ticketListView is null");
        } else {
            showTickets(selectedEvent);
        }
        // set the initial text of the nomTicketField and emailField to an empty string
        //nomTicketField.setText("");
        //emailField.setText("");
    }

    @FXML
    public void addTicket(ActionEvent actionEvent) {
        if (prixField.getText().isEmpty() || nombreMaxField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }
        try {
            //String nom = nomTicketField.getText();
            //String email = emailField.getText();
            int prix = Integer.parseInt(prixField.getText());
            int nombreMax = Integer.parseInt(nombreMaxField.getText());
            boolean disponibilite = disponibiliteCheckBox.isSelected();

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO ticket (prix, nombre_max, disponibilite, event_id) VALUES ( ?, ?, ?, ?)");
            //stmt.setString(1, nom);
            //stmt.setString(2, email);
            stmt.setInt(1, prix);
            stmt.setInt(2, nombreMax);
            stmt.setBoolean(3, disponibilite);
            stmt.setInt(4, selectedEvent.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ticket ajouté");
                alert.setHeaderText(null);
                alert.setContentText("Le ticket a été ajouté avec succès.");
                alert.showAndWait();
            } else {
                throw new SQLException("Failed to add ticket.");
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez saisir des nombres valides pour le prix et le nombre maximum.");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ajouter le ticket. Veuillez réessayer plus tard.");
            alert.showAndWait();
        }
    }


    private void showTickets(Event event) {
        try {
            // Query the database to get a list of tickets for the selected event
            String query = "SELECT * FROM ticket WHERE event_id = " + selectedEvent.getId();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Create a list of Ticket objects from the ResultSet
            List<Ticket> ticketList = new ArrayList<>();
            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("id"));
                ticket.setNom(rs.getString("nom"));
                ticket.setEmail(rs.getString("email"));
                ticket.setPrix(rs.getInt("prix"));
                ticket.setNombreMax(rs.getInt("nombre_max"));
                ticket.setDisponibilite(rs.getBoolean("disponibilite"));
                ticket.setEvent(event);
                ticketList.add(ticket);
            }

            // Use an ObservableList to display the list of tickets in the ListView
    // Use an ObservableList to display the list of tickets in the ListView
        ObservableList<Ticket> observableList = FXCollections.observableArrayList(ticketList);
        ticketListView.setItems(observableList);

        // Define a custom cell factory for the ListView
        ticketListView.setCellFactory(param -> new ListCell<Ticket>() {
            @Override
            protected void updateItem(Ticket item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Create separate labels for each attribute and add them to a HBox
                    Label nomLabel = new Label(item.getNom());
                    Label emailLabel = new Label(item.getEmail());
                    Label prixLabel = new Label(Integer.toString(item.getPrix()));
                    Label nombreMaxLabel = new Label(Integer.toString(item.getNombreMax()));
                    Label disponibiliteLabel = new Label(item.isDisponibilite() ? "Available" : "Sold Out");
                    HBox hbox = new HBox(nomLabel, emailLabel, prixLabel, nombreMaxLabel, disponibiliteLabel);
                    hbox.setSpacing(10);

                    // Set the HBox as the cell's graphic
                    setGraphic(hbox);
                }
            }
        });

    } catch (SQLException e) {
        e.printStackTrace();
    }
       
    }

    
    @FXML
public void removeTicket(ActionEvent actionEvent) {
    // Get the selected ticket from the ListView
    Ticket selectedTicket = ticketListView.getSelectionModel().getSelectedItem();
    if (selectedTicket == null) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Please select a ticket to remove.");
        alert.showAndWait();
        return;
    }
    try {
        // Delete the selected ticket from the database
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM ticket WHERE id = ?");
        stmt.setInt(1, selectedTicket.getId());
        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected == 1) {
            // Remove the selected ticket from the ListView
            ObservableList<Ticket> items = ticketListView.getItems();
            items.remove(selectedTicket);
            ticketListView.setItems(items);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ticket removed");
            alert.setHeaderText(null);
            alert.setContentText("The ticket has been removed successfully.");
            alert.showAndWait();
        } else {
            throw new SQLException("Failed to remove ticket.");
        }
    } catch (SQLException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Failed to remove ticket. Please try again later.");
        alert.showAndWait();
    }
}
/*
@FXML
public void editTicket(ActionEvent actionEvent) {
    // Get the selected ticket from the ListView
    Ticket selectedTicket = ticketListView.getSelectionModel().getSelectedItem();
    if (selectedTicket == null) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Please select a ticket to edit.");
        alert.showAndWait();
        return;
    }
    try {
        // Create a new EditTicketDialog to edit the selected ticket
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/fithub11/gui/EditTicketDialog.fxml"));
        Parent root = loader.load();

        EditTicketDialogController controller = loader.getController();
        controller.setTicket(selectedTicket);

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Edit Ticket");
        stage.setScene(scene);
        stage.showAndWait();

        // Reload the list of tickets from the database
        showTickets(selectedEvent);
    } catch (IOException e) {
        e.printStackTrace();
    }
*/
}





