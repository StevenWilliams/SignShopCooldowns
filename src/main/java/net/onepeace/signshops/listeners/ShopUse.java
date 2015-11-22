package net.onepeace.signshops.listeners;

import net.onepeace.signshops.PlayerSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.wargamer2010.signshop.events.SSPostTransactionEvent;
import org.wargamer2010.signshop.events.SSPreTransactionEvent;
import org.wargamer2010.signshop.player.SignShopPlayer;

import java.util.HashMap;

public class ShopUse implements Listener {
    public static HashMap<PlayerSign, Integer> itemLimit;
    public static HashMap<PlayerSign, Float> moneyLimit;
    public static HashMap<PlayerSign, Long> cooldown;

    public static int itemLimits = 100;
    public static float moneyLimits = 1000;
    public static long cooldownMillis = 10000;

    public ShopUse(itemLimit, moneyLimit, cooldown) //TODO: load hashmaps from file in onenable, and pass them to here.

    //TODO: check if the player has used the item limit for sign, check if the player has used the money limit
    //check pretransaction to see if transaction would violate limit (or if cooldown is in effect0
    //if false, then continue on, and add to the item limit and money limit
    //if true, then add the time in millis + cooldownlength to hashmap + (remove from other hashmaps).
    //create task that runs through the cooldown hashmaps, and remove if cooldowntime is in the past

    //save to file with task, including on disable
    //load on-enable


    //TIMEstamp, playersign,

    @EventHandler
    public void onSSPostTransactionEvent(SSPostTransactionEvent event) {
        SignShopPlayer player = event.getPlayer();
        PlayerSign playerSign = new PlayerSign(player.getPlayer(), event.getShop().getSignLocation().getBlock());

        int itemBefore = 0;
        if(itemLimit.containsKey(playerSign)) itemBefore = itemLimit.get(playerSign);
        itemLimit.put(playerSign, itemBefore);
        float moneyBefore = 0F;



        if(itemLimit.get(playerSign) >= itemLimits) {
            player.sendMessage("The item limit for this shop has been reached: " + itemLimit.get(playerSign) + "/" + itemLimits);
            itemLimit.remove(playerSign);
            moneyLimit.remove(playerSign);
            cooldown.put(playerSign, System.currentTimeMillis());
        }else if(moneyLimit.get(playerSign) >= moneyLimits ) {
            player.sendMessage("The money limit for this shop has been reached: " + moneyLimit.get(playerSign) + "/" + moneyLimits);
            itemLimit.remove(playerSign);
            moneyLimit.remove(playerSign);
            cooldown.put(playerSign, System.currentTimeMillis());
        }

    }

    @EventHandler
    public void onSSPreTransactionEvent(SSPreTransactionEvent event) {
        SignShopPlayer player = event.getPlayer();
        PlayerSign playerSign = new PlayerSign(player.getPlayer(), event.getShop().getSignLocation().getBlock());
        if(cooldown.containsKey(playerSign)) {
            if(cooldown.get(playerSign) + cooldownMillis < System.currentTimeMillis()){
                long waitingtime = (cooldown.get(playerSign) + cooldownMillis) - System.currentTimeMillis();
                player.sendMessage("Please wait: " + waitingtime);
                event.setCancelled(true);
                return;
            }
        }
        if(itemLimit.containsKey(playerSign)) {
            int itemsSoFar = itemLimit.get(playerSign);
            if(event.getItems().length + itemsSoFar > itemLimits) {
                event.setCancelled(true);
                player.sendMessage("This transaction goes above the item limit of " + itemLimits);
            }
        } else if (moneyLimit.containsKey(playerSign)) {
            float moneySoFar = moneyLimit.get(playerSign);
            if(event.getPrice() + moneySoFar > moneyLimits)
            {
                event.setCancelled(true);
                player.sendMessage("This transaction goes above the money limit of " + moneyLimits);
            }
        }
    }
}
