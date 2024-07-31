package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Artefact> inventory;
    private int location;
    private int health;

    public Player(String name, List<Artefact> inventory) {
        this.name = name;
        this.inventory = new ArrayList<Artefact>();
        this.location = 0;
        this.health = 3;
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

    public void addArtefactToInventory(Artefact artefact) {
        this.inventory.add(artefact);
    }

    public void removeArtefactFromInventory(Artefact artefact) {
        this.inventory.remove(artefact);
    }

    public int getHealth() { return health; }

    public void reduceHealth(int amount) {
        this.health = this.health - amount;
        System.out.println("Ouch! Health is now " + this.health);
    }

    public void refillHealth() {
        this.health = 3;
    }
}
