import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class VariableCommandGenerator {
    private static final Random RANDOM = new Random();

    public static String generateRandomCommand(List<String> triggers, String subjectUsed, String subjectActedOn ) {
        String trigger = getRandomTrigger(triggers);
        String subject1 = subjectUsed;
        String subject2 = subjectActedOn;
        List<String> randomCommands;
        // Possible command structures
        if ("bridge".equals(trigger)) {
            randomCommands = Arrays.asList(
                String.format("build bridge with %s", subject1),
                String.format("build bridge with %s", subject2),
                String.format("build bridge with %s and %s", subject1, subject2),
                String.format("use %s and %s to build a bridge", subject1, subject2)
            );
        }else{
            randomCommands = Arrays.asList(
                String.format("%s %s with %s", trigger, subject2, subject1),
                String.format("%s that %s", trigger, subject2),
                String.format("use the %s on the %s", subject1, subject2),
                String.format("%s %s using the %s", trigger, subject2, subject1),
                String.format("%s %s with the %s", trigger, subject2, subject1),
                String.format("using the %s, %s the %s", subject1, trigger, subject2),
                String.format("you know the %s ? Use it to %s the %s", trigger, subject1, subject2)
            );
        }
        return getRandomCommand(randomCommands);
    }

    public static String generateRandomCommand(List<String> triggers, String subject) {
        String trigger = getRandomTrigger(triggers);
        List<String> randomCommands = Arrays.asList(
            String.format("%s %s", trigger, subject),
            String.format("%s that %s", trigger, subject),
            String.format("I want to %s the %s", trigger, subject),
            String.format("The %s over there, I want to %s it", subject, trigger)
        );
        return getRandomCommand(randomCommands);
    }

    private static String getRandomTrigger(List<String> triggers){
        int index = RANDOM.nextInt(triggers.size());
        return triggers.get(index);
    }

    private static String getRandomCommand(List<String> randomCommands){
        int index = RANDOM.nextInt(randomCommands.size());
        return randomCommands.get(index);
    }

    
}


