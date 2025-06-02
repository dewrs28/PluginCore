package me.dewrs;

import me.dewrs.commands.CommandManager;
import me.dewrs.events.EventManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginCore extends JavaPlugin {
    protected CommandManager commandManager;
    protected EventManager eventManager;

    @Override
    public final void onEnable(){
        commandManager = new CommandManager(this);
        eventManager = new EventManager(this);
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
}
