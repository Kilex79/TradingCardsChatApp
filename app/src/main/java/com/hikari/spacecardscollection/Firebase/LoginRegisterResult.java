package com.hikari.spacecardscollection.Firebase;

import com.hikari.spacecardscollection.User.User;

public class LoginRegisterResult {

    private boolean loggedIn;
    private boolean registeredCorrect;
    private User user;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isRegisteredCorrect() {
        return registeredCorrect;
    }

    public void setRegisteredCorrect(boolean registeredCorrect) {
        this.registeredCorrect = registeredCorrect;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}