package gameclient;

import java.util.Collection;

/**
 * @author Henrik Olofsson
 */

public interface ServerInformationListener {
    void update(Collection<ServerInformation> serverList);
}
