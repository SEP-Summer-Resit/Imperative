package edu.uob;

public class Artefact {
    private String name;
    private String description;

    public Artefact(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Artefact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

}
