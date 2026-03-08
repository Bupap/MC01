public class WateringCan {
    private final int MAX_LEVEL = 10;
    private int currentLevel;

    public WateringCan() {
        this.currentLevel = MAX_LEVEL;
    }

    public int getCurrentLevel() { return currentLevel; }

    public boolean canWater() { return currentLevel > 0; }

    public void use() {
        if (currentLevel > 0) {
            currentLevel--;
        }
    }

    public void refill() {
        this.currentLevel = MAX_LEVEL;
    }
}