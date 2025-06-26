package me.dewrs.core.utils;

import me.dewrs.core.model.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemUtils {

    public static ItemStack getItemStackFromCustomItem(CustomItem customItem) {
        ItemStack itemStack = new ItemStack(customItem.getMaterial());
        itemStack.setAmount(customItem.getAmount());

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return itemStack;
        }

        itemMeta.setDisplayName(MessageUtils.getColoredMessage(customItem.getName()));
        List<String> lore = new ArrayList<>();
        for (String s : customItem.getLore()) {
            lore.add(MessageUtils.getColoredMessage(s));
        }
        itemMeta.setLore(lore);

        Map<Enchantment, Integer> enchants = customItem.getEnchants();
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        List<ItemFlag> itemFlags = customItem.getItemFlags();
        for (ItemFlag flag : itemFlags) {
            itemMeta.addItemFlags(flag);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack getItemStackReplaceVariables(ItemStack itemStack, Map<String, String> variables) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return itemStack;
        }

        String name = itemMeta.getDisplayName();
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            name = name.replace(entry.getKey(), entry.getValue());
        }
        itemMeta.setDisplayName(MessageUtils.getColoredMessage(name));

        List<String> lore = itemMeta.getLore();
        if (lore != null) {
            List<String> loreReplace = lore.stream().map(line -> {
                for (Map.Entry<String, String> entry : variables.entrySet()) {
                    line = line.replace(entry.getKey(), entry.getValue());
                }
                return MessageUtils.getColoredMessage(line);
            }).collect(Collectors.toList());
            itemMeta.setLore(loreReplace);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static CustomItem getCustomItemFromConfigPath(String path, FileConfiguration config){
        if(!config.contains(path+".material")) {
            return null;
        }
        int slot = config.contains(path+".slot") ? config.getInt(path+".slot") : 0;
        Material material = Material.valueOf(config.getString(path+".material"));
        String name = config.contains(path+".name") ? config.getString(path+".name") : "";
        List<String> lore = config.contains(path+".lore") ? config.getStringList(path+".lore") : new ArrayList<>();

        Map<Enchantment, Integer> enchants = new HashMap<>();
        if(config.contains(path+".enchants")) {
            for(String e : config.getStringList(path+".enchants")){
                String[] enchantSplit = e.split(":");
                String enchantString = enchantSplit[0];
                String levelString = enchantSplit[1];
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantString));
                if(enchantment == null){
                    continue;
                }
                int level;
                try{
                    level = Integer.parseInt(levelString);
                }catch (NumberFormatException ex){
                    continue;
                }
                enchants.put(enchantment, level);
            }
        }

        List<ItemFlag> itemFlags = new ArrayList<>();
        if(config.contains(path+".item_flags")) {
            for (String s : config.getStringList(path + ".item_flags")) {
                ItemFlag itemFlag;
                try{
                   itemFlag = ItemFlag.valueOf(s);
                }catch (IllegalArgumentException ex){
                    continue;
                }
                itemFlags.add(itemFlag);
            }
        }

        return new CustomItem(name, lore, material, 1, slot, enchants, itemFlags);
    }
}