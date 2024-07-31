package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alexmerz.graphviz.ParseException;


final class CommandTests {

  private GameServer server;

  // This method is automatically run _before_ each of the @Test methods
  @BeforeEach
  void setup() {
      server = new GameServer();
      server.validSubjects.addAll(Arrays.asList("potion", "forest", "key", "axe"));
  }

  @AfterEach
  void reset() throws ParseException {
    server.handleCommand("Daniel: reset");
  }

  @Test
  void testConsumption() throws ParseException {
    ArrayList<Location> map;
    Player player;
    int storeroomSizeBefore;
    int storeroomSizeAfter;
    int containerSizeBefore;
    int containerSizeAfter;

    // Initialise player
    server.handleCommand("Daniel: look");
    map = server.getMap(0);
    player = server.getPlayer(0);
    // Store sizes of storeroom and locations' furniture before running consumption
    storeroomSizeBefore = map.get(5).getFurniture().size();
    containerSizeBefore = map.get(0).getFurniture().size();
    // Run the consumption. First element in furniture in first location should move to storeroom
    server.consumption(0, map.get(0).getFurniture().get(0), map.get(0).getFurniture());
    // Store sizes of storeroom and locations' furniture after running consumption
    storeroomSizeAfter = map.get(5).getFurniture().size();
    containerSizeAfter = map.get(0).getFurniture().size();

    // Check that a single piece of furniture has moved from location to storeroom
    assertEquals(storeroomSizeAfter, storeroomSizeBefore + 1);
    assertEquals(containerSizeAfter, containerSizeBefore - 1);

    // Add item to player's inventory
    server.handleCommand("Daniel: get axe");
    // Store sizes of storeroom's artefacts and player's inventory before running consumption
    storeroomSizeBefore = map.get(5).getArtefacts().size();
    containerSizeBefore = player.getInventory().size();
    // Run the consumption. First element in player's inventory should move to storeroom
    server.consumption(0, player.getInventory().get(0), player.getInventory());
    // Store sizes of storeroom's artefacts and player's inventory after running consumption
    storeroomSizeAfter = map.get(5).getArtefacts().size();
    containerSizeAfter = player.getInventory().size();

    // Check that a single piece of furniture has moved from location to storeroom
    assertEquals(storeroomSizeAfter, storeroomSizeBefore + 1);
    assertEquals(containerSizeAfter, containerSizeBefore - 1);
  }

  @Test
  void testCommandGenerator() {
    System.out.println(VariableCommandGenerator.generateRandomCommand(Arrays.asList("chop", "cut"), "axe", "tree"));
    System.out.println(VariableCommandGenerator.generateRandomCommand(Arrays.asList("chop", "cut"), "axe", "tree"));
    System.out.println(VariableCommandGenerator.generateRandomCommand(Arrays.asList("chop", "cut"), "axe", "tree"));
    System.out.println(VariableCommandGenerator.generateRandomCommand(Arrays.asList("pay"), "elf"));
    System.out.println(VariableCommandGenerator.generateRandomCommand(Arrays.asList("bridge"), "log", "river"));
  }


