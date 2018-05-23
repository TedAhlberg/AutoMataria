package mainserver;

import common.ServerInformation;

import java.util.ArrayList;

/**
 * @author Henrik Olofsson
 */
public class GameServerList {
    private ArrayList<ServerInformation> servers = new ArrayList<>();

    public synchronized void addServer(ServerInformation serverInformation) {
        servers.remove(serverInformation);
        servers.add(serverInformation);
    }

    public synchronized ArrayList<ServerInformation> getServers() {
        return servers;
    }
}
