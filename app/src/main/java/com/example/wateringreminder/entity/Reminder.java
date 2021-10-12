package com.example.wateringreminder.entity;

import java.io.Serializable;

public class Reminder implements Serializable {
    private int id;
    private String name;
    private int plant;
    private long time;
    private int period;
    private long last;

    public Reminder(int id, String name, int plant, long time, int period, long last) {
        this.id = id;
        this.name = name;
        this.plant = plant;
        this.time = time;
        this.period = period;
        this.last = last;
    }

    public Reminder() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlant() {
        return plant;
    }

    public void setPlant(int plant) {
        this.plant = plant;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Long getLast() {
        return last;
    }

    public void setLast(Long last) {
        this.last = last;
    }
}
