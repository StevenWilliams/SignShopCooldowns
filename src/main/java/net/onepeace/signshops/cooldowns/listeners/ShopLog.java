package net.onepeace.signshops.cooldowns.listeners;

import net.onepeace.signshops.cooldowns.SignShopCooldowns;
import net.onepeace.signshops.cooldowns.data.UsesDatabase;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.wargamer2010.signshop.events.SSPostTransactionEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShopLog implements Listener {
    private UsesDatabase database;
    private String type;

    private static List<String> loadedTypes = new ArrayList<>();

    public static boolean addTypeLogger(SignShopCooldowns plugin, String type) {
        if(loadedTypes.contains(type)) {
            return false;
        }
        plugin.getServer().getPluginManager().registerEvents(new ShopLog(type, plugin.getUsesDatabase()), plugin);
        return true;
    }

    public ShopLog(String type, UsesDatabase database) {
        this.database = database;
        this.type = type;
        loadedTypes.add(type);
    }
    @EventHandler
    public void onSSPostTransactionEvent(SSPostTransactionEvent event) {
        if(event.isCancelled() || event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getRequirementsOK())
            return;

        Sign sign = (Sign) event.getSign().getState();

        if(!type.equalsIgnoreCase(ChatColor.stripColor(sign.getLine(0)))) return;

        try {
            database.insertUse(event.getSign().getLocation(), type, event.getPlayer().getPlayer().getUniqueId(), SignShopCooldowns.getItemAmount(event.getItems()), event.getPrice());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
