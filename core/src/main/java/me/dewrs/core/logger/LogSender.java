package me.dewrs.core.logger;

import me.dewrs.core.PluginCore;
import me.dewrs.core.utils.MessageUtils;
import org.bukkit.Bukkit;

public class LogSender {

    public static void sendMessage(String message){
        Bukkit.getConsoleSender().sendMessage(PluginCore.getPluginPrefix()+MessageUtils.getColoredMessage(message));
    }

    public static void sendWarnMessage(String message){
        Bukkit.getLogger().warning("["+PluginCore.getPluginName()+"] "+message);
    }

    public static void sendErrorMessage(String message){
        Bukkit.getLogger().severe("["+PluginCore.getPluginName()+"] "+message);
    }

    public static void sendUpdateMessage(String pluginName, String actualVersion, String latestVersion, int spigotId){
        LogSender.sendMessage("*********************************************************************");
        LogSender.sendMessage("&c"+pluginName+" is outdated!");
        LogSender.sendMessage("&cNewest version: &e"+latestVersion);
        LogSender.sendMessage("&cYour version: &e"+actualVersion);
        LogSender.sendMessage("&cPlease Update Here: &ehttps://spigotmc.org/resources/"+spigotId);
        LogSender.sendMessage("*********************************************************************");
    }
}