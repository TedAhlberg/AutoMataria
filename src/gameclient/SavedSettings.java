package gameclient;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SavedSettings {

    private String userName;
    private boolean music;
    private BufferedWriter bw;

    public SavedSettings() {

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setMusic(boolean music) {
        this.music = music;

    }

    public void writeToFile() {
        try {
            bw = new BufferedWriter(new FileWriter("UserSettings.txt"));
            bw.write(userName);
            bw.newLine();
          
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
