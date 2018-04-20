package gameclient;

import java.util.Collection;
/**
 * @author Johannes Blüml & Henrik Olofsson
 */

import gameserver.ServerInformation;

public interface ServerInformationListener {
    
    public void update(Collection<ServerInformation> serverList);
    

}
