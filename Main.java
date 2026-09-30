import Minecraft.Host;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[0m";
    private static final int CONSOLE_WIDTH = 60;

    public static void main(String[] args) {
        int mcPort = 25565;
        int webPort = 8080;

        Host host = new Host(mcPort);
        Game game = new Game(host);
        WebSOcket webSocket = new WebSOcket(webPort, host, game);

        game.start();
        webSocket.start();

        int pingVal = host.ping();
        int threads = Thread.activeCount();
        String ip = host.getIpAddress().getAddress();
        String startTime = getFormattedStartTime();

        printHeader(pingVal, threads, mcPort, ip, startTime);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            webSocket.stop();
            if (game.getStoper() != null) {
                game.getStoper().stopServer();
            }
        }));

        Cli cli = new Cli(game);
        cli.startListening();

        webSocket.stop();
        System.exit(0);
    }

    private static void printHeader(int ping, int threads, int port, String ip, String startTime) {
        String[] artLines = {
            "   __  ________        _    ",
            "  /  |/  / ___/     __| |__ ",
            " / /|_/ / /__| |/|/ / / _ \\",
            "/_/  /_/\\___/|__,__/_/_//_/"
        };

        System.out.println();
        for (String line : artLines) {
            System.out.println(CYAN + centerText(line, CONSOLE_WIDTH) + RESET);
        }
        System.out.println();

        String pingStr = (ping < 0) ? "N/A" : ping + "ms";
        String line1 = String.format("Server Ping : %s    Threads : %d", pingStr, threads);
        String line2 = String.format("Port : %d", port);
        String line3 = String.format("IP   : %s", ip);
        String line4 = String.format("-- %s --", startTime);

        System.out.println(CYAN + centerText(line1, CONSOLE_WIDTH) + RESET);
        System.out.println(CYAN + centerText(line2, CONSOLE_WIDTH) + RESET);
        System.out.println(CYAN + centerText(line3, CONSOLE_WIDTH) + RESET);
        System.out.println(CYAN + centerText(line4, CONSOLE_WIDTH) + RESET);
        System.out.println();
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int leftPadding = (width - text.length()) / 2;
        return " ".repeat(leftPadding) + text;
    }

    private static String getFormattedStartTime() {
        long startTimeMillis = ManagementFactory.getRuntimeMXBean().getStartTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(new Date(startTimeMillis));
    }
}