package common;

/**
 * Actions that users can do while connected to a GameServer.
 *
 * @author Johannes Blüml
 * @author Ted Ahlberg
 */
public enum Action {
    ExitGame,
    ToggleInterpolation,
    ToggleReady,
    TogglePlayerColor,
    ToggleNames,
    ToggleDebugText,

    GoLeft,
    GoRight,
    GoUp,
    GoDown,
    UsePickup
}
