package gameclient;

import java.util.Collection;
/**
 * @author Johannes Blüml & Henrik Olofsson
 */

public interface ServerInformationListener {
    
    public void update(Collection<ServerInformation> serverList);
    

}
