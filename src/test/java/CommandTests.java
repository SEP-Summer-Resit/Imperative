package edu.uob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class CommandTests {

  private GameServer server;

  // This method is automatically run _before_ each of the @Test methods
  @BeforeEach
  void setup() {
      server = new GameServer();
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
