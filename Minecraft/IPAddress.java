package Minecraft;

import java.net.InetAddress;

public class IPAddress {
    private final String address;

    public IPAddress() {
        this.address = resolveIP();
    }

    private String resolveIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    public String getAddress() {
        return address;
    }
}