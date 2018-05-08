package common;

/**
 * States that the GameServer can be in.
 *
 * @author Johannes Blüml
 */
public enum GameState {
    Warmup,
    Countdown,
    Running,
    RoundOver,
    GameOver
}
