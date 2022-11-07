package com.example.cloudshare.Model;

public class Pic {
    private int id;
    private String name;

    public Pic(){}

    public Pic(int id,String name){
        this.id=id;
        this.name=name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
