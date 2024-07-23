package edu.uob;

public class Furniture {
    private String name;
    private String description;

    public Furniture(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
