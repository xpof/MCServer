import Minecraft.Host;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class WebSOcket {
    private final int port;
    private final Host host;
    private final Game game;
    private HttpServer server;

    public WebSOcket(int port, Host host, Game game) {
        this.port = port;
        this.host = host;
        this.game = game;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/status", new StatusHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("[WebAPI] Webステータスサーバーを起動しました (Port: " + port + ")");
        } catch (IOException e) {
            System.err.println("[WebAPI] Webステータスサーバーの起動に失敗: " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String jsonResponse = String.format(
                "{\"running\":%b, \"ping\":%d, \"mc_port\":%d, \"ip\":\"%s\"}",
                game.isRunning(),
                host.ping(),
                host.getPort().getPortNumber(),
                host.getIpAddress().getAddress()
            );

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            byte[] bytes = jsonResponse.getBytes("UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
}