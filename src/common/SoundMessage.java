package common;

import java.io.Serializable;
/**
 * 
 * @author eriklundow
 *
 */
public class SoundMessage extends Message {

    public String sfx;
    private static final long serialVersionUID = 1L;

   public SoundMessage(String sfx) {
       this.sfx=sfx;  
   }



}
