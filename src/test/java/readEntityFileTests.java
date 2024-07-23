package edu.uob;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.alexmerz.graphviz.ParseException;


final class readEntityFileTests {

  private GameServer server;

  // This method is automatically run _before_ each of the @Test methods
  @BeforeEach
  void setup() {
      server = new GameServer();
  }

  @Test
  void testCorrectLocations() throws ParseException {
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
  }


