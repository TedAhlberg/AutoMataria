package common.messages;

import java.util.HashMap;

import gameobjects.Player;
/**
 * 
 * @author eriklundow
 *
 */
public class GameOverMessage extends Message{
    
    private static final long serialVersionUID = 1L;
    private HashMap <Player,Integer>  scores;
    
    public GameOverMessage(HashMap<Player, Integer> scores) {
        this.scores=scores;
    }
    
    public HashMap<Player, Integer> getScores() {
        return scores;
    }

}
