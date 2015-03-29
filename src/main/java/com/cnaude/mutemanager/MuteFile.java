package com.cnaude.mutemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *
 * @author cnaude
 */
public class MuteFile {

    private final MuteManager plugin;
    private File dataFolder;
    final String MUTE_FILE = "muted-players.ser";

    private boolean dataFolderExists() {
        this.dataFolder = new File("plugins/MuteManager");
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
        return this.dataFolder.exists();
    }

    public MuteFile(MuteManager instance) {
        this.plugin = instance;
    }

    public boolean saveMuteList() {
        boolean saved;
        if (!dataFolderExists()) {
            plugin.logError("Unable to find data folder! [" + this.dataFolder.getAbsolutePath() + "]");
            return false;
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(new File(this.dataFolder, MUTE_FILE)));
            oos.writeObject(plugin.muteList);
            saved = true;
        } catch (IOException ex) {
            plugin.logError(ex.getMessage());
            saved = false;
        }
        return saved;
    }

    public boolean loadMuteList() {
        if (!dataFolderExists()) {
            plugin.logInfo("Unable to find data folder! [" + this.dataFolder.getAbsolutePath() + "]");
            return false;
        }
        File newMuteFile = new File(this.dataFolder, MUTE_FILE);
        if (newMuteFile.exists()) {
            plugin.logInfo("Loading mute list from file: " + MUTE_FILE);
            try {
                FileInputStream fis = new FileInputStream(newMuteFile);
                try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                    plugin.muteList = (ArrayList<MutedPlayer>) ois.readObject();
                    ois.close();
                    fis.close();
                }
            } catch (IOException | ClassNotFoundException ex) {
                plugin.logError(ex.getMessage());
                return false;
            }
            for (MutedPlayer mutedPlayer : plugin.muteList) {
                plugin.logInfo("[Player: " + mutedPlayer.getPlayerName() + "] "
                + "[Duration: " + mutedPlayer.getExpiredTime(plugin.getMConfig()) + "] "
                + "[Reason: " + mutedPlayer.getReason() + "]");
            }
            return true;
        }
        return false;
    }

}
