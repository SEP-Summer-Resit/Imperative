package edu.uob;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alexmerz.graphviz.ParseException;


final class readEntityFileTests {

  private GameServer server;

  // This method is automatically run _before_ each of the @Test methods
  @BeforeEach
  void setup() {
      server = new GameServer();
  }

  @Test
  void testLocations() throws ParseException {
    ArrayList<Location> locations = server.readEntityFile("etities.dot");
    String[] expectedLocations = {"cabin", "riverbank", "forest", "clearing", "cellar", "storeroom"};
    Set<String> locationNames = new HashSet<>();
    for (Location location : locations) {
      locationNames.add(location.getName());
    }
    for (String expectedLocation : expectedLocations) {
      assertTrue(locationNames.contains(expectedLocation), "Location '" + expectedLocation + "' should be present.");
    }
    }

  @Test
  void testArtefacts() throws ParseException {
    System.out.println("Starting testArtefacts");
    ArrayList<Location> locations = server.readEntityFile("etities.dot");
    String[] expectedArtefacts = {"potion", "axe", "coin"};
    Set<String> actualArtefacts = new HashSet<>();
    for (Location location : locations) {
      System.out.println(location.getName());
      for (Artefact artefact : location.getArtefacts()){
          actualArtefacts.add(artefact.getName());
          System.out.println(location.getName());
          System.out.println(artefact.getName());
      }
    }
    
    for (String expectedArtefact : expectedArtefacts) {
      assertTrue(actualArtefacts.contains(expectedArtefact), "artefact '" + expectedArtefact + "' should be present.");
    }
    }


  }


