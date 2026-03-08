public class Fertilizer {
    private String name;
    private int price;
    private int effectDays;

    public Fertilizer(String name, int price, int effectDays) {
        this.name = name;
        this.price = price;
        this.effectDays = effectDays;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getEffectDays() { return effectDays; }

    public void reduceEffect() {
        if (effectDays > 0) {
            effectDays--;
        }
    }

    public boolean isDepleted() {
        return effectDays <= 0;
    }
}