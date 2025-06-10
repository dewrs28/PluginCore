package me.dewrs.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.Map;
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

    public static String getMessageReplaceVariables(String message, Map<String, String> variables){
        String finalMessage = message;
        for(Map.Entry<String, String> entry : variables.entrySet()){
            finalMessage = message.replaceAll(entry.getKey(), entry.getValue());
        }
        return finalMessage;
    }

    public static String getStringFromList(List<String> strings){
        return String.join(", ", strings);
    }
}