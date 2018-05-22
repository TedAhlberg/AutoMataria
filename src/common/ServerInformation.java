package common;

import java.io.Serializable;

/**
 * Represents information from a GameServer
 *
 * @author Henrik Olofsson
 */
public class ServerInformation implements Serializable {
    public static final long serialVersionUID = 1L;
    private String ip;
    private String serverName;
    private String mapName;
    private String gameState;
    private int serverPort;
    private int connectedClients;
    private int maxPlayers;
    private long lastUpdateTime;

    public ServerInformation(String ip, String serverName, String mapName, String gameState, int serverPort,
                             int connectedClients, int maxPlayers) {
        this.ip = ip;
        this.serverName = serverName;
        this.mapName = mapName;
        this.gameState = gameState;
        this.serverPort = serverPort;
        this.connectedClients = connectedClients;
        this.maxPlayers = maxPlayers;
        lastUpdateTime = System.currentTimeMillis();
    }

    public long getUpdateTime() {
        return lastUpdateTime;
    }

    public String getIp() {
        return ip;
    }

    public String getServerName() {
        return serverName;
    }

    public String getMapName() {
        return mapName;
    }

    public String getGameState() {
        return gameState;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getConnectedClients() {
        return connectedClients;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + serverPort;
        return result;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ServerInformation) {
            ServerInformation serverInformation = (ServerInformation) obj;
            return (serverInformation.getIp().equals(ip) && serverInformation.getServerPort() == serverPort);
        }
        return false;
    }

    public byte[] toByteArray() {
        String string = serverName + "\n"
                + mapName + "\n"
                + gameState + "\n"
                + serverPort + "\n"
                + connectedClients + "\n"
                + maxPlayers + "\0";

        return string.getBytes();
    }

    public String toString() {
        return "ServerInformation [ip=" + ip + ", serverName=" + serverName + ", mapName=" + mapName + ", gameState="
                + gameState + ", serverPort=" + serverPort + ", connectedClients=" + connectedClients + ", maxPlayers="
                + maxPlayers + ", lastUpdateTime=" + lastUpdateTime + "]";
    }
}
