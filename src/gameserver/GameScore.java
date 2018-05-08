package gameserver;

import gameobjects.Player;

import java.util.Collection;
import java.util.HashMap;

/**
 * Manages the score for all players during the games
 *
 * @author Johannes Blüml
 */
public class GameScore {
    private int deadPlayers = 0;
    private HashMap<Player, Integer> roundScores = new HashMap<>();
    private HashMap<Player, Integer> accumulatedScores = new HashMap<>();
    private Collection<Player> players;
    private int roundLimit, scoreLimit;
    private int roundsPlayed, highestScore;
    private boolean gameComplete, roundComplete;

    /**
     * @param players    The collection of players that will be checked for deaths each call to calculateScores
     * @param roundLimit less than 1 means no round limit otherwise specified number of rounds have to be played before map can be changed
     * @param scoreLimit less than 1 means no score limit otherwise any player needs to reach this number of points before map can be changed
     */
    synchronized public void startGame(Collection<Player> players, int roundLimit, int scoreLimit) {
        this.roundLimit = roundLimit;
        this.scoreLimit = scoreLimit;
        this.players = players;

        gameComplete = roundComplete = false;
        highestScore = roundsPlayed = 0;
    }

    /**
     * Resets values to a state that represents that a new round in the game has begun
     */
    synchronized public void startRound() {
        deadPlayers = 0;
        roundComplete = false;
        roundScores.clear();
        players.forEach(player -> {
            roundScores.put(player, 0);
            accumulatedScores.putIfAbsent(player, 0);
        });
    }

    /**
     * Increments round count and checks if game is completed
     * If game is completed newGame() methos has to be called to start a new game
     */
    private void endRound() {
        if (gameComplete) return;
        roundsPlayed += 1;
        roundComplete = true;
        if (roundLimit > 0 && roundLimit <= roundsPlayed) {
            gameComplete = true;
        }
        if (scoreLimit > 0 && scoreLimit <= highestScore) {
            gameComplete = true;
        }
    }

    /**
     * Checks if players have died - if so then updates the sccores for everyone thet is alive
     */
    synchronized public void calculateScores() {
        if (gameComplete) return;
        int currentDeadPlayers = (int) players.stream().filter(Player::isDead).count();
        if (currentDeadPlayers == this.deadPlayers) return; // no change in dead players

        int deadPlayersSinceLastTime = currentDeadPlayers - this.deadPlayers;
        this.deadPlayers = currentDeadPlayers;

        int alivePlayers = 0;
        for (Player player : players) {
            if (!player.isDead()) {
                alivePlayers += 1;
                int newScore = deadPlayersSinceLastTime;

                newScore += roundScores.getOrDefault(player, 0);
                roundScores.put(player, newScore);

                newScore += accumulatedScores.getOrDefault(player, 0);
                accumulatedScores.put(player, newScore);

                if (newScore > highestScore) {
                    highestScore = newScore;
                }
            }
        }
        if (alivePlayers <= 1) {
            endRound();
        }
    }

    synchronized public HashMap<Player, Integer> getRoundScores() {
        return roundScores;
    }

    synchronized public HashMap<Player, Integer> getAccumulatedScores() {
        return accumulatedScores;
    }

    synchronized public boolean isGameComplete() {
        return gameComplete;
    }

    synchronized public boolean isRoundComplete() {
        return roundComplete;
    }
}
