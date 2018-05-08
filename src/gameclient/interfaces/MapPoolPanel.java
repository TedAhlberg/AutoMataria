package gameclient.interfaces;

import common.Maps;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

/**
 * @author Johannes Blüml
 */
public class MapPoolPanel extends JPanel {
    private int gridY = 0;
    private HashSet<String> selectedMaps = new HashSet<>();

    public MapPoolPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        String[] mapList = Maps.getInstance().getMapList();
        for (String map : mapList) {
            addMap(map);
        }
    }

    public String[] getSelectedMaps() {
        return selectedMaps.toArray(new String[0]);
    }

    private void addMap(String map) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.addChangeListener(e -> {
            if (checkBox.isSelected()) {
                selectedMaps.add(map);
            } else {
                selectedMaps.remove(map);
            }
        });
        checkBox.setText(map.toUpperCase());
        checkBox.setHorizontalAlignment(SwingConstants.LEFT);
        checkBox.setSelected(true);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        add(checkBox, c);
    }
}
