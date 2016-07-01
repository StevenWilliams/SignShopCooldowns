package net.onepeace.signshops.cooldowns;

import net.onepeace.signshops.cooldowns.data.UsesDatabase;
import net.onepeace.signshops.cooldowns.listeners.ShopLog;
import net.onepeace.signshops.cooldowns.listeners.ShopUse;
import net.onepeace.signshops.cooldowns.listeners.ShopUseGlobal;
import net.onepeace.signshops.cooldowns.tasks.Cleanup;
import net.onepeace.signshops.cooldowns.tasks.Commit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Set;

public class SignShopCooldowns extends JavaPlugin {
    private UsesDatabase usesDatabase;

    public static int getItemAmount(ItemStack[] stacks) {
        int itemAmount = 0;
        for(ItemStack stack : stacks) {
            itemAmount += stack.getAmount();
        }
        return itemAmount;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        try {
            usesDatabase = new UsesDatabase(this);

            long cleanupTime = this.getConfig().getLong("cleanup") * 1000L;
            new Cleanup(usesDatabase, cleanupTime).runTaskTimerAsynchronously(this, 0, 200);
            new Commit(usesDatabase).runTaskTimerAsynchronously(this, 10, 200);


            Set<String> types = this.getConfig().getConfigurationSection("types").getKeys(false);
            for(String type : types) {
                this.getServer().getPluginManager().registerEvents(new ShopUse(this,getConfig().getConfigurationSection("types"), type, usesDatabase), this);
                ShopLog.addTypeLogger(this, type);
            }

            Set<String> globalTypes = this.getConfig().getConfigurationSection("global-types").getKeys(false);
            for(String globalType : globalTypes) {
                this.getServer().getPluginManager().registerEvents(new ShopUseGlobal(this,getConfig().getConfigurationSection("global-types"), globalType, usesDatabase), this);
                ShopLog.addTypeLogger(this, globalType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onDisable() {
        try {
            usesDatabase.commit();
            usesDatabase.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        usesDatabase = null;
    }
    public UsesDatabase getUsesDatabase () {
        return usesDatabase;
    }
}

