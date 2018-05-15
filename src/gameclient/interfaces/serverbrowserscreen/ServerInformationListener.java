package gameclient.interfaces.serverbrowserscreen;

import common.ServerInformation;

import java.util.Collection;

/**
 * @author Henrik Olofsson
 */

public interface ServerInformationListener {
    void update(Collection<ServerInformation> serverList);
}
