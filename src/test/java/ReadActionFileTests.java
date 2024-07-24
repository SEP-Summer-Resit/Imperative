package edu.uob;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alexmerz.graphviz.ParseException;

public class ReadActionFileTests {
  private GameServer server;

  // This method is automatically run _before_ each of the @Test methods
  @BeforeEach
  void setup() {
      server = new GameServer();
  }

  @Test
  void testTriggers() throws ParseException {
        ArrayList<Action> actions = server.loadActionsFile("actions.xml");
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
          ArrayList<Action> actions = server.loadActionsFile("actions.xml");
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
            ArrayList<Action> actions = server.loadActionsFile("actions.xml");
            String[] expectedConsumeds = {"key", "potion", "tree", "health", "coin", "log", "ground"};
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
              ArrayList<Action> actions = server.loadActionsFile("actions.xml");
              String[] expectedProduceds = {"cellar", "log", "health", "shovel", "clearing", "gold", "hole", "lumberjack"};
              Set<String> actualProduceds = new HashSet<>();
              for (Action action : actions) {
                  actualProduceds.addAll(action.getProduced());
              }
      
              for (String expectedProduced : expectedProduceds) {
                  assertTrue(actualProduceds.contains(expectedProduced), "Produced '" + expectedProduced + "' should be present.");
              }
          }

          @Test
          void testNarration() throws ParseException {
                ArrayList<Action> actions = server.loadActionsFile("actions.xml");
                String[] expectedNarrations = {"You unlock the door and see steps leading down into a cellar"};
                Set<String> actualNarrations = new HashSet<>();
                for (Action action : actions) {
                    actualNarrations.add(action.getNarration());
                }
        
                for (String expectedNarration : expectedNarrations) {
                    assertTrue(actualNarrations.contains(expectedNarration), "Narration '" + expectedNarration + "' should be present.");
                }
            }
}


