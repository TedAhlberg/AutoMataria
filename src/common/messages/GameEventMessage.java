package common.messages;

/**
 * 
 * @author eriklundow
 *
 */
public class GameEventMessage extends Message {
    public String data;
    private static final long serialVersionUID = 1L;

   public GameEventMessage(String data) {
       this.data=data;  
       
   }




}
