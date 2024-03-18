package com.hikari.spacecardscollection.User;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class User implements Serializable {

    private List<User> firends;
    private List<String> friendsRequest;
    private Long gachaQuantity;
    private String playerColdown;


    private String level;
    private String registerDate;
    private String userEmail;
    private String userIcon;
    private String userId;
    private String userName;
    private String virtualMoney;
    private String virtualPremiumMoney;

    public User(String userName, String userEmail, String registerDate, String userIcon, String userId, String virtualMoney, String virtualPremiumMoney, String level) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.registerDate = registerDate;
        this.userIcon = userIcon;
        this.userId = userId;
        this.virtualMoney = virtualMoney;
        this.virtualPremiumMoney = virtualPremiumMoney;
        this.level = level;
    }

    public User(String userName, String userEmail, String registerDate, String userIcon, String level) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.registerDate = registerDate;
        this.userIcon = userIcon;
        this.level = level;
    }

    public User(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public User() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVirtualMoney() {
        return virtualMoney;
    }

    public void setVirtualMoney(String virtualMoney) {
        this.virtualMoney = virtualMoney;
    }

    public String getVirtualPremiumMoney() {
        return virtualPremiumMoney;
    }

    public void setVirtualPremiumMoney(String virtualPremiumMoney) {
        this.virtualPremiumMoney = virtualPremiumMoney;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getGachaQuantity() {
        return gachaQuantity;
    }

    public void setGachaQuantity(Long gachaQuantity) {
        this.gachaQuantity = gachaQuantity;
    }

    public String getPlayerColdown() {
        return playerColdown;
    }

    public void setPlayerColdown(String  playerColdown) {
        this.playerColdown = playerColdown;
    }
}
