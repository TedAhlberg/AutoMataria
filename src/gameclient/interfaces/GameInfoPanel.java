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
    private GridBagConstraints gridBagConstraints = new GridBagConstraints();
    private ArrayList<Integer> textItems;
    private int lines;
    private Color defaultColor = Color.CYAN;


    public GameInfoPanel(int lines) {
        this.lines = lines;
        textItems = new ArrayList<Integer>(lines);
        setLayout(new GridBagLayout());

        gridBagConstraints.gridx = 0;
        gridBagConstraints.ipady = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;

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
            text += "\n";
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
        /*
        JLabel textLabel = new JLabel(text.toUpperCase());
        textLabel.setForeground(color);
        textLabel.setFont(Resources.getInstance().getDefaultFont().deriveFont(Font.BOLD, 10));
        add(textLabel, gridBagConstraints);
        textItems.add(textLabel);
        if (textItems.size() > lines) remove(textItems.removeFirst());
        revalidate();
        repaint();
        */
    }
}
