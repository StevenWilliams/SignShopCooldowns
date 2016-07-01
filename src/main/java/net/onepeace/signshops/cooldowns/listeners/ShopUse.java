package net.onepeace.signshops.cooldowns.listeners;

import net.onepeace.signshops.cooldowns.SignShopCooldowns;
import net.onepeace.signshops.cooldowns.data.UsesDatabase;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.wargamer2010.signshop.events.SSPreTransactionEvent;
import org.wargamer2010.signshop.player.SignShopPlayer;

import java.sql.SQLException;

public class ShopUse implements Listener {

    private final int itemLimits;
    private final double moneyLimits;
    final long period;

    final UsesDatabase database;
    final SignShopCooldowns plugin;
    final String type;

    private final String waitMsgItem;
    private final String waitMsgMoney;


    public ShopUse(SignShopCooldowns plugin, ConfigurationSection section, String type, UsesDatabase database)
    {
        this.plugin = plugin;
        this.database = database;
        this.type = type;
        this.itemLimits = section.getInt(type + ".item-limit");
        this.moneyLimits = section.getDouble(type + ".money-limit");
        this.period = section.getLong(type + ".period") * 1000L;
        this.waitMsgItem = section.getString(type + ".wait-msg-item");
        this.waitMsgMoney = section.getString(type + ".wait-msg-money");

    }

    double getPriceSum(SSPreTransactionEvent event) throws SQLException {
        double priceSum = database.getMoneySum(event.getSign().getLocation(), event.getPlayer().getPlayer().getUniqueId(),System.currentTimeMillis() - period);
        return priceSum;
    }

    int getItemSum(SSPreTransactionEvent event) throws SQLException {
        int itemSum = database.getItemSum(event.getSign().getLocation(), event.getPlayer().getPlayer().getUniqueId(), System.currentTimeMillis() - period); //items sold in since since time
        return itemSum;
    }



    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSSPreTransactionEvent(SSPreTransactionEvent event) {

        if(event.isCancelled() || event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getRequirementsOK())
            return;

        Sign sign = (Sign) event.getSign().getState();

        if(!type.equalsIgnoreCase(ChatColor.stripColor(sign.getLine(0)))) return;

        try {
            SignShopPlayer player = event.getPlayer();
            int itemSum = getItemSum(event);

            if(SignShopCooldowns.getItemAmount(event.getItems()) + itemSum > itemLimits) {
                player.sendMessage(ChatColor.RED + "Cannot complete sale!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', waitMsgItem));
                event.setCancelled(true);
                return;
            }

            double priceSum = getPriceSum(event);
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
