import java.util.Scanner;
import java.util.Map;
import org.json.JSONObject;

public class VerdantSun {
    private Soil[][] field;
    private int savings;
    private int currentDay;
    private WateringCan wateringCan;
    private Map<String, Plant> availablePlants;
    private Map<String, Fertilizer> availableFertilizers;
    private Scanner scanner;

    public VerdantSun() {
        this.field = new Soil[10][10];
        this.savings = 1000;
        this.currentDay = 1;
        this.wateringCan = new WateringCan();
        this.scanner = new Scanner(System.in);
        loadInitialData();
    }

    private void loadInitialData() {
        try {
            this.availablePlants = JsonLoader.loadPlants("Plants.json"); //checks available plants
            this.availableFertilizers = JsonLoader.loadFertilizers("Fertilizers.json"); //checks available fertz
            String[][] gridLayout = JsonLoader.loadMapGrid("Map.json");
            Map<String, String> legend = JsonLoader.loadMapLegend("Map.json");

            for (int r = 0; r < 10; r++) { //loop creates grid
                for (int c = 0; c < 10; c++) {
                    String symbol = gridLayout[r][c];
                    String type = legend.get(symbol);
                    field[r][c] = new Soil(type);
                }
            }
        } catch (Exception e) { //failsafe
            System.out.println("Error loading game data.");
        }
    }

    public void start() {
        while (currentDay <= 15) {
            displayField();
            displayStatus();
            System.out.println("[1] Plant \n[2] Harvest/Remove \n[3] Water \n[4] Refill \n[5] Fertilize \n[6] Next Day");
            System.out.print("Action: ");
            String action = scanner.nextLine();

            switch (action) { //menu
                case "1": handlePlanting(); break;
                case "2": handleBulkRemove(); break;
                case "3": handleBulkWatering(); break;
                case "4": handleRefill(); break;
                case "5": handleFertilizing(); break;
                case "6": advanceDay(); break;
                default: System.out.println("Invalid action.");
            }
        }
        endGame();
    }

    private void displayField() {
        System.out.println("\n   1 2 3 4 5 6 7 8 9 10");//loop displays field
        for (int r = 0; r < 10; r++) {
            System.out.printf("%2d ", (r + 1));
            for (int c = 0; c < 10; c++) {
                Soil s = field[r][c];
                if (s.hasMeteorite()) System.out.print("M ");
                else if (s.getPlant() != null) System.out.print("P ");
                else System.out.print(". ");
            }
            System.out.println();
        }
    }

    private void handlePlanting() {
        availablePlants.forEach((id, p) -> System.out.println("- " + id + " ($" + p.getPrice() + ")"));
        System.out.print("Enter ID and coords (id r c): ");
        String[] parts = scanner.nextLine().split(" ");
        if (parts.length < 3) return;

        String id = parts[0];
        int r = Integer.parseInt(parts[1]) - 1;
        int c = Integer.parseInt(parts[2]) - 1;

        if (isValidCoord(r, c) && availablePlants.containsKey(id)) {
            Plant p = availablePlants.get(id);
            if (savings >= p.getPrice() && field[r][c].getPlant() == null && !field[r][c].hasMeteorite()) {
                field[r][c].setPlant(new Plant(p.getName(), p.getPrice(), p.getYield(), p.getMaxGrowth(), p.getPreferredSoil(), p.getCropPrice()));
                savings -= p.getPrice();
            }
        }
    }

    private void handleBulkWatering() {
        System.out.print("Enter tiles to water (r1 c1 r2 c2 ...): ");
        String[] parts = scanner.nextLine().split(" ");
        for (int i = 0; i < parts.length - 1; i += 2) {
            int r = Integer.parseInt(parts[i]) - 1;
            int c = Integer.parseInt(parts[i+1]) - 1;
            if (isValidCoord(r, c) && wateringCan.canWater() && field[r][c].getPlant() != null) {
                field[r][c].getPlant().setWatered(true);
                wateringCan.use();
            }
        }
    }

