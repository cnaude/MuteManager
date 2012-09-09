package me.cnaude.plugin.MuteManager;

import java.util.ArrayList;
import java.util.HashMap;
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
            ArrayList al = new ArrayList();            
            Set st = plugin.mList.keySet();
            Iterator itr = st.iterator();
            while(itr.hasNext()) {                
                String pName = (String)itr.next();                    
                if (plugin.mList.containsKey(pName)) {
                    long curTime = System.currentTimeMillis();
                    long expTime = plugin.mList.get(pName);
                    if (expTime <= curTime) {                                                
                        al.add(pName);
                    } 
                }
            }
            for (Object pName : al) {
                plugin.unMutePlayer((String)pName); 
            }
        }
    }

    public void end() {
        timer.cancel();
    }
}
