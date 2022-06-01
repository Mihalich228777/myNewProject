package com.example.mynewproject;


public class User {
    private static User user;
    private static String userName;
    private static String UID;
    private static String Email;
    private static String Type;
    private static String UserGroup;
    public static synchronized User getUser(){
        if(user == null){
            user = new User();
        }
        return user;
    }

    private User() {

    }

    public void create(String uid, String username, String email, String type){
        UID = uid;
        userName = username;
        Email = email;
        Type = type;
    }
    public void setUserGroup(String userGroup) {
        UserGroup = userGroup;
    }

    public String getUserName() {
        return userName;
    }

    public String getUID() {
        return UID;
    }

    public String getEmail() {
        return Email;
    }

    public String getType() {
        return Type;
    }

    public String getUserGroup() {
        return UserGroup;
    }


}

