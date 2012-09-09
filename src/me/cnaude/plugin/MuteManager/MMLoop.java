package me.cnaude.plugin.MuteManager;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MMLoop {

    Timer timer;
    private final MM plugin;

    public MMLoop(MM instance) {
        plugin = instance;
        timer = new Timer();
        timer.schedule(new MMLoop.muteTask(), 0, 5000);        
        plugin.logInfo("MuteManager main loop running.");
    }

    class muteTask extends TimerTask {

        @Override
        public void run() {            
            for (Player player : Bukkit.getOnlinePlayers()) {
                String pName = player.getName();            
                if (plugin.mList.containsKey(pName)) {
                    long curTime = System.currentTimeMillis();
                    long expTime = plugin.mList.get(pName);
                    if (expTime <= curTime) {                        
                        plugin.unMutePlayer(player);                        
                    } 
                }
            }
        }
    }

    public void end() {
        timer.cancel();
    }
}
