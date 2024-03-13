package com.hikari.spacecardscollection.User.InterfaceListeners;

import com.google.firebase.Timestamp;
import com.hikari.spacecardscollection.User.User;

public interface UserInfoListener {
    void onUserInfoLoaded(User user);
    void onUserInfoError(String errorMessage);
}