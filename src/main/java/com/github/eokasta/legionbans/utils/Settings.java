package com.github.eokasta.legionbans.utils;

import com.github.aldevteam.core.utils.Helper;
import com.github.aldevteam.core.utils.Replacer;
import com.github.aldevteam.core.utils.YamlConfig;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Data
public class Settings {

    private final YamlConfig file;

    public String messageOf(String path) {
        return Helper.format(file.getConfig().getString("messages." + path, path + " is not defined."));
    }

    public String replaceOf(String path, Replacer replacer) {
        return Helper.format(replacer.replace(messageOf(path)));
    }

    public List<String> listMessageOf(String path) {
        final List<String> list = new ArrayList<>();

        for (String s : file.getConfig().getStringList("messages." + path))
            list.add(Helper.format(s));

        return list;
    }

    public List<String> listReplaceOf(String path, Replacer replacer) {
        final List<String> list = new ArrayList<>();

        for (String s : file.getConfig().getStringList("messages." + path))
            list.add(Helper.format(replacer.replace(s)));

        return list;
    }

    public List<String> getMuteBlockedCommands() {
        return file.getConfig().getStringList("mute-blocked-commands");
    }

    public ConfigurationSection getSQLSettings() {
        return file.getConfig().getConfigurationSection("mysql");
    }


}
