package edu.uob;

import java.util.List;

public class Action {
    private List<String> triggers; // List of strings that can trigger the action
    private List<Entity> subjects; // List of entities that the action can be performed on
    private List<Entity> consumed; // List of entities that are consumed by the action
    private List<Entity> produced; // List of entities that are produced by the action
    private String narration; // String that describes the action

    public Action(List<String> triggers, List<Entity> subjects, List<Entity> consumed, List<Entity> produced, String narration) {
        this.triggers = triggers;
        this.subjects = subjects;
        this.consumed = consumed;
        this.produced = produced;
        this.narration = narration;
    }
    
}
