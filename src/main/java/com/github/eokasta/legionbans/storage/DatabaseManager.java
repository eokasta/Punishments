package com.github.eokasta.legionbans.storage;

import com.github.eokasta.legionbans.LegionBansPlugin;
import com.github.eokasta.legionbans.punishment.Punishment;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends com.github.aldevteam.core.storage.sql.DatabaseManager {

    private final String punishmentTable;

    private final LegionBansPlugin plugin;

    public DatabaseManager(LegionBansPlugin plugin) {
        super(plugin);
        this.plugin = plugin;

        this.punishmentTable = plugin.getSettings().getSQLSettings().getString("table");
    }

    public void insertPunishment(Punishment punishment) {
        try (final PreparedStatement statement = plugin.getDatabaseManager().getSql()
                .prepareStatement(String.format("INSERT INTO %s (player, json) VALUES (?,?)",
                        punishmentTable))
        ) {
            statement.setString(1, punishment.getPlayer().toLowerCase());
            statement.setString(2, plugin.getGson().toJson(punishment));
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Punishment> getPunishments(String player) {
        final List<Punishment> punishments = new ArrayList<>();

        try (final PreparedStatement statement = getSql().getConnection().prepareStatement(String.format("SELECT * FROM %s WHERE player LIKE ?", punishmentTable))) {

            statement.setString(1, player.toLowerCase());

            try (final ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    final Punishment punishment = plugin.getGson().fromJson(resultSet.getString("json"), Punishment.class);
                    punishment.setId(resultSet.getInt("id"));
                    punishments.add(punishment);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return punishments;
    }

    public void updatePunishment(Punishment punishment) {
        try (final PreparedStatement statement = getSql().getConnection().prepareStatement(String.format("UPDATE %s SET json = ? WHERE id = ?", punishmentTable))) {
            statement.setString(1, plugin.getGson().toJson(punishment));
            statement.setInt(2, punishment.getId());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePunishment(String player) {
        try (final PreparedStatement statement = plugin.getDatabaseManager().getSql()
                .prepareStatement(String.format("DELETE FROM %s WHERE id = ?",
                        punishmentTable))
        ) {
            statement.setString(1, player.toLowerCase());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        final ConfigurationSection sqlSettings = plugin.getSettings().getSQLSettings();
        final String host = sqlSettings.getString("host");
        final String database = sqlSettings.getString("database");
        final String user = sqlSettings.getString("user");
        final String password = sqlSettings.getString("password");
        initMySQL(host, database, user, password);
    }

    public void initTables() {
        try {
            getSql().createTable(punishmentTable, "id INT PRIMARY KEY AUTO_INCREMENT, player VARCHAR(16) NOT NULL, json JSON NOT NULL, INDEX(player)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
