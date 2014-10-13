package com.google.todotxt.backend;

import com.google.appengine.api.users.User;

/**
 * Created by Newshka on 05.10.2014.
 */
public class Person {

    private String name;

    private User user;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
