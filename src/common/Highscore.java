package common;

import java.io.Serializable;

public class Highscore implements Serializable {
    private static final long serialVersionUID = 1L;
    private String playerName;
    private int highscore;

    public Highscore(String playerName, int highscore) {
        this.playerName = playerName;
        this.highscore = highscore;
    }

    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public int getHighscore() {
        return highscore;
    }
    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }
    
    public boolean equals(Object obj) {
        return obj instanceof Highscore &&
                (((Highscore) obj).getPlayerName().equals(playerName));
    }

    public String toString() {
        return "Highscore [playerName=" + playerName + ", highscore=" + highscore + "]";
    }
    
    
    

    
    

}
