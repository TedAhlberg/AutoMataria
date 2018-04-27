package gameclient;

/**
 * Represents information from a GameServer
 *
 * @author Henrik Olofsson
 */
public class ServerInformation {
    private String ip;
    private String serverName;
    private String mapName;
    private String gameState;
    private int serverPort;
    private int connectedClients;
    private int maxPlayers;

    public ServerInformation(String ip, String serverName, String mapName, String gameState, int serverPort,
                             int connectedClients, int maxPlayers) {
        this.ip = ip;
        this.serverName = serverName;
        this.mapName = mapName;
        this.gameState = gameState;
        this.serverPort = serverPort;
        this.connectedClients = connectedClients;
        this.maxPlayers = maxPlayers;
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

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + serverPort;
        return result;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ServerInformation) {
            ServerInformation servInfo = (ServerInformation) obj;
            return (servInfo.getIp().equals(ip) && servInfo.getServerPort() == serverPort);
        }
        return false;
    }
}
