package common.messages;

import gameobjects.Player;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Johannes Blüml
 */
public class ReadyPlayersMessage extends Message {
    private static final long serialVersionUID = 1L;

    private final Collection<Player> players = new ArrayList<>();
    private final int ready, totalPlayers;
    private int roundLimit, scoreLimit;

    public ReadyPlayersMessage(Collection<Player> players) {
        this.players.addAll(players);
        totalPlayers = players.size();
        ready = (int) players.stream().filter(Player::isReady).count();
    }

    public int getReadyPlayerCount() {
        return ready;
    }

    public int getPlayerCount() {
        return totalPlayers;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public int getRoundLimit() {
        return roundLimit;
    }

    public void setRoundLimit(int roundLimit) {
        this.roundLimit = roundLimit;
    }

    public int getScoreLimit() {
        return scoreLimit;
    }

    public void setScoreLimit(int scoreLimit) {
        this.scoreLimit = scoreLimit;
    }
}
