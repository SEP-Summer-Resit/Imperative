package edu.uob;

public class Path {
    private String startLocation;
    private String destination;
    

    public Path(String destination, String startLocation) {
        this.destination = destination;
        this.startLocation = startLocation;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getDestination() {
        return destination;
    }

}