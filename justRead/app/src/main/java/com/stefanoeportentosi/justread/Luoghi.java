package com.stefanoeportentosi.justread;

/**
 * Created by stefanoeportentosi on 07/05/17.
 */

public class Luoghi {
    private String titolo;
    private Double lat;
    private Double lng;
    private Integer id;
    private Float radius;
    private String autore;
    private String stelle;


    public Luoghi(int id, String titolo, String autore, String stelle, double lat, double lng, float radius) {
        this.id=id;
        this.titolo =titolo ;
        this.lat=lat;
        this.lng=lng;
        this.stelle=stelle;
        this.radius=radius;
        this.autore =autore;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getRadius() {
        return radius;
    }

    public void setRadius(Float radius) {
        this.radius = radius;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getStelle() {
        return stelle;
    }

    public void setStelle(String stelle) {
        this.stelle = stelle;
    }
}
