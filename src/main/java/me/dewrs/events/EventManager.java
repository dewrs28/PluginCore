package me.dewrs.events;

import me.dewrs.PluginCore;
import me.dewrs.logger.LogSender;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventManager {
    private final EventManager instance;
    private final PluginCore plugin;

    public EventManager(PluginCore plugin){
        this.plugin = plugin;
        instance = this;
    }

    public void registerEvents(List<Listener> events){
        for(Listener e : events){
            register(e);
        }
        LogSender.sendMessage("&aEvents registered successfully");
    }

    private void register(Listener event){
        Bukkit.getPluginManager().registerEvents(event, plugin);
    }

    public EventManager getInstance() {
        return instance;
    }
}