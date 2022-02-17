package com.l2jserver.gameserver.model.quest;

public class State {
    public static final int CREATED = 0;
    public static final int STARTED = 1;
    public static final int COMPLETED = 2;

    /**
     * Get the quest state's string representation from its byte value.
     */
    public static String getStateName(int state) {
        return switch (state) {
            case STARTED -> "Started";
            case COMPLETED -> "Completed";
            default -> "Start";
        };
    }

    /**
     * Get the quest state's byte value from its string representation.
     *
     * @param statename the String representation of the state
     * @return the byte value of the quest state (default: 0)
     */
    public static int getStateId(String statename) {
        return switch (statename) {
            case "Started" -> 1;
            case "Completed" -> 2;
            default -> 0;
        };
    }
}
