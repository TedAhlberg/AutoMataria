package mainserver;

import java.util.ArrayList;
import java.util.Collection;

import common.ServerInformation;
import gameclient.ServerInformationListener;
/**
 * 
 * @author Henrik Olofsson
 *
 */

public class Servers implements ServerInformationListener {
    private ArrayList<ServerInformation> servers = new ArrayList<>();
    
    public Servers() {
        
    }
    
    public void addServer(ServerInformation serverInformation) {
        servers.remove(serverInformation);
        servers.add(serverInformation);
    }
    
    public ArrayList<ServerInformation> getAll() {
        return servers;
    }

    public void update(Collection<ServerInformation> serverList) {
        
    }
    
    
    

}
