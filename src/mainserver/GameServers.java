package mainserver;

import common.ServerInformation;

import java.util.ArrayList;

/**
 * @author Henrik Olofsson
 */
public class GameServers {
    private ArrayList<ServerInformation> servers = new ArrayList<>();

    public GameServers() {

    }

    public synchronized void addServer(ServerInformation serverInformation) {
        servers.remove(serverInformation);
        servers.add(serverInformation);
    }

    public synchronized ArrayList<ServerInformation> getServers() {
        return servers;
    }
}
