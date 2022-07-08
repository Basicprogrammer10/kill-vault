package com.connorcodde.killvault.misc;

import com.connorcodde.killvault.KillVault;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    public Connection connection;

    public Database(String path) {
        // Init database
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + KillVault.plugin.getDataFolder() + File.separator + path);

            // Init tables
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("PRAGMA synchronous = NORMAL");
            stmt.executeUpdate("PRAGMA journal_mode = WAL");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS deaths (killer TEXT NOT NULL, dieer TEXT NOT NULL, deathInventory TEXT, deathMessage TEXT, deathTime INTEGER NOT NULL)");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}