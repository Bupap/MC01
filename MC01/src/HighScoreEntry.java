public class HighScoreEntry {
    private String name;
    private int savings;

    public HighScoreEntry(String name, int savings) {
        this.name = name;
        this.savings = savings;
    }

    public String getName() { return name; }
    public int getSavings() { return savings; }
}
