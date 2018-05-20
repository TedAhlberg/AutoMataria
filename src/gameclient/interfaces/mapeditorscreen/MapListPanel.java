package gameclient.interfaces.mapeditorscreen;

import common.*;
import gameclient.interfaces.AMButton;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * @author Johannes Blüml
 */
public class MapListPanel extends JComponent {

    private Consumer<String> loadMapConsumer, deleteMapConsumer;

    public MapListPanel(Consumer<String> loadMapConsumer, Consumer<String> deleteMapConsumer) {
        this.loadMapConsumer = loadMapConsumer;
        this.deleteMapConsumer = deleteMapConsumer;
        setLayout(new GridBagLayout());
    }

    public void reload() {
        removeAll();
        for (String map : Maps.getInstance().getUserMapList()) {
            addMap(map);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 0;
        gbc.weighty = 1;
        add(Box.createVerticalStrut(0), gbc);

        repaint();
    }

    private void addMap(String map) {
        GameMap gameMap = Maps.getInstance().get(map);

        AMButton loadButton = new AMButton("LOAD");
        AMButton deleteButton = new AMButton("DELETE");
        JLabel nameLabel = new JLabel(gameMap.getName().toUpperCase());
        nameLabel.setForeground(new Color(0x9b59b6));
        nameLabel.setLabelFor(loadButton);
        JLabel playersLabel = new JLabel(gameMap.getPlayers() + " PLAYERS");
        playersLabel.setForeground(Color.LIGHT_GRAY);
        JLabel gridSizeLabel = new JLabel(Utility.getGridSizeName(gameMap.getGrid()).toUpperCase() + " GRIDS");
        gridSizeLabel.setForeground(Color.LIGHT_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        add(nameLabel, gbc);

        gbc.gridx = 1;
        add(playersLabel, gbc);

        gbc.gridx = 2;
        add(gridSizeLabel, gbc);

        loadButton.addActionListener(e -> loadMapConsumer.accept(gameMap.getName()));
        gbc.gridx = 3;
        add(loadButton, gbc);

        deleteButton.addActionListener(e -> deleteMapConsumer.accept(gameMap.getName()));
        gbc.gridx = 4;
        add(deleteButton, gbc);
    }
}
