package me.dewrs.utils;

import me.dewrs.logger.LogSender;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SoundUtils {
    public static void playSound(Player player, String soundName, int volume, float pitch){
        Sound sound = null;
        try {
            sound = getSoundByName(soundName);
        }catch(Exception e ) {
            LogSender.sendErrorMessage("The sound '"+soundName+"' is not valid.");
            return;
        }
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    private static Sound getSoundByName(String name){
        try {
            Class<?> soundTypeClass = Class.forName("org.bukkit.Sound");
            Method valueOf = soundTypeClass.getMethod("valueOf", String.class);
            return (Sound) valueOf.invoke(null,name);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
