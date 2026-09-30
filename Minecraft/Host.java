package Minecraft;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Host {
    private final IPAddress ipAddress;
    private final Port port;
    private final File serverCodeDir;

    public Host(int portNumber) {
        this.ipAddress = new IPAddress();
        this.port = new Port(portNumber);
        this.serverCodeDir = new File("Minecraft/Code");
    }

    public IPAddress getIpAddress() {
        return ipAddress;
    }

    public Port getPort() {
        return port;
    }

    public File getServerCodeDir() {
        return serverCodeDir;
    }

    public int ping() {
        long start = System.currentTimeMillis();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", port.getPortNumber()), 500);
            return (int) (System.currentTimeMillis() - start);
        } catch (IOException e) {
            return -1; 
        }
    }
}