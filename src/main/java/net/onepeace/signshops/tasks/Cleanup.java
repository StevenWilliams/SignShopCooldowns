package net.onepeace.signshops.tasks;

import net.onepeace.signshops.PlayerSign;
import net.onepeace.signshops.listeners.ShopUse;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;

public class Cleanup extends BukkitRunnable {

    @Override
    public void run() {
            Iterator it = ShopUse.cooldown.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if((long) pair.getValue() + ShopUse.cooldownMillis >= System.currentTimeMillis())
                {
                    it.remove();
                    continue;
                }
                PlayerSign playerSign = (PlayerSign) pair.getKey();
                if(playerSign.getPlayer().getLastPlayed() + ShopUse.cooldownMillis <= System.currentTimeMillis())
                {
                    it.remove();
                    if(ShopUse.itemLimit.containsKey(playerSign))ShopUse.itemLimit.remove(playerSign); //TODO: check if works
                    if(ShopUse.moneyLimit.containsKey(playerSign))ShopUse.moneyLimit.remove(playerSign);
                    continue;
                }
                //TODO: also do one to see if sign still exists
            }
    }
}
