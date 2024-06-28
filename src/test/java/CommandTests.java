package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

}
