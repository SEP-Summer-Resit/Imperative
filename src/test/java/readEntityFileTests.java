package edu.uob;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alexmerz.graphviz.ParseException;

import edu.uob.Character;


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
    ArrayList<Location> locations = server.readEntityFile("etities.dot");
    String[] expectedArtefacts = {"potion", "axe", "coin", "log", "shovel", "gold", "key", "horn"};
    Set<String> actualArtefacts = new HashSet<>();
    for (Location location : locations) {
      for (Artefact artefact : location.getArtefacts()){
        actualArtefacts.add(artefact.getName());
      }
    }
    for (String expectedArtefact : expectedArtefacts) {
      assertTrue(actualArtefacts.contains(expectedArtefact), "artefact '" + expectedArtefact + "' should be present.");
    }
  }

  @Test
  void testFurniture() throws ParseException {
    ArrayList<Location> locations = server.readEntityFile("etities.dot");
    String[] expectedFurnitures = {"trapdoor", "tree", "river", "ground", "hole"};
    Set<String> actualFurniture = new HashSet<>();
    for (Location location : locations) {
      for (Furniture furniture : location.getFurniture()){
        actualFurniture.add(furniture.getName());
      }
    }
    for (String expectedFurniture : expectedFurnitures) {
      assertTrue(actualFurniture.contains(expectedFurniture), "furniture '" + expectedFurniture + "' should be present.");
    }
  }

  @Test
  void testCharacters() throws ParseException {
    ArrayList<Location> locations = server.readEntityFile("etities.dot");
    String[] expectedCharacters = {"lumberjack", "elf"};
    Set<String> actualCharacters = new HashSet<>();
    for (Location location : locations) {
      for (Character character : location.getCharacters()){
        actualCharacters.add(character.getName());
      }
    }
    for (String expectedCharacter : expectedCharacters) {
      assertTrue(actualCharacters.contains(expectedCharacter), "character '" + expectedCharacter + "' should be present.");
    }
  }

  

}



  


