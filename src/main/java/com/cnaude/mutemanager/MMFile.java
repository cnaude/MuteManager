package com.cnaude.mutemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author cnaude
 */
public class MMFile {

    private final MuteManager plugin;
    private File dataFolder;
    final String OLD_MUTE_FILE = "mute.list";
    final String OLD_REASON_FILE = "mutereason.list";
    final String MUTE_FILE = "muted-players.ser";

    private boolean dataFolderExists() {
        this.dataFolder = new File("plugins/MuteManager");
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
        return this.dataFolder.exists();
    }

    public MMFile(MuteManager instance) {
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
            oos.writeObject(plugin.mList);
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
        File oldMuteFile = new File(this.dataFolder, OLD_MUTE_FILE);
        File newMuteFile = new File(this.dataFolder, MUTE_FILE);
        if (oldMuteFile.exists()) {
            ArrayList<String> playerNames = new ArrayList<>();
            HashMap<String, Long> tmpList = new HashMap<>();            
            try {
                BufferedReader in = new BufferedReader(new FileReader(oldMuteFile));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("\n")) {
                        continue;
                    }
                    String[] parts = line.split("=", 2);
                    String pName = parts[0];
                    Long expTime = Long.valueOf(parts[1]);
                    tmpList.put(pName, expTime);
                    playerNames.add(pName);
                }
                UUIDFetcher fetcher = new UUIDFetcher(playerNames);
                Map<String, UUID> response = null;
                try {
                    response = fetcher.call();
                } catch (Exception e) {
                    plugin.logError("Exception while running UUIDFetcher!");
                    plugin.logError(e.getMessage());
                }
                if (response != null) {
                    for (String pName : response.keySet()) {
                        if (tmpList.containsKey(pName)) {
                            UUID uuid = response.get(pName);
                            plugin.logInfo("Converting player name '" + pName + "' to UUID: " + uuid);
                            long expTime = tmpList.get(pName);
                            this.plugin.mList.add(new MutedPlayer(pName, uuid, expTime, ""));
                        }
                    }
                }                
                oldMuteFile.renameTo(new File(this.dataFolder, OLD_MUTE_FILE + ".preUUID"));
                return true;
            } catch (IOException | NumberFormatException e) {
                plugin.logError(e.getMessage());
            }            
        }
        if (newMuteFile.exists()) {
            plugin.logInfo("Loading mute list from file: " + MUTE_FILE);
            try {
                FileInputStream fis = new FileInputStream(newMuteFile);
                try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                    plugin.mList = (ArrayList<MutedPlayer>) ois.readObject();
                    ois.close();
                    fis.close();
                }
            } catch (IOException | ClassNotFoundException ex) {
                plugin.logError(ex.getMessage());
                return false;
            }
            for (MutedPlayer mutedPlayer : plugin.mList) {
                plugin.logInfo("[Player: " + mutedPlayer.getPlayerName() + "] "
                + "[Duration: " + mutedPlayer.getExpiredTime(plugin.getMConfig()) + "] "
                + "[Reason: " + mutedPlayer.getReason() + "]");
            }
            return true;
        }
        return false;
    }

    public boolean loadMuteReasonList() {
        if (!dataFolderExists()) {
            plugin.logInfo("Unable to find data folder! [" + this.dataFolder.getAbsolutePath() + "]");
            return false;
        }
        File oldReasonFile = new File(this.dataFolder, OLD_REASON_FILE);
        if (oldReasonFile.exists()) {
            plugin.logInfo("Attempting to load muted players reason list: " + OLD_REASON_FILE);
            try {
                BufferedReader in = new BufferedReader(new FileReader(oldReasonFile));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("\n")) {
                        continue;
                    }
                    String[] parts = line.split("=", 2);
                    String pName = parts[0];
                    String reason = parts[1];
                    for (MutedPlayer mutedPlayer : plugin.mList) {
                        if (mutedPlayer.getPlayerName().equals(pName)) {
                            mutedPlayer.setReason(reason);
                            plugin.logInfo("Player " + pName + " mute reason: " + reason);
                        }
                    }
                }
                oldReasonFile.renameTo(new File(this.dataFolder, OLD_REASON_FILE + ".preUUID"));
                return true;
            } catch (IOException e) {
                plugin.logError(e.getMessage());
            }            
        }
        return false;
    }
}
