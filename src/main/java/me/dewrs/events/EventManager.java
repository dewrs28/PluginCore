package me.dewrs.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class EventManager {
    private static EventManager instance;
    private final JavaPlugin plugin;
    private EventManager(JavaPlugin plugin){
        this.plugin = plugin;
    }

    private void init(JavaPlugin plugin){
        if(instance != null){
            return;
        }
        instance = new EventManager(plugin);
    }

    private void registerEvents(List<Listener> events){
        for(Listener e : events){
            register(e);
        }
    }

    private void register(Listener event){
        Bukkit.getPluginManager().registerEvents(event, plugin);
    }

    public static EventManager getInstance() {
        return instance;
    }
}