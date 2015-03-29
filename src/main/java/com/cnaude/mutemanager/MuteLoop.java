package com.cnaude.mutemanager;

import java.util.ArrayList;

public class MuteLoop {

    private final MuteManager plugin;
    private final int taskID;

    public MuteLoop(MuteManager instance) {
        plugin = instance;
        plugin.logInfo("MuteManager main loop running.");

        taskID = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                plugin.logDebug("Checking mute list.");
                if (plugin.muteList.isEmpty()) {
                    return;
                }
                ArrayList<MutedPlayer> unMuteList = new ArrayList();
                for (MutedPlayer mutedPlayer : plugin.muteList) {
                    if (!mutedPlayer.isMuted()) {
                        unMuteList.add(mutedPlayer);
                        plugin.logDebug("Unmuting " + mutedPlayer.getPlayerName());
                    }
                }
                for (MutedPlayer mutedPlayer : unMuteList) {
                    plugin.unMutePlayer(mutedPlayer);
                }
                unMuteList.clear();
            }
        }, 200, 200);
    }

    public void end() {
        this.plugin.getServer().getScheduler().cancelTask(taskID);
    }
}
