package me.dewrs.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class EventManager {
    private final EventManager instance;
    private final JavaPlugin plugin;

    public EventManager(JavaPlugin plugin){
        this.plugin = plugin;
        instance = this;
    }

    public void registerEvents(List<Listener> events){
        for(Listener e : events){
            register(e);
        }
    }

    private void register(Listener event){
        Bukkit.getPluginManager().registerEvents(event, plugin);
    }

    public EventManager getInstance() {
        return instance;
    }
}