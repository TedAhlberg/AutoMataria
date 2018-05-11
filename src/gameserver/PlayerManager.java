package gameserver;

import java.util.Collection;
import java.util.LinkedList;
import common.Action;
import common.Direction;
import common.GameMap;
import common.GameState;
import common.ID;
import common.Utility;
import common.messages.Message;
import common.messages.MessageListener;
import common.messages.PlayerMessage;
import common.messages.ReadyPlayersMessage;
import gameobjects.GameObject;
import gameobjects.Player;

/**
 * 
 * @author Dante Håkansson
 * @author Johannes Blüml
 *
 */

public class PlayerManager implements MessageListener {
    private final LinkedList<Player> players = new LinkedList<>();
    private final StartingPositions startingPositions = new StartingPositions();
    private final GameColors colors = new GameColors();
    private final Collection<GameObject> gameObjects;
    private GameMap currentMap;
    private GameState state;
    private int currentPlayerSpeed;
    private MessageListener messageListener;
    
    

    public PlayerManager(Collection<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }
    
    public Player login(String username) {
        
        Player player = newPlayer(username);
        System.out.println(player);
        if (player != null) {
            player.setColor(colors.takeColor());
            
            
            
            newMessage(new PlayerMessage(PlayerMessage.Event.Connected, player));
            updateReadyPlayers();
            getPlayers().add(player);
            return player;
        }  
            return null;
        }
    
    
    private Player newPlayer(String name) {
      
        if (getPlayers().size() > currentMap.getPlayers()) return null;
        
        Player player = new Player(name, gameObjects, currentMap);
        player.setListener(this);
        player.setId(ID.getNext());
        player.setSpeed(currentPlayerSpeed);
        System.out.println(state);
        if (state == GameState.Warmup) {
            player.reset();
            player.setSpeed(currentPlayerSpeed);
            player.setPoint(Utility.getRandomUniquePosition(currentMap.getGrid(), gameObjects));
            gameObjects.add(player);
            gameObjects.add(player.getTrail());
        }
        return player;
    }

    public void controlPlayer(Player player, Object value) {

        if (value instanceof Direction) {
            if (state == GameState.Running || state == GameState.Warmup) {
                Direction direction = (Direction) value;
                player.setNextDirection(direction);
            }
        } else if (value instanceof Action) {
            if (value == Action.UsePickup) {
                player.usePickup();
            } else if (state == GameState.Warmup) {
                if (value == Action.TogglePlayerColor) {
                    player.setColor(colors.exchangeColor(player.getColor()));
                    newMessage(new PlayerMessage(PlayerMessage.Event.ColorChange, player));
                } else if (value == Action.ToggleReady) {
                    boolean ready = !player.isReady();
                    player.setReady(ready);
                    newMessage(new PlayerMessage((ready) ? PlayerMessage.Event.Ready : PlayerMessage.Event.Unready,
                            player));
                    updateReadyPlayers();
                }
            }
        }
    }
    
    public void resetGame() {
        gameObjects.clear();

        if (currentMap.getStartingPositions() != null) {
            startingPositions.set(currentMap.getStartingPositions());
        } else {
            startingPositions.generate(currentMap.getGrid(), currentMap.getPlayers());
        }

        getPlayers().forEach((player) -> {
            if (state != GameState.Warmup) {
                player.setReady(false);
            }
            player.reset();
            player.setSpeed(currentPlayerSpeed);
            player.setPoint(Utility.convertFromGrid(startingPositions.getNext()));
            gameObjects.add(player);
            gameObjects.add(player.getTrail());
        });
    }
    
    public void updateReadyPlayers() {
        ReadyPlayersMessage message = new ReadyPlayersMessage(getPlayers());
        newMessage(message);
       
    }
    
    public void respawnDeadPlayers() {
        for (Player player : getPlayers()) {
            if (player.isDead()) {
                player.reset();
                player.setSpeed(currentPlayerSpeed);
                player.setPoint(Utility.getRandomUniquePosition(currentMap.getGrid(), gameObjects));
            }
        }
    }
    public void setCurrentMap(GameMap currentMap) {
        this.currentMap = currentMap;
    }
    public void setState(GameState state) {
        this.state = state;
    }
    public void setCurrentPlayerSpeed(int currentPlayerSpeed) {
        this.currentPlayerSpeed = currentPlayerSpeed;
    }
    public void addListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * @param message
     *            Message to be sent to all connected clients
     */
    public void newMessage(Message message) {
        messageListener.newMessage(message);
    }

    public LinkedList<Player> getPlayers() {
        return players;
    }


   
}
