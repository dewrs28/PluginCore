package me.dewrs.commands;

import me.dewrs.PluginCore;
import me.dewrs.logger.LogSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

import java.util.Map;

public class CommandManager {
    private final CommandManager instance;
    private final PluginCore plugin;

    public CommandManager(PluginCore plugin){
        instance = this;
        this.plugin = plugin;
    }

    public void registerCommands(Map<String, CommandExecutor> commands){
        for(Map.Entry<String, CommandExecutor> entry : commands.entrySet()){
            register(entry.getKey(), entry.getValue());
        }
        LogSender.sendMessage("&aCommands registered successfully");
    }

    private void register(String command, CommandExecutor commandExecutor){
        PluginCommand pluginCommand = plugin.getCommand(command);
        if(pluginCommand == null){
            return;
        }
        pluginCommand.setExecutor(commandExecutor);
    }

    public CommandManager getInstance() {
        return instance;
    }
}