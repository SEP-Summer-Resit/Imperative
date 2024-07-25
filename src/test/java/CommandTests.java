package edu.uob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class CommandTests {

  private GameServer server;

  // This method is automatically run _before_ each of the @Test methods
  @BeforeEach
  void setup() {
      server = new GameServer();
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
    assertTrue(response.contains("is not available to take"));
    response = server.handleCommand("Daniel: get");
    assertTrue(response.contains("You must provide a valid artefact you wish to get"));
  }

  @Test
  void testDropCommand() throws ParseException {
    String response = server.handleCommand("Daniel: drop potion");
    assertTrue(response.contains("is not in your inventory"));
    server.handleCommand("Daniel: get potion");
    response = server.handleCommand("Daniel: drop potion");
    System.out.println("LOWE " + response);
    assertTrue(response.contains("You have dropped"));
    response = server.handleCommand("Daniel: drop");
    assertTrue(response.contains("You must provide a valid artefact you wish to drop from your inventory"));
  }

  @Test
  void testGotoCommand() throws ParseException {
    String response = server.handleCommand("Daniel: goto");
    assertTrue(response.contains("You must provide a valid location you wish to move to"));
    response = server.handleCommand("Daniel: goto asdf");
    assertTrue(response.contains("is not a location you can travel to"));
    response = server.handleCommand("Daniel: goto forest");
    assertTrue(response.contains("You have moved to"));
  }

  @Test
  void testResetCommand() throws ParseException {
    String response = server.handleCommand("Daniel: reset asdf");
    assertTrue(response.contains("To reset the game please simply type 'reset'"));
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
    String filteredCommand = server.filterCommand("This is a command! There is some random punctuation :)");
    String expectedOutput = "command random punctuation";
    assertEquals(expectedOutput, filteredCommand, "Incorrect Filtering");
    }

    @Test
    void testCommandTokenisationTooManySpaces() {
      String filteredCommand = server.filterCommand("This    command has  too many     spaces");
      String expectedOutput = "command many spaces";
      assertEquals(expectedOutput, filteredCommand, "Incorrect Filtering");
      }
 

}
