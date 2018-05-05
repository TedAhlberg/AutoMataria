package gameserver;

import gameobjects.Player;

import java.util.*;

/**
 * Manages scores during a round in the game
 *
 * @author Johannes Blüml
 */
public class GameScore {
    private HashMap<Player, Integer> scores = new HashMap<>();
    private HashSet<Player> previouslyAlivePlayers = new HashSet<>();

    public void start(Collection<Player> players) {
        previouslyAlivePlayers.clear();
        scores.clear();
        for (Player player : players) {
            previouslyAlivePlayers.add(player);
            scores.put(player, 0);
        }
    }

    public int calculateScores() {
        if (previouslyAlivePlayers.size() <= 1) return previouslyAlivePlayers.size();

        HashSet<Player> deadPlayers = new HashSet<>();

        Iterator<Player> iterator = previouslyAlivePlayers.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.isDead()) {
                deadPlayers.add(player);
                iterator.remove();
            }
        }

        int deadPlayersCount = deadPlayers.size();
        if (deadPlayersCount == 0) return previouslyAlivePlayers.size();

        for (Player player : previouslyAlivePlayers) {
            int newScore = deadPlayersCount;
            newScore += scores.get(player);
            scores.put(player, newScore);
        }

        return previouslyAlivePlayers.size();
    }

    public HashMap<Player, Integer> getScores() {
        return scores;
    }
}
