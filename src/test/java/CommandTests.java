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
      server.validSubjects.addAll(Arrays.asList("potion", "forest", "key"));
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
    assertTrue(response.contains("You must provide a valid item to pick up"));
    response = server.handleCommand("Daniel: get");
    assertTrue(response.contains("You must provide a valid item to pick up"));
  }

  

  // @Test
  // void testGotoCommand() throws ParseException {
  //   String response = server.handleCommand("Daniel: goto");
  //   assertTrue(response.contains("You must provide a valid location you wish to move to"));
  //   response = server.handleCommand("Daniel: goto asdf");
  //   assertTrue(response.contains("You must provide a valid location you wish to move to"));
  //   response = server.handleCommand("Daniel: goto forest");
  //   List<Set<String>> command = server.produceValidCommand(server.filterCommand("Daniel: goto forest"));
  //   for (Set<String> set : command) {
  //     for (String str : set) {
  //         System.out.println(str);
  //     }
  // }
  //   System.out.println("this is the response: " + response);
  //   assertTrue(response.contains("You have moved to forest"));
  // }

  // @Test
  // void testResetCommand() throws ParseException {
  //   String response = server.handleCommand("Daniel: reset asdf");
  //   assertTrue(response.contains("To reset the game please simply type 'reset'"));
  //   // response = server.handleCommand("Daniel: goto forest");
  //   // assertTrue(response.contains("You have moved to"));
  //   response = server.handleCommand("Daniel: get key");
  //   assertTrue(response.contains("You now have 'key' in your inventory"));
  //   response = server.handleCommand("Daniel: reset");
  //   assertTrue(response.contains("Game reset"));
  //   response = server.handleCommand("Daniel: look");
  //   assertTrue(response.contains("cabin"));
  //   response = server.handleCommand("Daniel: inv");
  //   assertTrue(response.contains("You have no items in your inventory"));
  // }

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
