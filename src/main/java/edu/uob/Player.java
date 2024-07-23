package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Artefact> inventory;

    public Player(String name, List<Artefact> inventory) {
        this.name = name;
        this.inventory = new ArrayList<Artefact>();
    }

    public String getName() {
        return name;
    }

    public List<Artefact> getDescription() {
        return inventory;
    }
}
