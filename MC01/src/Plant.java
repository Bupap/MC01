public class Plant {
    private String name;
    private int price;
    private int yield;
    private int maxGrowth;
    private int currentGrowth;
    private String preferredSoil;
    private int cropPrice;
    private boolean watered;

    public Plant(String name, int price, int yield, int maxGrowth, String preferredSoil, int cropPrice) {
        this.name = name;
        this.price = price;
        this.yield = yield;
        this.maxGrowth = maxGrowth;
        this.preferredSoil = preferredSoil;
        this.cropPrice = cropPrice;
        this.currentGrowth = 0;
        this.watered = false;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getYield() { return yield; }
    public int getCropPrice() { return cropPrice; }
    public int getMaxGrowth() { return maxGrowth; }
    public String getPreferredSoil() { return preferredSoil; }
    public boolean isFullyGrown() { return currentGrowth >= maxGrowth; }
    public boolean isWatered() { return watered; }

    public void setWatered(boolean watered) { this.watered = watered; }

    public void grow(boolean isPreferredSoil, boolean isFertilized) {
        if (watered && !isFullyGrown()) {
            currentGrowth++;
            if (isPreferredSoil) {
                currentGrowth++;
            }
            if (isFertilized) {
                currentGrowth++;
            }
            if (currentGrowth > maxGrowth) {
                currentGrowth = maxGrowth;
            }
        }
    }

    public boolean prefers(String soilName) {
        return this.preferredSoil.equalsIgnoreCase(soilName);
    }
}