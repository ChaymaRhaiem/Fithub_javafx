/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fithub11.entities;

/**
 *
 * @author Lenovo
 */
public class Ticket {
     private int id;
    private int prix;
    private int nombreMax;
    private boolean disponibilite;
    private String nom;
    private String email;
    Event event;

    public Ticket(int prix, int nombreMax, boolean disponibilite, String nom, String email, Event event) {
        this.prix = prix;
        this.nombreMax = nombreMax;
        this.disponibilite = disponibilite;
        this.nom = nom;
        this.email = email;
        this.event = event;
    }

    public Ticket() {
    }

    public Ticket(int id, int prix, int nombreMax, boolean disponibilite, String nom, String email, Event event) {
        this.id = id;
        this.prix = prix;
        this.nombreMax = nombreMax;
        this.disponibilite = disponibilite;
        this.nom = nom;
        this.email = email;
        this.event = event;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public int getNombreMax() {
        return nombreMax;
    }

    public void setNombreMax(int nombreMax) {
        this.nombreMax = nombreMax;
    }

    public boolean isDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", prix=" + prix +
                ", nombreMax=" + nombreMax +
                ", disponibilite=" + disponibilite +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", event=" + event +
                '}';
    }
    
}
