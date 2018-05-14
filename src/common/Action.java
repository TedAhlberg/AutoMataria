package common;

/**
 * Actions that users can do while connected to a GameServer.
 *
 * @author Johannes Blüml
 * @author Ted Ahlberg
 */
public enum Action {

    // Client Actions
    InterfaceBack,
    OpenChatPrompt,
    SendChatMessage,
    ToggleInterpolation,
    ToggleNames,
    ToggleDebugText,

    //Server Actions
    GoLeft,
    GoRight,
    GoUp,
    GoDown,
    UsePickup,
    ToggleReady,
    TogglePlayerColor
}
