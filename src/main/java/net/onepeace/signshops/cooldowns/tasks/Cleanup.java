package net.onepeace.signshops.cooldowns.tasks;

import net.onepeace.signshops.cooldowns.data.UsesDatabase;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class Cleanup extends BukkitRunnable {
    private final UsesDatabase database;
    private final Long before;

    public Cleanup(UsesDatabase database, Long before) {
        this.database = database;
        this.before = before;
    }

    @Override
    public void run() {
        try {
            database.cleanup(System.currentTimeMillis() - before);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
