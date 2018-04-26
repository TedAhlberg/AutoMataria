package gameserver;

import common.messages.Message;
/**
 * 
 * @author eriklundow
 *
 */
public interface MessageListener {

    public void newMessage(Message message);
}
