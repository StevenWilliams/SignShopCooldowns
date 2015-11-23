package net.onepeace.signshops.listeners;

import com.almworks.sqlite4java.SQLiteException;
import net.onepeace.signshops.SignShopCooldowns;
import net.onepeace.signshops.data.UsesDatabase;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.wargamer2010.signshop.events.SSPostTransactionEvent;
import org.wargamer2010.signshop.events.SSPreTransactionEvent;
import org.wargamer2010.signshop.player.SignShopPlayer;

public class ShopUse implements Listener {

    public static int itemLimits = 100;
    public static float moneyLimits = 1000;
    public static long cooldownMillis = 10000;

    private UsesDatabase database;
    private SignShopCooldowns plugin;

    public ShopUse(SignShopCooldowns plugin, UsesDatabase database)
    {
        this.plugin = plugin;
        this.database = database;
    }

     //TODO: load hashmaps from file in onenable, and pass them to here.

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
        try {
            database.insertUse(event.getSign().getLocation(), event.getPlayer().getPlayer().getUniqueId(), getItemAmount(event.getItems()), event.getPrice());
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private int getItemAmount(ItemStack[] stacks) {
        int itemAmount = 0;
        for(ItemStack stack : stacks) {
            itemAmount += stack.getAmount();
        }
        return itemAmount;
    }

    @EventHandler
    public void onSSPreTransactionEvent(SSPreTransactionEvent event) {
        if(event.isCancelled() || event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getRequirementsOK())
            return;

        try {
            SignShopPlayer player = event.getPlayer();
            long since = System.currentTimeMillis() - 60L * 60L * 1000L; //1 hour
            int itemSum = database.getItemSum(event.getSign().getLocation(), event.getPlayer().getPlayer().getUniqueId(), since); //items sold in since since time

            if(getItemAmount(event.getItems()) + itemSum > itemLimits) {
                player.sendMessage(ChatColor.RED + "Cannot complete sale!");
                player.sendMessage("You can only sell up to "  + itemLimits + " items within " + since);
                event.setCancelled(true);
                return;
            }
            double priceSum = database.getMoneySum(event.getSign().getLocation(), event.getPlayer().getPlayer().getUniqueId(), since);

            if(priceSum + event.getPrice() > moneyLimits) {
                player.sendMessage(ChatColor.RED + "Cannot complete sale!");
                player.sendMessage("You can only sell up to $" + moneyLimits + " within " + since);
                event.setCancelled(true);
                return;
            }
        } catch (SQLiteException ex){
            event.getPlayer().sendMessage(ChatColor.RED + "An internal error has occurred!!");
            ex.printStackTrace();
        }
    }
}
