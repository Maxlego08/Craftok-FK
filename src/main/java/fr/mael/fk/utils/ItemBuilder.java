package fr.mael.fk.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class ItemBuilder {
    private String title;
    private int amount;
    private short damage;
    private Color leatherColor;
    private Material material;
    private List<String> lores = new ArrayList<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>();

    public ItemBuilder(ItemStack item) {
        this(item.getType(), item.getAmount(), item.getDurability());
    }

    public ItemBuilder(Material material) {
        this(material, 1, (short) 0);
    }

    public ItemBuilder(Material material, int amount) {
        this(material, amount, (short) 0);
    }

    public ItemBuilder(Material material, int amount, short damage) {
        this.material = material;
        this.amount = amount;
        this.damage = damage;
    }

    public ItemBuilder(Material material, short durability) {
        this(material, 1, durability);
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder addLores(String... lores) {
        this.lores.addAll(Arrays.asList(lores));
        return this;
    }

    public ItemBuilder setLeatherColor(Color color) {
        leatherColor = color;
        return this;
    }

    public ItemStack build() {
        if (material == null) { throw new NullPointerException("Material cannot be null!"); }
        ItemStack item = new ItemStack(material, amount, damage);
        if (!enchantments.isEmpty()) {
            item.addUnsafeEnchantments(enchantments);
        }
        ItemMeta meta = item.getItemMeta();
        if (title != null) {
            meta.setDisplayName(title);
        }
        if (leatherColor != null && item.getType().name().contains("LEATHER_")) {
            ((LeatherArmorMeta) meta).setColor(leatherColor);
        }
        if (!lores.isEmpty()) {
            meta.setLore(lores);
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemBuilder setTitle(String title) {
        this.title = title;
        return this;
    }
}
