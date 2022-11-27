package com.example.cloudshare.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String username;
    private String password;
    private ArrayList<String> folders;

    public User (String username,String password,ArrayList<String> folders){
        this.username=username;
        this.password=password;
        this.folders=folders;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFolders(ArrayList<String> folders) {
        this.folders = folders;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getFolders() {
        return folders;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", folders=" + folders +
                '}';
    }
}
