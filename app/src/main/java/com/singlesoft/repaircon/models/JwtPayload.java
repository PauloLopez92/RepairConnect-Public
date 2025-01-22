package com.singlesoft.repaircon.models;

public class JwtPayload {
    private long userId;
    private String userName;
    private String userType;
    private long exp;

    // Getters and Setters here
    public long getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }

    public String getUserType() {
        return userType;
    }
    public long getExp() {
        return exp;
    }
}