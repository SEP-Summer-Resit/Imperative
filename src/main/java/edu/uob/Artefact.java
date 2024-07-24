package edu.uob;

public class Artefact extends Entity{

    public Artefact(String name, String description) {
        super(name, description);
    }

    public boolean equals(Object obj){
        Artefact checkArtefact = (Artefact) obj;
        return checkArtefact.name.equals(name);
    }

}

