// package edu.uob;

// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import com.alexmerz.graphviz.ParseException;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// final class ActionTests {

//   private GameServer server;

//   // This method is automatically run _before_ each of the @Test methods
//   @BeforeEach
//   void setup() {
//       server = new GameServer();
//   }

//   @Test
//   void testInitialChopTree() throws ParseException {
//     String response = server.handleCommand("Joe: chop tree axe");
//     assertTrue(response.contains("You do not have the required item to perform this action"), "There is no tree in the cabin");
//   }

//   @Test
//   void testInitialDrinkPotion() throws ParseException {
//     String response = server.handleCommand("Joe: drink potion");
//     assertTrue(response.contains("You drink the potion and your health improves"), "You should be able to drink the potion directly from the location");
//   }

//   @Test
//   void testInitialDrinkBanana() throws ParseException {
//     String response = server.handleCommand("Joe: drink banana");
//     assertFalse(response.contains("You drink"), "This should fail as there is no banana in the location");
//   }

//   @Test
//   void testInitialExplode() throws ParseException {
//     String response = server.handleCommand("Joe: explode");
//     assertTrue(response.contains("There is no action explode"), "You cant explode as explode is not a valid action");
//   }

// }
