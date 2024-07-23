package edu.uob;

import java.util.ArrayList;
import java.util.List;


public class Location {
    private String name;
    private String description;
    private List<Character> characters;
    private List<Furniture> furniture;
    private List<Artefact> artefacts;
    private List<Path> pathsOut;

    public Location(String name, String description) {
        this.name = name;
        this.description = description;
        this.artefacts = new ArrayList<Artefact>();
        this.pathsOut = new ArrayList<Path>();
        this.characters = new ArrayList<Character>();
        this.furniture = new ArrayList<Furniture>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Artefact> getArtefacts() {
        return artefacts;
    }

    public List<Path> getPathsOut() {
        return pathsOut;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public List<Furniture> getFurniture() {
        return furniture;
    }

    public void addCharacter(Character character) {
        characters.add(character);
    }

    public void addFurniture(Furniture curFurniture) {
        furniture.add(curFurniture);
    }

    public void addArtefact(Artefact artefact) {
        artefacts.add(artefact);
    }

    public void addPath(Path path) {
        pathsOut.add(path);
    }

    public void removeCharacter(Character character) {
        characters.remove(character);
    }

    public void removeFurniture(Furniture curFurniture) {
        furniture.remove(curFurniture);
    }

    public void removeArtefact(Artefact artefact) {
        artefacts.remove(artefact);
    }

    public void removePath(Path path) {
        pathsOut.remove(path);
    }
}

