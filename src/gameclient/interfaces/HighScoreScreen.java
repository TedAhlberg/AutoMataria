package gameclient.interfaces;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import gameclient.Resources;
import mainserver.HighScore2;
import mainserver.HighScoreList;



public class HighScoreScreen extends JPanel implements UserInterfaceScreen {
    private UserInterface userInterface;
    private JPanel panel;
    private HighScoreList highscoreList = new HighScoreList();
	
	public HighScoreScreen(UserInterface userInterface) {
        this.userInterface = userInterface;
        
		createLayout();
		update(highscoreList);
		
	}
	
	public void setHighScoreList(HighScoreList highscoreList) {
	    this.highscoreList = highscoreList;
	}
	
	public void createLayout() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        
        int column = 0, row = 0;
        GridBagConstraints c;

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        
        JLabel headerLabel = new JLabel("HIGHSCORES");
        headerLabel.setFont(Resources.getInstance().getTitleFont());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 10;
        c.insets = new Insets(0, 50, 0, 0);
        c.anchor = GridBagConstraints.WEST;
        topPanel.add(headerLabel, c);
        
        AMButton customConnectButton = new AMButton("BACK");
        customConnectButton.addActionListener(e -> userInterface.changeToPreviousScreen());
        c = new GridBagConstraints();
        c.gridx = 1;
        c.ipadx = 30;
        c.ipady = 10;
        c.insets = new Insets(0, 0, 0, 10);
        c.anchor = GridBagConstraints.EAST;
        topPanel.add(customConnectButton, c);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(topPanel, c);
        
        panel = new JPanel(new GridBagLayout());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 50;
        c.weightx = 1;
        add(panel, c);
        
	}
	
	public void update(HighScoreList list) {
//		panel.removeAll();
	    
	    HighScore2 highScore = new HighScore2("Henko", 100);
	    HighScore2 highscore2 = new HighScore2("Erik", 200);
	    HighScore2 highscore3 = new HighScore2("Johannes", 300);
	    HighScore2 highscore4 = new HighScore2("Ted", 400);
	    HighScore2 highscore5 = new HighScore2("Dante", 500);
	    
	    list.addAndReplace(highScore);
	    list.addAndReplace(highscore2);
	    list.addAndReplace(highscore3);
	    list.addAndReplace(highscore4);
	    list.addAndReplace(highscore5);
	    
	    ArrayList<HighScore2> highscoreList = list.getSortedList();
	    
	    
//		HighScore highScore = new HighScore();
//		highScore.put("Henko", 100);
//		highScore.put("Erik", 200);
//		highScore.put("Ted", 300);
//		highScore.put("Dante", 400);
//		highScore.put("Johannes", 500);
		
		
        int column = 0, row = 0;
        GridBagConstraints c;

        JLabel[] headerLabels = {
                new JLabel("PLAYER NAME"),
                new JLabel("HIGHSCORE"),
                new JLabel()
        };
        
        for (JLabel label : headerLabels) {
            label.setForeground(Color.MAGENTA.darker());
            panel.add(label, getTableConstraints(column, row));

            column += 1;
        }
        row += 1;
        
        JPanel separator = new JPanel();
        separator.setBorder(new MatteBorder(1, 0, 0, 0, Color.MAGENTA.darker().darker()));
        c = new GridBagConstraints();
        c.gridwidth = headerLabels.length;
        c.gridy = row;
        c.fill = GridBagConstraints.BOTH;
        panel.add(separator, c);

        row += 1;
        
        
        for(int index = 0; index < highscoreList.size(); index++) {
            column = 0;
            
            JLabel[] labels = {
                    new JLabel(highscoreList.get(index).getPlayerName()),
                    new JLabel(Integer.toString(highscoreList.get(index).getHighscore()))
            };
            
            for (JLabel label : labels) {
                panel.add(label, getTableConstraints(column, row));
                column += 1;
            }
            row += 1;
        }

        revalidate();
        repaint();
	}
	
    private GridBagConstraints getTableConstraints(int gridx, int gridy) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        c.insets = new Insets(10, 10, 10, 10);
        return c;
    }

    public void onScreenActive() {

    }

    public void onScreenInactive() {

    }
}
