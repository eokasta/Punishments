package com.github.eokasta.legionbans;

import com.github.aldevteam.core.javaplugin.ExtendedJavaPlugin;
import com.github.aldevteam.core.utils.YamlConfig;
import com.github.eokasta.legionbans.commands.*;
import com.github.eokasta.legionbans.listeners.PunishmentListener;
import com.github.eokasta.legionbans.punishment.PunishmentManager;
import com.github.eokasta.legionbans.storage.DatabaseManager;
import com.github.eokasta.legionbans.utils.Settings;
import com.google.gson.Gson;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;

public class LegionBansPlugin extends ExtendedJavaPlugin {

    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Getter
    private Settings settings;
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private PunishmentManager punishmentManager;

    @Getter
    private Gson gson;

    @Override
    public void enable() {
        this.gson = new Gson();

        this.settings = new Settings(new YamlConfig("config.yml", this, true));

        this.databaseManager = new DatabaseManager(this);
        databaseManager.init();
        databaseManager.startConnection();
        databaseManager.initTables();

        this.punishmentManager = new PunishmentManager(this, "localhost", 6379);
        punishmentManager.init();

        new KickCommand(this);
        new BanCommand(this);
        new TempBanCommand(this);
        new UnbanCommand(this);
        new HistoricCommand(this);
        new TempMuteCommand(this);
        new UnmuteCommand(this);

        new PunishmentListener(this);
    }

}
