package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Artefact> inventory;
    private int location;

    public Player(String name, List<Artefact> inventory) {
        this.name = name;
        this.inventory = new ArrayList<Artefact>();
        this.location = 0;
    }

    public String getName() {
        return name;
    }

    public List<Artefact> getInventory() {
        return inventory;
    }

    public int getLocation() { return location; }

    public void setLocation(int location) {
        this.location = location;
    }
}
