import Minecraft.Host;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Game {
    private final Host host;
    private Process serverProcess;
    private SToper stoper;
    private PrintWriter processInputWriter;

    public Game(Host host) {
        this.host = host;
    }

    public void start() {
        File codeDir = host.getServerCodeDir();
        File jarFile = new File(codeDir, "server.jar");

        if (!jarFile.exists()) {
            System.err.println("[Error] Minecraft/Code/server.jar が見つかりません。");
            return;
        }

        setupEula(codeDir);

        try {
            ProcessBuilder pb = new ProcessBuilder(
                "java",
                "-Xmx2048M",
                "-Xms1048M",
                "-jar",
                "server.jar",
                "nogui"
            );
            pb.directory(codeDir);
            pb.redirectErrorStream(true);

            serverProcess = pb.start();
            stoper = new SToper(serverProcess);
            processInputWriter = new PrintWriter(new OutputStreamWriter(serverProcess.getOutputStream()), true);

            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[MC] " + line);

                        if (line.contains("logged in") || line.contains("joined the game")) {
                            String playerName = parsePlayerName(line);
                            stoper.onPlayerJoin(playerName);
                        }
                        else if (line.contains("left the game")) {
                            String playerName = parsePlayerName(line);
                            stoper.onPlayerLeave(playerName);
                        }
                        else if (line.contains("Done (") && line.contains("For help, type")) {
                            if (stoper.getOnlinePlayers() == 0) {
                                stoper.pauseServer();
                            }
                        }
                    }
                } catch (IOException e) {

                }
            }).start();

        } catch (IOException e) {
            System.err.println("[Error] サーバー起動エラー: " + e.getMessage());
        }
    }

    private String parsePlayerName(String logLine) {
        try {
            String[] parts = logLine.split("]: ");
            if (parts.length > 1) {
                String namePart = parts[1].trim();
                return namePart.split("\\[")[0].split(" ")[0];
            }
        } catch (Exception e) {
        }
        return "UnknownPlayer";
    }

    private void setupEula(File dir) {
        Path eulaPath = dir.toPath().resolve("eula.txt");
        if (!Files.exists(eulaPath)) {
            try {
                Files.writeString(eulaPath, "eula=true\n");
            } catch (IOException ignored) {}
        }
    }

    public void sendCommand(String command) {
        if (stoper != null && stoper.isPaused()) {
            stoper.resumeServer();
        }
        if (processInputWriter != null && isRunning()) {
            processInputWriter.println(command);
        }
    }

    public boolean isRunning() {
        return serverProcess != null && serverProcess.isAlive();
    }

    public SToper getStoper() {
        return stoper;
    }
}