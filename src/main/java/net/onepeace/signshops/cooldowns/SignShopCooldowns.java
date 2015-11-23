package net.onepeace.signshops.cooldowns;

import net.onepeace.signshops.cooldowns.data.UsesDatabase;
import net.onepeace.signshops.cooldowns.listeners.ShopUse;
import net.onepeace.signshops.cooldowns.tasks.Cleanup;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Set;

public class SignShopCooldowns extends JavaPlugin {
    private UsesDatabase usesDatabase;
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        try {
            usesDatabase = new UsesDatabase(this);
            long cleanupTime = this.getConfig().getLong("cleanup") * 1000L;
            new Cleanup(usesDatabase, cleanupTime).runTaskTimer(this, 0, 200);
             Set<String> types = this.getConfig().getConfigurationSection("types").getKeys(false);
            for(String type : types) {
                this.getServer().getPluginManager().registerEvents(new ShopUse(this, type, usesDatabase), this);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onDisable() {
        try {
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

