package me.dewrs.core.model;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItem {
    private String name;
    private List<String> lore;
    private Material material;
    private int amount;
    private int slot;
    private Map<Enchantment, Integer> enchants;
    private List<ItemFlag> itemFlags;

    public CustomItem(String name, List<String> lore, Material material, int amount) {
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.amount = amount;
        this.enchants = new HashMap<>();
        this.itemFlags = new ArrayList<>();
    }

    public CustomItem(String name, List<String> lore, Material material, int amount, int slot) {
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.amount = amount;
        this.slot = slot;
        this.enchants = new HashMap<>();
        this.itemFlags = new ArrayList<>();
    }

    public CustomItem(String name, List<String> lore, Material material, int amount, int slot, Map<Enchantment, Integer> enchants, List<ItemFlag> itemFlags) {
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.amount = amount;
        this.slot = slot;
        this.enchants = enchants;
        this.itemFlags = itemFlags;
    }

    public CustomItem(String name, List<String> lore, Material material, int amount, Map<Enchantment, Integer> enchants, List<ItemFlag> itemFlags) {
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.amount = amount;
        this.enchants = enchants;
        this.itemFlags = itemFlags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Map<Enchantment, Integer> getEnchants() {
        return enchants;
    }

    public void setEnchants(Map<Enchantment, Integer> enchants) {
        this.enchants = enchants;
    }

    public List<ItemFlag> getItemFlags() {
        return itemFlags;
    }

    public void setItemFlags(List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public CustomItem cloneCustomItem(){
        return new CustomItem(this.getName(), this.getLore(), this.material, this.getAmount(), this.getEnchants(), this.getItemFlags());
    }
}