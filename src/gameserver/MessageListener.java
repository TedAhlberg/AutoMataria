package gameserver;

import common.messages.Message;
/**
 * 
 * @author eriklundow
 *
 */
public interface MessageListener {
    void newMessage(Message message);
}
