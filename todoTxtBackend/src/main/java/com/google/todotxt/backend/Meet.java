package com.google.todotxt.backend;

/**
 * Created by Newshka on 25.09.2014.
 */
public class Meet {

    private String place;

    private String name;

    private String description;

    public Meet(String place) {
        this.place = place;
    }

    public Meet() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