    private void handleBulkRemove() {
        System.out.print("Enter tiles to harvest/remove (r1 c1 r2 c2 ...): ");
        String[] parts = scanner.nextLine().split(" ");
        for (int i = 0; i < parts.length - 1; i += 2) {
            int r = Integer.parseInt(parts[i]) - 1;
            int c = Integer.parseInt(parts[i+1]) - 1;
            if (isValidCoord(r, c)) processHarvestOrRemove(r, c);
        }
    }

    private void processHarvestOrRemove(int r, int c) {
        Soil soil = field[r][c];
        if (soil.getPlant() != null) {
            Plant p = soil.getPlant();
            if (p.isFullyGrown()) {
                int earnings = p.getYield() * p.getCropPrice();
                savings += earnings;
                System.out.println("Harvested " + p.getName() + " at (" + (r+1) + "," + (c+1) + ") for $" + earnings);
            } else {
                System.out.println("Removed immature " + p.getName());
            }
            soil.setPlant(null);
        } else if (soil.hasMeteorite()) {
            if (savings >= 500) {
                savings -= 500;
                soil.excavate();
                System.out.println("Meteorite excavated at (" + (r+1) + "," + (c+1) + ")");
            }
        }
    }

    private void handleFertilizing() {
        availableFertilizers.forEach((id, f) -> System.out.println("- " + id + " ($" + f.getPrice() + ")"));
        System.out.print("Enter fertilizer ID and coords (id r c): ");
        String[] parts = scanner.nextLine().split(" ");
        if (parts.length < 3) return;

        String id = parts[0];
        int r = Integer.parseInt(parts[1]) - 1;
        int c = Integer.parseInt(parts[2]) - 1;

        if (isValidCoord(r, c) && availableFertilizers.containsKey(id)) {
            Fertilizer f = availableFertilizers.get(id);
            if (field[r][c].getFertilizer() != null) {
                System.out.println("Error: This plot already has fertilizer applied.");
                return;
            }
            if (savings >= f.getPrice()) {
                savings -= f.getPrice();
                field[r][c].setFertilizer(new Fertilizer(f.getName(), f.getPrice(), f.getEffectDays()));
            }
        }
    }

    private void handleRefill() {
        if (savings >= 100) {
            savings -= 100;
            wateringCan.refill();
        } else {
            System.out.println("Insufficient funds to refill watering can ($100 required).");
        }
    }

    private void advanceDay() {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                field[r][c].processDayEnd();
            }
        }
        if (currentDay == 7) triggerMeteoriteEvent();
        savings += 50;
        currentDay++;
    }

    private void triggerMeteoriteEvent() {
        int r = (int) (Math.random() * 10);
        int c = (int) (Math.random() * 10);
        field[r][c].hitByMeteorite();
        System.out.println("!!! A meteorite struck at row " + (r + 1) + ", col " + (c + 1) + " !!!");
    }

    private boolean isValidCoord(int r, int c) {
        return r >= 0 && r < 10 && c >= 0 && c < 10;
    }

    private void displayStatus() {
        System.out.println("Day: " + currentDay + " | Savings: $" + savings + " | Water: " + wateringCan.getCurrentLevel());
    }

    private void endGame() {
        System.out.println("Season Over! Final Savings: $" + savings);
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        try { updateHighScores(name, savings); } catch (Exception e) {}
    }

    private void updateHighScores(String name, int score) throws Exception {
        Map<String, HighScoreEntry> scores = JsonLoader.loadHighScores("HighScores.json");
        scores.put("New", new HighScoreEntry(name, score));
        JSONObject root = new JSONObject();
        int rank = 1;
        for (HighScoreEntry entry : scores.values()) {
            JSONObject s = new JSONObject();
            s.put("name", entry.getName());
            s.put("savings", entry.getSavings());
            root.put(String.valueOf(rank++), s);
            if (rank > 10) break;
        }
        java.nio.file.Files.write(java.nio.file.Paths.get("HighScores.json"), root.toString(2).getBytes());
    }
}