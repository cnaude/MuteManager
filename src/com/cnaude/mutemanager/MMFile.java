/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.mutemanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;

/**
 *
 * @author cnaude
 */
public class MMFile {
    private final MuteManager plugin;
    private File dataFolder;

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
            File mFile = new File(this.dataFolder, "mute.list");            
            BufferedWriter out = new BufferedWriter(new FileWriter(mFile));
            for (Map.Entry<String, Long> entry : this.plugin.mList.entrySet()) {                           
                    out.write(entry.getKey() + "=" + entry.getValue() + "\n");    
            }
            out.close();
            saved = true;
        } catch (Exception e) {
            plugin.logError(e.getMessage());
            saved = false;
        }
        return saved;
    }
    
    public boolean saveMuteReasonList() {
        boolean saved;
        if (!dataFolderExists()) {
            plugin.logError("Unable to find data folder! [" + this.dataFolder.getAbsolutePath() + "]");
            return false;
        }        
        try {
            File mFile = new File(this.dataFolder, "mutereason.list");            
            BufferedWriter out = new BufferedWriter(new FileWriter(mFile));
            for (Map.Entry<String, String> entry : this.plugin.mReason.entrySet()) {                           
                    out.write(entry.getKey() + "=" + entry.getValue() + "\n");    
            }
            out.close();
            saved = true;
        } catch (Exception e) {
            plugin.logError(e.getMessage());
            saved = false;
        }
        return saved;
    }
    
    public boolean loadMuteList() {
        if (!dataFolderExists()) {
            plugin.logInfo("Unable to find data folder! [" + this.dataFolder.getAbsolutePath() + "]");
            return false;
        }        
        File muteFile = new File(this.dataFolder, "mute.list");
        if (muteFile.exists()) {
            plugin.logInfo("Attempting to load muted players.");            
            try {             
                BufferedReader in = new BufferedReader(new FileReader(muteFile));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("\n")) {
                        continue;
                    }                    
                    String[] parts = line.split("=", 2);
                    String pName = parts[0];
                    Long expTime = Long.valueOf(parts[1]);                    
                    this.plugin.mList.put(pName, expTime);                                        
                    plugin.logInfo("Player " + pName + " is muted for " + plugin.expireTime(pName));
                }
                return true;
            } catch (Exception e) {
                plugin.logError(e.getMessage());
            }            
        }       
        return false;
    }
    
    public boolean loadMuteReasonList() {
        if (!dataFolderExists()) {
            plugin.logInfo("Unable to find data folder! [" + this.dataFolder.getAbsolutePath() + "]");
            return false;
        }        
        File muteFile = new File(this.dataFolder, "mutereason.list");
        if (muteFile.exists()) {
            plugin.logInfo("Attempting to load muted players reason list.");            
            try {             
                BufferedReader in = new BufferedReader(new FileReader(muteFile));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("\n")) {
                        continue;
                    }                    
                    String[] parts = line.split("=", 2);
                    String pName = parts[0];
                    String reason = parts[1];             
                    this.plugin.mReason.put(pName, reason);                                        
                    plugin.logInfo("Player " + pName + " mute reason: " + reason);
                }
                return true;
            } catch (Exception e) {
                plugin.logError(e.getMessage());
            }            
        }       
        return false;
    }
}
