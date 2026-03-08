import java.util.Scanner;
import java.util.Map;
import org.json.JSONObject;

public class VerdantSun {
    private Soil[][] field;
    private int savings;
    private int currentDay;
    private int excavationsToday;
    private WateringCan wateringCan;
    private Map<String, Plant> availablePlants;
    private Map<String, Fertilizer> availableFertilizers;
    private Scanner scanner;

    private final int[][] METEORITE_PATTERN = {
            {1,1}, {1,4}, {1,5}, {1,8},
            {3,3}, {3,4}, {3,5}, {3,6},
            {4,1}, {4,3}, {4,4}, {4,5}, {4,6}, {4,8},
            {5,1}, {5,3}, {5,4}, {5,5}, {5,6}, {5,8},
            {6,3}, {6,4}, {6,5}, {6,6},
            {8,1}, {8,4}, {8,5}, {8,8}
    };

    public VerdantSun() {
        this.field = new Soil[10][10];
        this.savings = 1000;
        this.currentDay = 1;
        this.excavationsToday = 0;
        this.wateringCan = new WateringCan();
        this.scanner = new Scanner(System.in);
        loadInitialData();
    }

    private void loadInitialData() {
        try {
            this.availablePlants = JsonLoader.loadPlants("Plants.json");
            this.availableFertilizers = JsonLoader.loadFertilizers("Fertilizers.json");
            String[][] gridLayout = JsonLoader.loadMapGrid("Map.json");
            Map<String, String> legend = JsonLoader.loadMapLegend("Map.json");

            for (int r = 0; r < 10; r++) {
                for (int c = 0; c < 10; c++) {
                    String symbol = gridLayout[r][c];
                    String type = legend.get(symbol);
                    field[r][c] = new Soil(type);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading game data.");
        }
    }

    public void start() {
        while (currentDay <= 15) {
            displayField();
            displayStatus();

            System.out.println("[1] Plant [2] Harvest/Remove [3] Water [4] Refill [5] Fertilize [6] Next Day" + (currentDay > 7 ? " [7] Excavate" : ""));
            System.out.print("Action: ");
            String action = scanner.nextLine();

            switch (action) {
                case "1": handlePlanting(); break;
                case "2": handleBulkRemove(); break;
                case "3": handleBulkWatering(); break;
                case "4": handleRefill(); break;
                case "5": handleFertilizing(); break;
                case "6": advanceDay(); break;
                case "7": if (currentDay > 7) handleExcavate(); break;
                default: System.out.println("Invalid action.");
            }
        }
        endGame();
    }

    private void displayField() {
        System.out.println("\n    1 2 3 4 5 6 7 8 9 10");
        for (int r = 0; r < 10; r++) {
            System.out.printf("%2d ", (r + 1));
            for (int c = 0; c < 10; c++) {
                Soil s = field[r][c];
                if (s.hasMeteorite()) {
                    System.out.print("- ");
                } else if (s.getPlant() != null) {
                    //gets the first letter of the plant name
                    char symbol = s.getPlant().getName().toUpperCase().charAt(0);
                    System.out.print(symbol + " ");
                } else {
                    System.out.print(". ");
                }
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
            if (field[r][c].hasMeteorite()) {
                System.out.println("Cannot plant here! A meteorite is blocking the tile.");
                return;
            }
            Plant p = availablePlants.get(id);
            if (savings >= p.getPrice() && field[r][c].getPlant() == null) {
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
                System.out.println("Removed immature " + p.getName() + " at (" + (r+1) + "," + (c+1) + ")");
            }
            soil.setPlant(null);
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
            if (field[r][c].getFertilizer() != null) {
                System.out.println("Error: This plot already has fertilizer applied.");
                return;
            }
            Fertilizer f = availableFertilizers.get(id);
            if (savings >= f.getPrice()) {
                savings -= f.getPrice();
                field[r][c].setFertilizer(new Fertilizer(f.getName(), f.getPrice(), f.getEffectDays()));
            }
        }
    }

    private void handleExcavate() {
        System.out.print("Enter tiles to excavate (r1 c1 r2 c2 ...): ");
        String[] parts = scanner.nextLine().split(" ");
        for (int i = 0; i < parts.length - 1; i += 2) {
            if (excavationsToday >= 5) {
                System.out.println("Daily limit reached (5/5). Wait until tomorrow.");
                break;
            }
            int r = Integer.parseInt(parts[i]) - 1;
            int c = Integer.parseInt(parts[i+1]) - 1;

            if (isValidCoord(r, c) && field[r][c].hasMeteorite()) {
                if (savings >= 500) {
                    savings -= 500;
                    field[r][c].excavate();
                    field[r][c].setFertilizer(new Fertilizer("Permanent", 0, 999));
                    excavationsToday++;
                    System.out.println("Tile (" + (r+1) + "," + (c+1) + ") excavated and permanently fertilized.");
                } else {
                    System.out.println("Insufficient funds ($500) for tile (" + (r+1) + "," + (c+1) + ").");
                }
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
        excavationsToday = 0;
        if (currentDay == 7) triggerMeteoriteEvent();
        savings += 50;
        currentDay++;
    }

    private void triggerMeteoriteEvent() {
        System.out.println("!!! A METEORITE SHOWER HAS STRUCK THE FIELD !!!");
        for (int[] pos : METEORITE_PATTERN) {
            int r = pos[0];
            int c = pos[1];
            processHarvestOrRemove(r, c);
            field[r][c].hitByMeteorite();
        }
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