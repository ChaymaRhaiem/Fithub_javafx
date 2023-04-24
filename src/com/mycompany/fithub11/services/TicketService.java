package com.mycompany.fithub11.services;

import com.mycompany.fithub11.Utils.MyDB;
import com.mycompany.fithub11.entities.Event;
import com.mycompany.fithub11.entities.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketService {

    private Connection conx;

    public TicketService() {
        conx = MyDB.getInstance().getConx();

    }

    public void addTicket(Ticket t) {
        try {
            String request = "INSERT INTO ticket(prix, nombre_max, disponibilite, nom, email, event_id) VALUES (?,?,?,?,?,?)";
            PreparedStatement pst = conx.prepareStatement(request);
            pst.setInt(1, t.getPrix());
            pst.setInt(2, t.getNombreMax());
            pst.setBoolean(3, t.isDisponibilite());
            pst.setString(4, t.getNom());
            pst.setString(5, t.getEmail());
            pst.setInt(6, t.getEvent().getId());
            pst.executeUpdate();
            System.out.println("Ticket ajouté avec succès !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void updateTicket(Ticket t) {
        try {
            String request = "UPDATE ticket SET prix=?, nombre_max=?, disponibilite=?, nom=?, email=?, id_event=? WHERE id=?";
            PreparedStatement pst = conx.prepareStatement(request);
            pst.setInt(1, t.getPrix());
            pst.setInt(2, t.getNombreMax());
            pst.setBoolean(3, t.isDisponibilite());
            pst.setString(4, t.getNom());
            pst.setString(5, t.getEmail());
            pst.setInt(6, t.getEvent().getId());
            pst.setInt(7, t.getId());
            pst.executeUpdate();
            System.out.println("Ticket modifié avec succès !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void deleteTicket(int id) {
        try {
            String request = "DELETE FROM ticket WHERE id=?";
            PreparedStatement pst = conx.prepareStatement(request);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Ticket supprimé avec succès !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> listTickets = new ArrayList<>();
        try {
            String request = "SELECT * FROM ticket JOIN event ON ticket.event_id=event.id";
            Statement st = conx.createStatement();
            ResultSet rs = st.executeQuery(request);
            while (rs.next()) {
                Event event = new Event(rs.getInt("event.id"), rs.getString("event.nomEvent"), rs.getString("event.description"), rs.getString("event.image"), rs.getString("event.location"), rs.getDate("event.date"), rs.getString("event.type"));
                Ticket ticket = new Ticket(rs.getInt("ticket.id"), rs.getInt("ticket.prix"), rs.getInt("ticket.nombreMax"), rs.getBoolean("ticket.disponibilite"), rs.getString("ticket.nom"), rs.getString("ticket.email"), event);
                listTickets.add(ticket);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return listTickets;
    }

    public Ticket getTicketById(int ticketId) throws SQLException {
        String query = "SELECT * FROM ticket WHERE id=?";
        try (PreparedStatement ps = conx.prepareStatement(query)) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
                return null;
            }
        }
    }

    public List<Ticket> getTicketsByEvent(Event event) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM ticket WHERE event_id=?";
        try (PreparedStatement ps = conx.prepareStatement(query)) {
            ps.setInt(1, event.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
                return tickets;
            }
        }
    }

    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setPrix(rs.getInt("prix"));
        ticket.setNombreMax(rs.getInt("nombre_max"));
        ticket.setDisponibilite(rs.getBoolean("disponibilite"));
        ticket.setNom(rs.getString("nom"));
        ticket.setEmail(rs.getString("email"));
        Event event = new Event();
        event.setId(rs.getInt("event_id"));
        ticket.setEvent(event);
        return ticket;
    }
    
public int getNumTicketsByEvent(Event event) throws SQLException {
    String query = "SELECT COUNT(*) AS numTickets FROM ticket WHERE event_id = ?";
    PreparedStatement stmt = conx.prepareStatement(query);
    stmt.setInt(1, event.getId());
    ResultSet rs = stmt.executeQuery();
    int numTickets = rs.next() ? rs.getInt("numTickets") : 0;
    rs.close();
    stmt.close();
    return numTickets;
}
}