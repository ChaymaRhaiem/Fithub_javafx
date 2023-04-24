/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fithub11;

import com.mycompany.fithub11.Utils.MyDB;
import com.mycompany.fithub11.entities.Event;
import com.mycompany.fithub11.services.EventService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author Lenovo
 */
public class Fithub00 extends Application {
    
    @Override
  public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Fithub00.class.getResource("gui/clientEvents.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) throws SQLException {
        MyDB db2 = MyDB.getInstance();
        //Date Datevoy = Date.valueOf("2022-10-10");
        //Event v = new Event("nom", "aaaaaaaaa", "arena", Datevoy, "Competition", "Image");
        EventService ps = new EventService();
        List<Event> Listevent = ps.ListEvent();

        for (Event event : Listevent) {
            System.out.println("id: " + event.getId());

            System.out.println("Nom event: " + event.getNomEvent());
            System.out.println("Description: " + event.getDescription());
            System.out.println("Location: " + event.getLocation());
            System.out.println("Type: " + event.getType());
            System.out.println("Date: " + event.getDate());
            System.out.println("Image: " + event.getImage());
            System.out.println("=========================");
        }
        /*
        Ticket t = new Ticket();
        t.setPrix(50);
        t.setNombreMax(100);
        t.setDisponibilite(true);
        t.setNom("Jon");
        t.setEmail("jon@example.com");
        Event event = new Event();
        event.setId(36); // assuming you have an event with ID 1 in the database
        t.setEvent(event);
        TicketService ticketService = new TicketService();
        ticketService.addTicket(t);*/
/*
        TicketService ticketService = new TicketService();

        Scanner scanner = new Scanner(System.in);

        // ask the user for the event ID
        System.out.print("Enter the ID of the event: ");
        int event_id = scanner.nextInt();

        // get the tickets for the specified event
        Event event = new Event();
        event.setId(event_id);
        List<Ticket> tickets = ticketService.getTicketsByEvent(event);

        // print the tickets
        System.out.println("Tickets for event " + event_id + ":");
        for (Ticket ticket : tickets) {
            System.out.println(ticket);
        }*/


        launch(args);

    }
    
}
