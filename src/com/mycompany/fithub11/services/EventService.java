package com.mycompany.fithub11.services;

import com.mycompany.fithub11.Utils.MyDB;
import com.mycompany.fithub11.entities.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


    /**
     *
     * @author Lenovo
     */
    public class EventService {

        public Connection conx;
        public Statement stm;

        public EventService() {
            conx = MyDB.getInstance().getConx();

        }

        public List<Event> ListEvent() throws SQLException {
            List<Event> events = new ArrayList<>();
            String sql = "SELECT * FROM event";
            try ( Statement statement = conx.createStatement();  ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    Event event = new Event(resultSet.getInt("id"), resultSet.getString("nom_event"), resultSet.getString("description"), resultSet.getString("location"), resultSet.getString("type"), resultSet.getDate("date"), resultSet.getString("image"));
                    event.setNomEvent(resultSet.getString("nom_event"));
                    event.setDescription(resultSet.getString("description"));
                    event.setLocation(resultSet.getString("location"));
                    event.setType(resultSet.getString("type"));
                    event.setDate(resultSet.getDate("date"));
                    event.setImage(resultSet.getString("image"));
                    events.add(event);
                }
            }
            return events;
        }

        public void AjouterEvent(Event v) {
            try {
                String req = "insert into event(id, description, nomEvent, location, date, type, image)"
                        + "values(" + v.getId() + ",'" + v.getDescription() + "','" + v.getNomEvent() + "','" + v.getLocation() + "','" + v.getDate() + "',"
                        + "'" + v.getType() + "','" + v.getImage() + "'";

                Statement st = conx.createStatement();
                st.executeUpdate(req);
                System.out.println("Evenement ajouté avec succés");
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void deleteEvent(int id) {
            try {
                Statement st = conx.createStatement();
                String req = "DELETE FROM event WHERE id = " + id + "";
                st.executeUpdate(req);
                System.out.println("L'evenement avec l'id = " + id + " a été supprimer avec succès...");
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        public void modifierEvent(Event v, int id) {
            try {

                PreparedStatement ps = conx.prepareStatement("UPDATE event SET nom_event=?,description=?,type=?,date=?,location=?,image=? WHERE id=?");
                ps.setString(1, v.getNomEvent());
                ps.setString(2, v.getDescription());
                ps.setString(3, v.getLocation());
                ps.setDate(4, (Date) v.getDate());
                ps.setString(5, v.getType());
                ps.setString(6, v.getImage());
                ps.setInt(7, id);

                ps.executeUpdate();
                System.out.println("Evenement Modifier avec succs");

            } catch (SQLException ex) {
                Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        public void updateEvent(Event selectedEvent) {

        }
    }
