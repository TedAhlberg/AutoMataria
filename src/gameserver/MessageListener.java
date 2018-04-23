package gameserver;

import common.Message;
/**
 * 
 * @author eriklundow
 *
 */
public interface MessageListener {

    public void newMessage(Message message);
}
