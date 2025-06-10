package me.dewrs.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    public static String getColoredMessage(String message) {
        if(ServerVersion.isNewServer()) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher match = pattern.matcher(message);
            while(match.find()) {
                String color = message.substring(match.start(),match.end());
                message = message.replace(color, ChatColor.of(color)+"");
                match = pattern.matcher(message);
            }
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }
}