  @Test
  void testProduction() throws ParseException {
    ArrayList<Location> map;
    Player player;
    int storeroomSizeBefore;
    int storeroomSizeAfter;
    int destinationSizeBefore;
    int destinationSizeAfter;

    // Initialise player & map
    server.handleCommand("Daniel: look");
    map = server.getMap(0);
    player = server.getPlayer(0);

    /* -------- Move furniture from storeroom to location -------- */
    // Store sizes of storeroom and locations' furniture before running production
    storeroomSizeBefore = map.get(5).getFurniture().size();
    destinationSizeBefore = map.get(0).getFurniture().size();
    // Run the production. First element in furniture in storeroom should move to location's furniture.
    server.production(0, map.get(5).getFurniture().get(0), map.get(0).getFurniture());
    // Store sizes of storeroom and locations' furniture after running production
    storeroomSizeAfter = map.get(5).getFurniture().size();
    destinationSizeAfter = map.get(0).getFurniture().size();
    // Check that a single piece of furniture has moved from storeroom to location
    assertEquals(storeroomSizeAfter, storeroomSizeBefore - 1);
    assertEquals(destinationSizeAfter, destinationSizeBefore + 1);

    /* -------- Move artefact from storeroom to inventory -------- */
    // Store sizes of storeroom's artefacts and player's inventory before running production
    storeroomSizeBefore = map.get(5).getArtefacts().size();
    destinationSizeBefore = player.getInventory().size();
    // Run the production. First element in player's inventory should move to storeroom
    server.production(0, map.get(5).getArtefacts().get(0), player.getInventory());
    // Store sizes of storeroom's artefacts and player's inventory after running production
    storeroomSizeAfter = map.get(5).getArtefacts().size();
    destinationSizeAfter = player.getInventory().size();
    // Check that a single piece of furniture has moved from location to storeroom
    assertEquals(storeroomSizeAfter, storeroomSizeBefore - 1);
    assertEquals(destinationSizeAfter, destinationSizeBefore + 1);

    /* -------- Move character from storeroom to location -------- */
    // Store sizes of storeroom and location's characters before running production
    storeroomSizeBefore = map.get(5).getCharacters().size();
    destinationSizeBefore = map.get(0).getCharacters().size();
    // Run the production. First element in characters in storeroom should move to location's characters.
    server.production(0, map.get(5).getCharacters().get(0), map.get(0).getCharacters());
    // Store sizes of storeroom and locations' characters after running production
    storeroomSizeAfter = map.get(5).getCharacters().size();
    destinationSizeAfter = map.get(0).getCharacters().size();
    // Check that a single piece of characters has moved from storeroom to location
    assertEquals(storeroomSizeAfter, storeroomSizeBefore - 1);
    assertEquals(destinationSizeAfter, destinationSizeBefore + 1);
  }

  @Test
  void testLookCommand() throws ParseException {
    String response = server.handleCommand("Daniel: look");
    assertTrue(response.contains("You are in "), "No location returned by `look`");
    assertTrue((response.contains("There are the following artefacts in this location") ||
            response.contains("There are no artefacts in this location")), "No artefacts returned by `look`");
    assertTrue((response.contains("There are paths to the following locations") ||
            response.contains("There are no paths from here")), "No paths returned by `look`");
  }

  @Test
  void testInventoryCommand() throws ParseException {
    String response = server.handleCommand("Daniel: inv");
    assertTrue((response.contains("You have the following items in your inventory") ||
            response.contains("You have no items in your inventory")), "Inventory not listed");
  }

  @Test
  void testGetCommand() throws ParseException {
    String response = server.handleCommand("Daniel: get potion");
    assertTrue(response.contains("You now have 'potion' in your inventory"));
    response = server.handleCommand("Daniel: get asdf");
    assertTrue(response.contains("You must provide a valid item to pick up"));
    response = server.handleCommand("Daniel: get");
    assertTrue(response.contains("You must provide a valid item to pick up"));
  }

  

  @Test
  void testGotoCommand() throws ParseException {
    String response = server.handleCommand("Daniel: goto");
    assertTrue(response.contains("You must provide a valid location you wish to move to"));
    response = server.handleCommand("Daniel: goto asdf");
    assertTrue(response.contains("You must provide a valid location you wish to move to"));
    response = server.handleCommand("Daniel: goto forest");
 
    System.out.println("this is the response: " + response);
    assertTrue(response.contains("You have moved to forest"));
  }

  @Test
  void testResetCommand() throws ParseException {
    String response = server.handleCommand("Daniel: reset asdf");
    assertTrue(response.contains("Game reset"));
    response = server.handleCommand("Daniel: goto forest");
    assertTrue(response.contains("You have moved to"));
    response = server.handleCommand("Daniel: get key");
    assertTrue(response.contains("You now have 'key' in your inventory"));
    response = server.handleCommand("Daniel: reset");
    assertTrue(response.contains("Game reset"));
    response = server.handleCommand("Daniel: look");
    assertTrue(response.contains("cabin"));
    response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("You have no items in your inventory"));
  }

  @Test
    void testCommandTokenisation() {
        Set<String> filteredCommand = server.filterCommand("This is a command! There is some random punctuation :)");
        Set<String> expectedOutput = new HashSet<>();
        expectedOutput.add("command");
        expectedOutput.add("random");
        expectedOutput.add("punctuation");
        assertEquals(expectedOutput, filteredCommand, "Incorrect Filtering");
    }

    @Test
    void testCommandTokenisationTooManySpaces() {
        Set<String> filteredCommand = server.filterCommand("This    command has  too many     spaces");
        Set<String> expectedOutput = new HashSet<>();
        expectedOutput.add("command");
        expectedOutput.add("many");
        expectedOutput.add("spaces");
        assertEquals(expectedOutput, filteredCommand, "Incorrect Filtering");
    }

  
}
