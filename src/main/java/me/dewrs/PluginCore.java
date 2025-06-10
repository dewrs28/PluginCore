package me.dewrs;

import me.dewrs.commands.CommandManager;
import me.dewrs.events.EventManager;
import me.dewrs.updatechecker.UpdateCheckerManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginCore extends JavaPlugin {
    protected CommandManager commandManager;
    protected EventManager eventManager;
    protected UpdateCheckerManager updateCheckerManager;

    protected PluginDescriptionFile pluginDescriptionFile = this.getDescription();
    protected String version = pluginDescriptionFile.getVersion();
    protected String name = pluginDescriptionFile.getName();
    protected static String prefix;
    protected int spigotId;

    @Override
    public final void onEnable(){
        commandManager = new CommandManager(this);
        eventManager = new EventManager(this);
        updateCheckerManager = new UpdateCheckerManager(version, name);
        updateCheckerManager.manageUpdateChecker(spigotId);
        enable();
    }

    @Override
    public final void onDisable(){
        disable();
    }

    protected void enable(){

    }

    protected void disable(){

    }

    public static String getPrefix(){
        return prefix;
    }

    public boolean isOutdated(){
        return !updateCheckerManager.getLatestVersion().equals(version);
    }
}