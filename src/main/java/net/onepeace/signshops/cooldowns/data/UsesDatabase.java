package net.onepeace.signshops.cooldowns.data;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class UsesDatabase {
    private static final String TABLE_NAME = "uses";
     private static final String CREATE_USES_TABLE =  "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " (" +
            "`sign` varchar(255) not null, `player` varchar(64) not null,`timemilli` long not null, `items` int not null, `money` double not null);";

    private static final String FILE_NAME = "shop-use.db";

    private Connection db;
    private JavaPlugin plugin;

    public UsesDatabase(JavaPlugin plugin) throws SQLException {
        File databaseFile = new File(plugin.getDataFolder(), FILE_NAME);
        if (!databaseFile.exists()){
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create database file.");
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            db = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
        } catch ( Exception e ) {
           e.printStackTrace();
        }
        Statement stmt = db.createStatement();
        stmt.execute(CREATE_USES_TABLE);
        stmt.close();
        System.out.println("Opened database successfully");
    }

    public void insertUse(Location location, UUID playerUUID, int items, double money) throws SQLException {
        PreparedStatement stmt = db.prepareStatement("INSERT into " + TABLE_NAME + " (sign, player, timemilli, items, money) VALUES (?,?,?,?,? );");

        stmt.setString(1, location.toString());
        stmt.setString(2, playerUUID.toString());
        stmt.setLong(3, System.currentTimeMillis());
        stmt.setInt(4, items);
        stmt.setDouble(5, money);
        stmt.executeUpdate();
        stmt.close();
    }

    public int getItemSum(Location location, UUID playerUUID, Long since) throws SQLException {
        PreparedStatement stmt = db.prepareStatement("SELECT sum(items) from " + TABLE_NAME +
                " WHERE timemilli >= " + since + " AND player = '" + playerUUID.toString() + "' AND sign = '" + location.toString() + "'");

        ResultSet rs = stmt.executeQuery();

        int sum = 0;
        while (rs.next()) {
            int c = rs.getInt(1);
            sum = sum + c;
        }
        rs.close();
        stmt.close();
        return sum;
    }

    public double getMoneySum(Location location, UUID playerUUID, Long since) throws SQLException {
        PreparedStatement stmt = db.prepareStatement("SELECT sum(money) from " + TABLE_NAME +
                " WHERE timemilli >= " + since + " AND player = '" + playerUUID.toString() + "' AND sign = '" + location.toString() + "'");

        ResultSet rs = stmt.executeQuery();

        double sum = 0;
        while (rs.next()) {
            double c = rs.getDouble(1);
            sum = sum + c;
        }
        rs.close();
        stmt.close();
        return sum;
    }

    public void cleanup(Long before) throws SQLException {
        PreparedStatement stmt = db.prepareStatement("DELETE from " + TABLE_NAME + " WHERE timemilli < " + before);
        stmt.execute();
        stmt.close();
    }

    public void close() throws SQLException {
        db.close();
    }

}
