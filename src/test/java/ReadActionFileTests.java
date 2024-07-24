package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alexmerz.graphviz.ParseException;

import edu.uob.GameServer;

public class ReadActionFileTests {
  private GameServer server;

  // This method is automatically run _before_ each of the @Test methods
  @BeforeEach
  void setup() {
      server = new GameServer();
  }

  @Test
  void testTriggers() throws ParseException {
        ArrayList<Actions> actions = server.readEntityFile("actions.xml");
        String[] expectedTriggers = {"open", "unlock", "chop", "cut", "cutdown", "fight", "hit", "attack", "drink", "pay", "bridge", "dig"};
        Set<String> actualTriggers = new HashSet<>();
        for (Action action : actions) {
            actualTriggers.addAll(action.getTriggers());
        }

        for (String expectedTrigger : expectedTriggers) {
            assertTrue(actualTriggers.contains(expectedTrigger), "Trigger '" + expectedTrigger + "' should be present.");
        }
    }

    @Test
    void testSubjects() throws ParseException {
          ArrayList<Actions> actions = server.readEntityFile("actions.xml");
          String[] expectedSubjects = {"trapdoor", "key", "tree", "axe", "potion", "elf", "coin", "log", "river"};
          Set<String> actualSubjects = new HashSet<>();
          for (Action action : actions) {
              actualSubjects.addAll(action.getSubjects());
          }

          for (String expectedSubject : expectedSubjects) {
              assertTrue(actualSubjects.contains(expectedSubject), "Subject '" + expectedSubject + "' should be present.");
          }
      }

      @Test
      void testConsumed() throws ParseException {
            ArrayList<Actions> actions = server.readEntityFile("actions.xml");
            String[] expectedConsumeds = {"open", "unlock", "chop", "cut", "cutdown", "fight", "hit", "attack", "drink", "pay", "bridge", "dig"};
            Set<String> actualConsumeds = new HashSet<>();
            for (Action action : actions) {
                actualConsumeds.addAll(action.getConsumed());
            }
    
            for (String expectedConsumed : expectedConsumeds) {
                assertTrue(actualConsumeds.contains(expectedConsumed), "Consumed '" + expectedConsumed + "' should be present.");
            }
        }

        @Test
        void testProduced() throws ParseException {
              ArrayList<Actions> actions = server.readEntityFile("actions.xml");
              String[] expectedProduceds = {"open", "unlock", "chop", "cut", "cutdown", "fight", "hit", "attack", "drink", "pay", "bridge", "dig"};
              Set<String> actualProduceds = new HashSet<>();
              for (Action action : actions) {
                  actualProduceds.addAll(action.getProduceds());
              }
      
              for (String expectedProduced : expectedProduceds) {
                  assertTrue(actualProduceds.contains(expectedProduced), "Produced '" + expectedProduced + "' should be present.");
              }
          }

          @Test
          void testNarration() throws ParseException {
                ArrayList<Actions> actions = server.readEntityFile("actions.xml");
                String[] expectedNarrations = {"open", "unlock", "chop", "cut", "cutdown", "fight", "hit", "attack", "drink", "pay", "bridge", "dig"};
                Set<String> actualNarrations = new HashSet<>();
                for (Action action : actions) {
                    actualNarrations.addAll(action.getNarrations());
                }
        
                for (String expectedNarration : expectedNarrations) {
                    assertTrue(actualNarrations.contains(expectedNarration), "Narration '" + expectedNarration + "' should be present.");
                }
            }



}


