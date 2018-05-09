package gameclient.interfaces;

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
    private int lineLimit;


    public GameInfoPanel(int lines, int lineLimit) {
        this.lines = lines;
        this.lineLimit = lineLimit;

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
            text = getLimitedString(text, lineLimit);
            document.insertString(document.getLength(), text, style);
            textItems.add(text.length());
            if (textItems.size() > lines) {
                int charsToRemove = textItems.get(0);
                document.remove(0, charsToRemove);
                textItems.remove(0);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds newline character at the lineLimit (maximum characters in a row)
     *
     * @param text      Text to split with newline character
     * @param lineLimit How many characters should each line have
     * @return A new String with newline character at every lineLimit index
     */
    private String getLimitedString(String text, int lineLimit) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i += lineLimit) {
            int end = i + lineLimit;
            if (end > text.length()) end = text.length();
            stringBuilder.append(text, i, end);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
