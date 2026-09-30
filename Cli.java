import java.util.Scanner;

public class Cli {
    private final Game game;

    public Cli(Game game) {
        this.game = game;
    }

    public void startListening() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("stop")) {
                if (game.getStoper() != null) {
                    game.getStoper().stopServer();
                }
                break;
            } else if (!input.isEmpty()) {
                game.sendCommand(input);
            }
        }
        scanner.close();
    }
}