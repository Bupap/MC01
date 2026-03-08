import java.util.Scanner;
import java.util.Map;

public class MainMenu {
    private Scanner scanner;
    private Map<String, HighScoreEntry> highScores;

    public MainMenu() {
        this.scanner = new Scanner(System.in);
        try {
            this.highScores = JsonLoader.loadHighScores("HighScores.json");
        } catch (Exception e) {
            System.out.println("Error loading high scores.");
        }
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== VERDANT SUN PLANTING SIMULATOR ===");
            System.out.println("[1] New Game");
            System.out.println("[2] View High Scores");
            System.out.println("[3] Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    startNewGame();
                    break;
                case "2":
                    displayHighScores();
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void startNewGame() {
        VerdantSun game = new VerdantSun();
        game.start();
    }

    private void displayHighScores() {
        System.out.println("\n--- Hall of Fame ---");
        if (highScores == null || highScores.isEmpty()) {
            System.out.println("No high scores recorded yet.");
            return;
        }
        highScores.forEach((rank, entry) -> {
            System.out.println(rank + ". " + entry.getName() + " - $" + entry.getSavings());
        });
    }

    public static void main(String[] args) {
        MainMenu menu = new MainMenu();
        menu.show();
    }
}
