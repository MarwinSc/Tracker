package com.example.tracker;

public class Habit {

    public Habit(String name){
        this.name = name;
    }

    private int counter;
    protected String name;

    public void increment(){
        this.counter += 1;
    }
}
