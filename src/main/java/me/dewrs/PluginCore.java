package me.dewrs;

import me.dewrs.commands.CommandManager;
import me.dewrs.events.EventManager;
import me.dewrs.logger.LogSender;
import me.dewrs.updatechecker.UpdateCheckerManager;
import me.dewrs.utils.ServerVersion;
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
    private static ServerVersion serverVersion;

    @Override
    public final void onEnable(){
        setVersion();
        name = pluginDescriptionFile.getName();

        commandManager = new CommandManager(this);
        eventManager = new EventManager(this);

        updateCheckerManager = new UpdateCheckerManager(version, name);
        updateCheckerManager.manageUpdateChecker(spigotId);

        enable();
        LogSender.sendMessage("&ahas been enabled!");
    }

    @Override
    public final void onDisable(){
        disable();
        LogSender.sendMessage("&ahas been disabled!");
    }

    protected void enable(){

    }

    protected void disable(){

    }

    public void setVersion(){
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

    public static String getPluginPrefix(){
        return prefix;
    }

    public static String getPluginName() {
        return name;
    }

    public static ServerVersion getServerVersion() {
        return serverVersion;
    }

    public boolean isOutdated(){
        return !updateCheckerManager.getLatestVersion().equals(version);
    }
}