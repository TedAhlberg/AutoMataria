package mainserver;


public class HighScore2 {
    private String playerName;
    private int highscore;
    
    public HighScore2(String playerName, int highscore) {
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
        return obj instanceof HighScore2 &&
                (((HighScore2)obj).getPlayerName().equals(playerName));
    }

    public String toString() {
        return "HighScore2 [playerName=" + playerName + ", highscore=" + highscore + "]";
    }
    
    
    

    
    

}
