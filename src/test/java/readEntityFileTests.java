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

  @Test
  void testDescription() throws ParseException {
    ArrayList<Location> locations = server.readEntityFile("etities.dot");
    String expectedDescriptionCabin = ("A log cabin in the woods");
    String expectedDescriptionPotion = ("A bottle of magic potion");
    String actualDescriptionCabin = "";
    String actualDescriptionPotion = "";
    for (Location location : locations) {
      if ((location.getName()).equals("cabin")){
        actualDescriptionCabin = location.getDescription();
        for (Artefact artefact : location.getArtefacts()) {
          if ((artefact.getName()).equals("potion")){
            actualDescriptionPotion = artefact.getDescription();
          }
        }
      }
    }
    assertTrue(actualDescriptionCabin.equals(expectedDescriptionCabin), "character '" + expectedDescriptionCabin + "' should be present.");
    assertTrue(actualDescriptionPotion.equals(expectedDescriptionPotion), "character '" + expectedDescriptionPotion + "' should be present.");
    
    
  }

  @Test
  void testPaths() throws ParseException {
    ArrayList<Location> locations = server.readEntityFile("etities.dot");
    String[] expectedPathsForest = {"cabin", "riverbank"};
    String[] expectedPathsCabin = {"forest"};
    String[] expectedPathsClearing = {"riverbank"};
    Set<String> actualPathsOutForest = new HashSet<>();
    Set<String> actualPathsOutCabin = new HashSet<>();
    Set<String> actualPathsOutClearing = new HashSet<>();
    for (Location location : locations) {
      if (location.getName().equals("cabin"))
      for (Path path : location.getPathsOut()){
        actualPathsOutCabin.add(path.getDestination());
      }
      if (location.getName().equals("forest"))
      for (Path path : location.getPathsOut()){
        actualPathsOutForest.add(path.getDestination());
      }
      if (location.getName().equals("clearing"))
      for (Path path : location.getPathsOut()){
        actualPathsOutClearing.add(path.getDestination());
      }
    }
    for (String expectedPathForest : expectedPathsForest) {
      assertTrue(actualPathsOutForest.contains(expectedPathForest), "character '" + expectedPathForest + "' should be present.");
    }
    for (String expectedPathClearing : expectedPathsClearing) {
      assertTrue(actualPathsOutForest.contains(expectedPathClearing), "character '" + expectedPathClearing + "' should be present.");
    }
    for (String expectedPathCabin : expectedPathsCabin) {
      assertTrue(actualPathsOutCabin.contains(expectedPathCabin), "character '" + expectedPathCabin + "' should be present.");
    }
  }

}



  


