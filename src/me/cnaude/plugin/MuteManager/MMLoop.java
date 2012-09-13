package me.cnaude.plugin.MuteManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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
            ArrayList unMuteList = new ArrayList();            
            for (String pName : plugin.mList.keySet()) {        
                if (plugin.mList.containsKey(pName)) {
                    long curTime = System.currentTimeMillis();
                    long expTime = plugin.mList.get(pName);
                    if (expTime <= curTime) {                                                
                        unMuteList.add(pName);
                    } 
                }
            }
            for (Object pName : unMuteList) {
                plugin.unMutePlayer((String)pName); 
            }
            unMuteList.clear();
        }
    }

    public void end() {
        timer.cancel();
    }
}
