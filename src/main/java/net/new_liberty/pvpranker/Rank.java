package net.new_liberty.pvpranker;

/**
 * Represents a PvP Rank.
 */
public class Rank {
    private final String id;

    private final String name;

    private final int score;

    public Rank(String id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
