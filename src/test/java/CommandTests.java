package edu.uob;

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
  void testLookCommand() throws ParseException {
    String response = server.handleCommand("Daniel: look");
    assertTrue(response.contains("You are in "), "No location returned by `look`");
    assertTrue((response.contains("There are the following artefacts in this location") ||
            response.contains("There are no artefacts in this location")), "No artefacts returned by `look`");
    assertTrue((response.contains("There are paths to the following locations") ||
            response.contains("There are no paths from here")), "No paths returned by `look`");
    //decorative command test
    response = server.handleCommand("Daniel: please cam I look over there");
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
    //decorative command test
    response = server.handleCommand("Daniel: can I see whats in my inv?");
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
    response = server.handleCommand("Daniel: reset");
    //test inverted command
    response = server.handleCommand("Daniel: the potion, I would like to get it");
    assertTrue(response.contains("You now have 'potion' in your inventory"));
    response = server.handleCommand("Daniel: reset");
    //test decorated command
    response = server.handleCommand("Daniel: I'd like to get that lovely green potion");
    assertTrue(response.contains("You now have 'potion' in your inventory"));

  }

  

  @Test
  void testGotoCommand() throws ParseException {
    String response = server.handleCommand("Daniel: goto");
    assertTrue(response.contains("You must provide a valid location you wish to move to"));
    response = server.handleCommand("Daniel: goto asdf");
    assertTrue(response.contains("You must provide a valid location you wish to move to"));
    response = server.handleCommand("Daniel: goto forest");
    assertTrue(response.contains("You have moved to forest"));
    response = server.handleCommand("Daniel: reset");
    //decorated command
    response = server.handleCommand("Daniel: I want to goto the forest");
    assertTrue(response.contains("You have moved to forest"));
  }

  //tests that the reset works and that once you have picked up an item or moved you cant
  //do the same again
  @Test
  void testResetCommand() throws ParseException {
    String response = server.handleCommand("Daniel: reset asdf");
    assertTrue(response.contains("Game reset"));
    response = server.handleCommand("Daniel: goto forest");
    assertTrue(response.contains("You have moved to forest"));
    response = server.handleCommand("Daniel: goto forest");
    assertTrue(response.contains("You must provide a valid location you wish to move to"));
    response = server.handleCommand("Daniel: get key");
    assertTrue(response.contains("You now have 'key' in your inventory"));
    response = server.handleCommand("Daniel: get key");
    assertTrue(response.contains("You must provide a valid item to pick up"));
    response = server.handleCommand("Daniel: reset");
    assertTrue(response.contains("Game reset"));
    response = server.handleCommand("Daniel: look");
    assertTrue(response.contains("cabin"));
    response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("You have no items in your inventory"));
  }

  @Test
  void testAmbiguousCommandNotAccepted() throws ParseException {
    String response = server.handleCommand("Daniel: can I goto the forest and get potion");
    assertTrue(response.contains("Please give one valid command."));
    response = server.handleCommand("Daniel:can I get the axe and the potion");
    assertTrue(response.contains("You can only pick up one artefact at a time."));
    response = server.handleCommand("Daniel: I want to look around and goto the forest");
    assertTrue(response.contains("Please give one valid command."));
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
