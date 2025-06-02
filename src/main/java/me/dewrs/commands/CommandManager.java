package me.dewrs.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class CommandManager {
    private static CommandManager instance;
    private final JavaPlugin plugin;

    private CommandManager(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public static void init(JavaPlugin plugin){
        if(instance != null){
            return;
        }
        instance = new CommandManager(plugin);
    }

    public void registerCommands(Map<String, CommandExecutor> commands){
        for(Map.Entry<String, CommandExecutor> entry : commands.entrySet()){
            register(entry.getKey(), entry.getValue());
        }
    }

    private void register(String command, CommandExecutor commandExecutor){
        PluginCommand pluginCommand = plugin.getCommand(command);
        if(pluginCommand == null){
            return;
        }
        pluginCommand.setExecutor(commandExecutor);
    }

    public static CommandManager getInstance() {
        return instance;
    }
}