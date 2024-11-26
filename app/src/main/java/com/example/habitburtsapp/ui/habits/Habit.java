package com.example.habitburtsapp.ui.habits;

public class Habit {
    // Private attributes
    private String name;
    private String description;
    private String type;
    private int time;

    // Default constructor
    public Habit() {
        // Default constructor (useful for frameworks like Firebase)
    }

    // Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and setter for type
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Getter and setter for time
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
