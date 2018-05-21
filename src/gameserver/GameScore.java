package gameserver;

import common.MainServerClient;
import common.messages.MessageListener;
import common.messages.ScoreUpdateMessage;
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
    private HashMap<Player, Integer> scores = new HashMap<>();
    private Collection<Player> players;
    private int roundLimit, scoreLimit;
    private int roundsPlayed, highestScore;
    private boolean gameOver, roundComplete;
    private MessageListener listener;

    public GameScore(MessageListener listener) {
        this.listener = listener;
    }

    /**
     * @param players    The collection of players that will be checked for deaths each call to calculateScores
     * @param roundLimit less than 1 means no round limit otherwise specified number of rounds have to be played before map can be changed
     * @param scoreLimit less than 1 means no score limit otherwise any player needs to reach this number of points before map can be changed
     */
    synchronized public void startGame(Collection<Player> players, int roundLimit, int scoreLimit) {
        this.roundLimit = roundLimit;
        this.scoreLimit = scoreLimit;
        this.players = players;

        gameOver = roundComplete = false;
        deadPlayers = highestScore = roundsPlayed = 0;

        scores.clear();
    }

    /**
     * Resets values to a state that represents that a new round in the game has begun
     * If game is completed newGame() method has to be called to start a new game
     */
    synchronized public void startRound() {
        if (gameOver) return;
        deadPlayers = 0;
        roundComplete = false;
        players.forEach(player -> scores.putIfAbsent(player, 0));
        sendScoreUpdate();
    }

    /**
     * Increments round count and checks if game is completed
     * If game is completed newGame() method has to be called to start a new game
     */
    private void endRound() {
        if (gameOver) return;
        roundsPlayed += 1;
        roundComplete = true;
        if (roundLimit > 0 && roundLimit <= roundsPlayed) {
            gameOver = true;
        }
        if (scoreLimit > 0 && scoreLimit <= highestScore) {
            gameOver = true;
        }
        sendScoreUpdate();
        if (gameOver) {
            HashMap<String, Integer> newScores = new HashMap<>();
            scores.forEach((player, score) -> newScores.put(player.getName(), score));
            new MainServerClient().sendGameScore(newScores);
        }
    }

    /**
     * Checks if players have died - if so then updates the sccores for everyone thet is alive
     */
    synchronized public void calculateScores() {
        if (gameOver) return;
        int currentDeadPlayers = (int) players.stream().filter(Player::isDead).count();
        if (currentDeadPlayers == this.deadPlayers) return; // no change in dead players

        int deadPlayersSinceLastTime = currentDeadPlayers - this.deadPlayers;
        this.deadPlayers = currentDeadPlayers;

        int alivePlayers = 0;
        for (Player player : players) {
            if (!player.isDead()) {
                alivePlayers += 1;

                int previousScore = scores.getOrDefault(player, 0);
                int newScore = deadPlayersSinceLastTime + previousScore;
                scores.put(player, newScore);

                if (newScore > highestScore) {
                    highestScore = newScore;
                }
            }
        }

        sendScoreUpdate();

        if (alivePlayers <= 1) {
            endRound();
        }
    }

    /**
     * Sends score to all clients through the MessageListener
     */
    private void sendScoreUpdate() {
        listener.newMessage(new ScoreUpdateMessage(scores, roundsPlayed, highestScore, gameOver));
    }

    synchronized public HashMap<Player, Integer> getScores() {
        return scores;
    }

    synchronized public boolean isGameOver() {
        return gameOver;
    }

    synchronized public boolean isRoundComplete() {
        return roundComplete;
    }
}
