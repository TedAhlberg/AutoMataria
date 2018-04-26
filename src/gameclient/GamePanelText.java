package gameclient;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import common.GameState;

/**
 * @author Ted Ahlberg
 */

public class GamePanelText {
    private Color color = new Color(255, 255, 255, 200);
    private String fontName = "Orbitron";

    public void drawDebugInfo(Graphics2D g2, GameState gameState, int fps, double playerReadyPercentage) {
        StringBuilder res = new StringBuilder();
        int x = 120, y = 200, fontSize = 100;
        
        g2.setColor(color);
        g2.setFont(new Font(fontName, Font.PLAIN, fontSize));
        res.append("FPS: " + fps + "/60 | ");
        res.append("STATE: " + gameState.toString().toUpperCase() + " | ");
        if (gameState == GameState.Warmup) {
            res.append(Math.round(playerReadyPercentage * 100) + "% PLAYERS READY (PRESS 'R' TO READY UP)");
        }
        g2.drawString(res.toString(), x, y);
        res.delete(0, res.length());
        res.append("CHANGE COLOR: C | TOGGLE HELP: F1 | TOOGLE INTERPOLATION: I");
        g2.drawString(res.toString(), x, y += fontSize);
    }
    
    public void drawNewGameCountdown(Graphics2D g2) {
        String infoText = "GAME IS ABOUT TO BEGIN";
        String infoText2 = "GET READY"; 
        Font font = new Font("Orbitron", Font.BOLD, 200);
        g2.setFont(font);
        g2.setColor(new Color(255, 255, 255, 200));
        FontMetrics fontMetrics = g2.getFontMetrics();
        Rectangle2D infoTextBounds = fontMetrics.getStringBounds(infoText, g2);
        Rectangle2D infoText2Bounds = fontMetrics.getStringBounds(infoText2, g2);
        int width = g2.getClipBounds().width;
        int height = g2.getClipBounds().height;
        int infoTextWidth = (int) ((width / 2) - (infoTextBounds.getWidth() / 2));
        int infoTextHeight = (int) (((height / 2) - (infoTextBounds.getHeight() / 2)) - 100);
        int infoText2Width = (int) ((width / 2) - (infoText2Bounds.getWidth() / 2));
        int infoText2Height = (int) (((height / 2) - (infoText2Bounds.getHeight() / 2)) + 100);
        
        g2.drawString(infoText, infoTextWidth, infoTextHeight);
        g2.drawString(infoText2, infoText2Width, infoText2Height);
    }
    
    public void drawGameOverInfo(Graphics2D g2) {
        String infoText = "GAME OVER";
        Font font = new Font("Orbitron", Font.BOLD, 300);
        g2.setFont(font);
        g2.setColor(new Color(255, 255, 255, 200));
        FontMetrics fontMetrics = g2.getFontMetrics();
        Rectangle2D infoTextBounds = fontMetrics.getStringBounds(infoText, g2);
        int width = g2.getClipBounds().width;
        int height = g2.getClipBounds().height;
        int infoTextWidth = (int) ((width / 2) - (infoTextBounds.getWidth() / 2));
        int infoTextHeight = (int) (((height / 2) - (infoTextBounds.getHeight() / 2)));

        g2.drawString(infoText, infoTextWidth, infoTextHeight);
    }
}
