package edu.uob;

public class Artefact extends Entity{

    public Artefact(String name, String description) {
        super(name, description);
    }

    public Boolean equals(String check){
        System.out.println("Comparing " + check + " with " + name + " returning " + check.equals(name));
        return check.equals(name);
    }

}

