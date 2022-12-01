package com.example.cloudshare.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Pic implements Serializable {
    private String path;
    private String sender;
    private String receiver;


    public Pic(String path,String sender,String receiver){
        this.path=path;
        this.sender=sender;
        this.receiver=receiver;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPath() {
        return path;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "Pic{" +
                "path='" + path + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }
}
