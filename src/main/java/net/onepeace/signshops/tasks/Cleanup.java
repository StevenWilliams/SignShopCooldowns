package net.onepeace.signshops.tasks;

import com.almworks.sqlite4java.SQLiteException;
import net.onepeace.signshops.data.UsesDatabase;
import org.bukkit.scheduler.BukkitRunnable;

public class Cleanup extends BukkitRunnable {
    private UsesDatabase database;
    private Long before;

    public Cleanup(UsesDatabase database, Long before) {
        this.database = database;
        this.before = before;
    }

    @Override
    public void run() {
        try {
            database.cleanup(System.currentTimeMillis() - before);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
}
