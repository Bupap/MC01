public class Soil {
    private String type;
    private Plant plant;
    private Fertilizer fertilizer;
    private boolean hasMeteorite;
    private boolean permanentlyFertilized;

    public Soil(String type) {
        this.type = type;
        this.plant = null;
        this.fertilizer = null;
        this.hasMeteorite = false;
        this.permanentlyFertilized = false;
    }

    public String getType() { return type; }
    public Plant getPlant() { return plant; }
    public Fertilizer getFertilizer() { return fertilizer; }
    public boolean hasMeteorite() { return hasMeteorite; }

    public void setPlant(Plant plant) { this.plant = plant; }
    public void setFertilizer(Fertilizer fertilizer) { this.fertilizer = fertilizer; }

    public void hitByMeteorite() {
        this.hasMeteorite = true;
        this.plant = null;
    }

    public void excavate() {
        this.hasMeteorite = false;
        this.permanentlyFertilized = true;
    }

    public boolean isFertilized() {
        return permanentlyFertilized || (fertilizer != null && !fertilizer.isDepleted());
    }

    public void processDayEnd() {
        if (plant != null) {
            boolean isPreferred = plant.prefers(this.type);
            boolean fertilized = isFertilized();

            int initialGrowth = plant.isFullyGrown() ? 1 : 0;

            plant.grow(isPreferred, fertilized);

            if (plant.isWatered() && !plant.isFullyGrown() && fertilizer != null && !permanentlyFertilized) {
                fertilizer.reduceEffect();
                if (fertilizer.isDepleted()) {
                    fertilizer = null;
                }
            }
            plant.setWatered(false);
        }
    }
}