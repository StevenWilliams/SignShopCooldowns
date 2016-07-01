package net.onepeace.signshops.cooldowns.listeners;

import net.onepeace.signshops.cooldowns.SignShopCooldowns;
import net.onepeace.signshops.cooldowns.data.UsesDatabase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.wargamer2010.signshop.events.SSPreTransactionEvent;

import java.sql.SQLException;

public class ShopUseGlobal extends ShopUse {

    public ShopUseGlobal(SignShopCooldowns plugin, ConfigurationSection section, String type, UsesDatabase database) {
        super(plugin, section, type, database);

    }

    @Override
    int getItemSum(SSPreTransactionEvent event) throws SQLException {
        int itemSum = database.getItemSum(type, event.getPlayer().getPlayer().getUniqueId(), System.currentTimeMillis() - period); //items sold in since since time
        return itemSum;
    }

    @Override
    double getPriceSum(SSPreTransactionEvent event) throws SQLException {
        double priceSum = database.getMoneySum(type, event.getPlayer().getPlayer().getUniqueId(),System.currentTimeMillis() - period);
        return priceSum;
    }
}
