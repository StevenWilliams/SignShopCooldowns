package net.onepeace.signshops.cooldowns.listeners;

import net.onepeace.signshops.cooldowns.SignShopCooldowns;
import net.onepeace.signshops.cooldowns.data.UsesDatabase;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.wargamer2010.signshop.events.SSPostTransactionEvent;
import org.wargamer2010.signshop.events.SSPreTransactionEvent;
import org.wargamer2010.signshop.player.SignShopPlayer;

import java.sql.SQLException;

public class ShopUse implements Listener {

    private final int itemLimits;
    private final double moneyLimits;
    private final long period;

    private final UsesDatabase database;
    private final SignShopCooldowns plugin;
    private final String type;

    private final String waitMsgItem;
    private final String waitMsgMoney;

    public ShopUse(SignShopCooldowns plugin, String type, UsesDatabase database)
    {
        this.plugin = plugin;
        this.database = database;
        this.type = type;
        this.itemLimits = plugin.getConfig().getInt("types." + type + ".item-limit");
        this.moneyLimits = plugin.getConfig().getDouble("types." + type + ".money-limit");
        this.period = plugin.getConfig().getLong("types." + type + ".period") * 1000L;
        this.waitMsgItem = plugin.getConfig().getString("types." + type + ".wait-msg-item");
        this.waitMsgMoney = plugin.getConfig().getString("types." + type + ".wait-msg-money");
    }


    @EventHandler
    public void onSSPostTransactionEvent(SSPostTransactionEvent event) {
        if(event.isCancelled() || event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getRequirementsOK())
            return;

        Sign sign = (Sign) event.getSign().getState();

        if(!type.equalsIgnoreCase(ChatColor.stripColor(sign.getLine(0)))) return;

        try {
            database.insertUse(event.getSign().getLocation(), event.getPlayer().getPlayer().getUniqueId(), getItemAmount(event.getItems()), event.getPrice());
        } catch (SQLException e) {
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

        Sign sign = (Sign) event.getSign().getState();

        if(!type.equalsIgnoreCase(ChatColor.stripColor(sign.getLine(0)))) return;

        try {
            SignShopPlayer player = event.getPlayer();
            long since = period;
            int itemSum = database.getItemSum(event.getSign().getLocation(), event.getPlayer().getPlayer().getUniqueId(), System.currentTimeMillis() - since); //items sold in since since time

            if(getItemAmount(event.getItems()) + itemSum > itemLimits) {
                player.sendMessage(ChatColor.RED + "Cannot complete sale!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', waitMsgItem));
                event.setCancelled(true);
                return;
            }
            double priceSum = database.getMoneySum(event.getSign().getLocation(), event.getPlayer().getPlayer().getUniqueId(), since);

            if(priceSum + event.getPrice() > moneyLimits) {
                player.sendMessage(ChatColor.RED + "Cannot complete sale!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', waitMsgMoney));
                event.setCancelled(true);
                return;
            }
        } catch (SQLException ex){
            event.getPlayer().sendMessage(ChatColor.RED + "An internal error has occurred!!");
            ex.printStackTrace();
        }
    }
}
