package net.onepeace.signshops;

import com.almworks.sqlite4java.SQLiteException;
import net.onepeace.signshops.data.UsesDatabase;
import net.onepeace.signshops.tasks.Cleanup;
import org.bukkit.plugin.java.JavaPlugin;

public class SignShopCooldowns extends JavaPlugin {
    private UsesDatabase usesDatabase;
    @Override
    public void onEnable() {
        try {
            usesDatabase = new UsesDatabase();
            long cleanupTime = this.getConfig().getLong("cleanup");
            new Cleanup(usesDatabase, cleanupTime).runTaskTimer(this, 0, 200);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }
    public UsesDatabase getUsesDatabase () {
        return usesDatabase;
    }
}

