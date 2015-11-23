package net.onepeace.signshops.data;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

public class UsesDatabase {
    private static final String TABLE_NAME = "uses";
     private static final String CREATE_USES_TABLE =  "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " (" +
            "`sign` varchar(255) not null, `player` varchar(64) not null,`timestamp` long not null, `items` int not null, `money` double not null);";

    private static final String FILE_NAME = "shop-use.db";

    private SQLiteConnection db;
    private JavaPlugin plugin;

    public UsesDatabase(JavaPlugin plugin) throws SQLiteException {
        db = new SQLiteConnection(new File(FILE_NAME));
        db.open(true);
        db.exec(CREATE_USES_TABLE);

        File databaseFile = new File(plugin.getDataFolder(), FILE_NAME);
        if (!databaseFile.exists()){
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create database file.");
                e.printStackTrace();
            }
        }

        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
        } catch ( Exception e ) {
           e.printStackTrace();
        }
        System.out.println("Opened database successfully");
    }

    public void insertUse(Location location, UUID playerUUID, int items, double money) throws SQLiteException {
        SQLiteStatement s = db.prepare("INSERT into " + TABLE_NAME + " (`sign`, `player`, `timestamp`, `items`, `money`) VALUES (?,?,?,?,?)");
        s.bind(1, location.toString());
        s.bind(2, playerUUID.toString());
        s.bind(3, System.currentTimeMillis());
        s.bind(4, items);
        s.bind(5, money);
        s.stepThrough();
    }

    public int getItemSum(Location location, UUID playerUUID, Long since) throws SQLiteException {
        SQLiteStatement s = db.prepare("SELECT sum(items) from " + TABLE_NAME + " WHERE timestamp >= " + since);
        s.step();
        return s.columnInt(0);
    }

    public double getMoneySum(Location location, UUID playerUUID, Long since) throws SQLiteException {
        SQLiteStatement s = db.prepare("SELECT sum(money) from " + TABLE_NAME + " WHERE timestamp >= " + since);
        s.step();
        return s.columnDouble(0);
    }

    public void cleanup(Long before) throws SQLiteException {
        SQLiteStatement s = db.prepare("DELETE from " + TABLE_NAME + " WHERE timestamp < " + before);
        s.stepThrough();
    }

}
