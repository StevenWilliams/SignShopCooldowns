package net.onepeace.signshops.cooldowns.tasks;

import net.onepeace.signshops.cooldowns.data.UsesDatabase;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class Commit extends BukkitRunnable {
    private final UsesDatabase database;

    public Commit(UsesDatabase database) {
        this.database = database;
    }

    @Override
    public void run() {
        try {
            database.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
