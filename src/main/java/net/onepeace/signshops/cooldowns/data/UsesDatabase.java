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
            "`sign` varchar(255) not null, `player` varchar(64) not null, `sign_type` varchar(32) not null, `timemilli` long not null, `items` int not null, `money` double not null);";

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
            db.setAutoCommit(false);
            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        } catch ( Exception e ) {
           e.printStackTrace();
        }
        Statement stmt = db.createStatement();
        stmt.execute(CREATE_USES_TABLE);
        stmt.close();
        System.out.println("Opened database successfully");
    }

    public void insertUse(Location location, String type, UUID playerUUID, int items, double money) throws SQLException {
        PreparedStatement stmt = db.prepareStatement("INSERT into " + TABLE_NAME + " (sign, player, sign_type, timemilli, items, money) VALUES (?,?,?,?,?,? );");

        stmt.setString(1, location.toString());
        stmt.setString(2, playerUUID.toString());
        stmt.setString(3, type);
        stmt.setLong(4, System.currentTimeMillis());
        stmt.setInt(5, items);
        stmt.setDouble(6, money);

        stmt.executeUpdate();
        stmt.close();
    }

    private int intSum(ResultSet rs) throws SQLException {
        int sum = 0;
        while (rs.next()) {
            int c = rs.getInt(1);
            sum = sum + c;
        }
        rs.close();
        return sum;
    }


    public int getItemSum(Location location, UUID playerUUID, Long since) throws SQLException {
        PreparedStatement stmt = db.prepareStatement("SELECT sum(items) from " + TABLE_NAME +
                " WHERE timemilli >= ? AND player = ? AND sign = ?");
        stmt.setLong(1, since);
        stmt.setString(2, playerUUID.toString());
        stmt.setString(3, location.toString());

        ResultSet rs = stmt.executeQuery();
        int sum = intSum(rs);
        stmt.close();
        return sum;
    }

    public int getItemSum(String type, UUID playerUUID, long since) throws SQLException {
        PreparedStatement stmt = db.prepareStatement("SELECT sum(items) from " + TABLE_NAME +
                " WHERE timemilli >= ? AND player = ? AND sign_type = ?");
        stmt.setLong(1, since);
        stmt.setString(2, playerUUID.toString());
        stmt.setString(3, type);

        ResultSet rs = stmt.executeQuery();
        int sum = intSum(rs);
        stmt.close();
        return sum;
    }

    public double getMoneySum(Location location, UUID playerUUID, Long since) throws SQLException {
        PreparedStatement stmt = db.prepareStatement("SELECT sum(money) from " + TABLE_NAME +
                " WHERE timemilli >= ? AND player = ? AND sign = ?");
        stmt.setLong(1, since);
        stmt.setString(2, playerUUID.toString());
        stmt.setString(3, location.toString());

        ResultSet rs = stmt.executeQuery();

        double sum = doubleSum(rs);
        stmt.close();
        return sum;
    }

    public double getMoneySum(String type, UUID playerUUID, Long since) throws SQLException {
        PreparedStatement stmt = db.prepareStatement("SELECT sum(money) from " + TABLE_NAME +
                " WHERE timemilli >= ? AND player = ? AND sign_type = ?");
        stmt.setLong(1, since);
        stmt.setString(2, playerUUID.toString());
        stmt.setString(3, type);

        ResultSet rs = stmt.executeQuery();
        double sum = doubleSum(rs);
        stmt.close();
        return sum;
    }

    public double doubleSum(ResultSet rs) throws SQLException {
        double sum = 0;
        while (rs.next()) {
            double c = rs.getDouble(1);
            sum = sum + c;
        }
        rs.close();
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

    public void commit() throws SQLException {
        db.commit();
    }

}
