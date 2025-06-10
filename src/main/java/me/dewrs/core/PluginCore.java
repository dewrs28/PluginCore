package me.dewrs.core;

import me.dewrs.core.bstats.Metrics;
import me.dewrs.core.commands.CommandManager;
import me.dewrs.core.database.ConnectionFactory;
import me.dewrs.core.database.StorageType;
import me.dewrs.core.events.EventManager;
import me.dewrs.core.logger.LogSender;
import me.dewrs.core.updatechecker.UpdateCheckerManager;
import me.dewrs.core.utils.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginCore extends JavaPlugin {
    protected CommandManager commandManager;
    protected EventManager eventManager;
    protected UpdateCheckerManager updateCheckerManager;

    protected PluginDescriptionFile pluginDescriptionFile = getDescription();
    protected String version = pluginDescriptionFile.getVersion();

    protected static String prefix;
    protected static String name;
    protected int spigotId;
    protected int metricsId;
    private static ServerVersion serverVersion;
    protected ConnectionFactory connectionFactory;
    protected StorageType storageType;

    @Override
    public final void onEnable(){
        setServerVersion();
        name = pluginDescriptionFile.getName();

        commandManager = new CommandManager(this).getInstance();
        eventManager = new EventManager(this).getInstance();
        updateCheckerManager = new UpdateCheckerManager(version, name);

        enable();

        updateCheckerManager.manageUpdateChecker(spigotId);
        new Metrics(this, metricsId);

        LogSender.sendMessage("&ahas been enabled!");
    }

    @Override
    public final void onDisable(){
        disable();
        LogSender.sendMessage("&ahas been disabled!");
    }

    public void setServerVersion(){
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        switch(bukkitVersion){
            case "1.20.5":
            case "1.20.6":
                serverVersion = ServerVersion.v1_20_R4;
                break;
            case "1.21":
            case "1.21.1":
                serverVersion = ServerVersion.v1_21_R1;
                break;
            case "1.21.2":
            case "1.21.3":
                serverVersion = ServerVersion.v1_21_R2;
                break;
            case "1.21.4":
                serverVersion = ServerVersion.v1_21_R3;
                break;
            case "1.21.5":
                serverVersion = ServerVersion.v1_21_R4;
                break;
            default:
                serverVersion = ServerVersion.valueOf(packageName.replace("org.bukkit.craftbukkit.", ""));
        }
    }

    protected void enable(){
    }

    protected void disable(){
    }

    public static String getPluginPrefix(){
        return prefix;
    }

    public static String getPluginName() {
        return name;
    }

    public static ServerVersion getServerVersion() {
        return serverVersion;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public boolean isOutdated(){
        return !updateCheckerManager.getLatestVersion().equals(version);
    }
}