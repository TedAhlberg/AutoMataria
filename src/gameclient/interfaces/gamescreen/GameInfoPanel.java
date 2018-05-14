package gameclient.interfaces.gamescreen;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Johannes Blüml
 */
public class GameInfoPanel extends JTextPane {
    private StyledDocument document;
    private MutableAttributeSet style;
    private ArrayList<Integer> textItems;
    private int lines;
    private Color defaultColor = Color.CYAN;

    public GameInfoPanel(int lines) {
        this.lines = lines;

        setEditable(false);

        textItems = new ArrayList<>(lines);
        document = getStyledDocument();

        style = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(style, .15f);
        StyleConstants.setFontSize(style, 10);
        setParagraphAttributes(style, true);
    }

    public void add(String text) {
        add(text, defaultColor);
    }

    public void add(String text, Color color) {
        try {
            StyleConstants.setForeground(style, color);
            document.insertString(document.getLength(), text + "\n", style);
            textItems.add(text.length() + 1);
            if (textItems.size() > lines) {
                int charsToRemove = textItems.get(0);
                document.remove(0, charsToRemove);
                textItems.remove(0);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
