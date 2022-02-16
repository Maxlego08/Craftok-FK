package fr.mael.fk.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

@Data
@AllArgsConstructor
public class Item {
    private Material material;
    private int minAmount;
    private int maxAmount;
    private int rarity;
    private boolean spawnMany;

    public ItemStack toItemStack(Random random) {
        return new ItemStack(material, minAmount > maxAmount || minAmount == maxAmount ? 1 : random.nextInt(maxAmount - minAmount) + 1);
    }

    public ItemStack toItemStackWithRarity(Random random) {
        int amount = 0;
        if (rarity >= 5) {
            amount -= minAmount + Math.abs(random.nextInt(rarity) - 11);
            while (amount > maxAmount) {
                amount--;
            }
        } else {
            amount = maxAmount - random.nextInt(rarity);
            while (amount < minAmount) {
                amount++;
            }
        }
        return new ItemStack(material, amount);
    }
}
