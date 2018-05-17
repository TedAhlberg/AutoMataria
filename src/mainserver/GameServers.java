package mainserver;

import common.ServerInformation;
import gameclient.interfaces.serverbrowserscreen.ServerInformationListener;

import java.util.ArrayList;
import java.util.Collection;
/**
 * 
 * @author Henrik Olofsson
 *
 */

public class GameServers implements ServerInformationListener {
    private ArrayList<ServerInformation> servers = new ArrayList<>();
    
    public GameServers() {
        
    }
    
    public synchronized void addServer(ServerInformation serverInformation) {
        servers.remove(serverInformation);
        servers.add(serverInformation);
    }
    
    public synchronized ArrayList<ServerInformation> getAll() {
        return servers;
    }

    public synchronized void update(Collection<ServerInformation> serverList) {
        
    }
    
    public ArrayList<ServerInformation> getServers(){
        return servers;
    }
    
    
    

}
