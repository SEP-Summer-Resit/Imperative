package edu.uob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
  void testLookCommand() {
    String response = server.handleCommand("Daniel: look");
    assertTrue(response.contains("The location you are currently in is"), "No location returned by `look`");
    assertTrue(response.contains("There are the following artefacts in this location"), "No artefacts returned by `look`");
    assertTrue(response.contains("There are paths to the following locations"), "No paths returned by `look`");
  }

  @Test
  void testInventoryCommand() {
    String response = server.handleCommand("Daniel: inv");
    assertTrue(response.contains("You have the following items in your inventory"), "Inventory not listed");
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
