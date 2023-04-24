/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fithub11.entities;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class Event {
      private int id;
    private String nomEvent;
    private String description;
    private String location;
    private Date date;
    private String type;
    private List<Ticket> tickets = new ArrayList<>();

    private String image;

    public Event(int id, String nomEvent, String description, String location, String type, Date date, String image) {
    }

    public Event(String nomEvent, String description, String location, Date date, String type, String image) {
        this.nomEvent = nomEvent;
        this.description = description;
        this.location = location;
        this.date = date;
        this.type = type;
        this.image = image;
    }
    public Event(int id,String nomEvent, String description, String location, Date date, String type, String image) {
        this.id = id;
        this.nomEvent = nomEvent;
        this.description = description;
        this.location = location;
        this.date = date;
        this.type = type;
        this.image = image;
    }
    public Event() {

    }

    public String getNomEvent() {
        return nomEvent;
    }

    public void setNomEvent(String nomEvent) {
        this.nomEvent = nomEvent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public java.sql.Date getDate() {
        return (java.sql.Date) date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Event{" + "id=" + id + ", nomEvent=" + nomEvent + ", description=" + description + ", location=" + location + ", date=" + date + ", type=" + type + ", image=" + image + '}';
    }
    
}
