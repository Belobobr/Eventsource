package com.google.todotxt.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.appengine.api.users.User;

import java.util.ArrayList;

/**
 * Created by Newshka on 05.10.2014.
 */

@ApiReference(Greetings.class)
@Api(name = "persons")
public class Persons {

    @ApiMethod(name = "persons.addIfNotExists", httpMethod = "post")
    public void addIfNotExists(User user) {

    }

    @ApiMethod(name = "persons.changeUserInfo", httpMethod = "post")
    public void changeUserInfo(Person person, User user) {

    }
}
