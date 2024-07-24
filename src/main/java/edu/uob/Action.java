package edu.uob;

import java.util.List;

public class Action {
    private List<String> triggers; // List of strings that can trigger the action
    private List<String> subjects; // List of entities that the action can be performed on
    private List<String> consumed; // List of entities that are consumed by the action
    private List<String> produced; // List of entities that are produced by the action
    private String narration; // String that describes the action

    public Action(List<String> triggers, List<String> subjects, List<String> consumed, List<String> produced, String narration) {
        this.triggers = triggers;
        this.subjects = subjects;
        this.consumed = consumed;
        this.produced = produced;
        this.narration = narration;
    }

    public void addTrigger(String trigger) {
        triggers.add(trigger);
    }

    public void addSubject(String subject) {
        subjects.add(subject);
    }

    public void addConsumed(String consumedEntity) {
        consumed.add(consumedEntity);
    }

    public void addProduced(String producedEntity) {
        produced.add(producedEntity);
    }

    public void addNarration(String narration) {
        this.narration = narration;
    }

    public List<String> getTriggers() {
        return triggers;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public List<String> getConsumed() {
        return consumed;
    }

    public List<String> getProduced() {
        return produced;
    }

    public String getNarration() {
        return narration;
    }

}
