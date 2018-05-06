package gameserver;

import gameobjects.Player;

import java.util.Collection;
import java.util.HashMap;

/**
 * Manages the score for all players during a round in the game
 *
 * @author Johannes Blüml
 */
public class GameScore {
    private int deadPlayers = 0;
    private HashMap<Player, Integer> roundScores = new HashMap<>();
    private HashMap<Player, Integer> accumulatedScores = new HashMap<>();
    private Collection<Player> players;

    public GameScore(Collection<Player> players) {
        this.players = players;
    }

    /**
     * Resets the round score
     */
    synchronized public void startRound() {
        this.deadPlayers = 0;
        roundScores.clear();
        players.forEach(player -> roundScores.put(player, 0));
        players.forEach(player -> accumulatedScores.putIfAbsent(player, 0));
    }

    /**
     * Checks if players have died - if so then updates the sccores for everyone thet is alive
     */
    synchronized public void calculateScores() {
        int currentDeadPlayers = (int) players.stream().filter(Player::isDead).count();
        if (currentDeadPlayers == this.deadPlayers) return;

        int deadPlayersSinceLastTime = currentDeadPlayers - this.deadPlayers;
        this.deadPlayers = currentDeadPlayers;

        for (Player player : players) {
            if (!player.isDead()) {
                int newScore = deadPlayersSinceLastTime;

                newScore += roundScores.getOrDefault(player, 0);
                roundScores.put(player, newScore);

                newScore += accumulatedScores.getOrDefault(player, 0);
                accumulatedScores.put(player, newScore);
            }
        }
    }

    synchronized public HashMap<Player, Integer> getRoundScores() {
        return roundScores;
    }

    synchronized public HashMap<Player, Integer> getAccumulatedScores() {
        return accumulatedScores;
    }
}
