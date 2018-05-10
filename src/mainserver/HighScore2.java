package mainserver;

import gameobjects.Player;

public class HighScore2 {
    private Player player;
    private int highscore;
    
    public HighScore2(Player player, int highscore) {
        this.player = player;
        this.highscore = highscore;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + highscore;
        result = prime * result + ((player == null) ? 0 : player.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HighScore2 other = (HighScore2) obj;
        if (highscore != other.highscore)
            return false;
        if (player == null) {
            if (other.player != null)
                return false;
        } else if (!player.equals(other.player))
            return false;
        return true;
    }

}
