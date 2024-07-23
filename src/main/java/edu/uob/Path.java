package edu.uob;

public class Path {
    private Location startLocation;
    private Location destination;
    

    public Path(Location destination, Location startLocation) {
        this.destination = destination;
        this.startLocation = startLocation;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getDestination() {
        return destination;
    }

}