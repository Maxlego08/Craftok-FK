package fr.mael.fk.event.inventory;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.handler.Kit;
import fr.mael.fk.handler.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClick extends FKListener {
    public InventoryClick(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        if (Step.isStep(Step.LOBBY)) {
            event.setCancelled(true);
            if (event.getInventory().getTitle().contains("Séléction :") && event.getSlot() == event.getRawSlot() && current != null && current.hasItemMeta()) {
                Player player = (Player) event.getWhoClicked();
                PlayerData data = plugin.getData(player);
                String display = current.getItemMeta().getDisplayName();
                Kit clickedKit = Kit.MINER.getName().equals(display) ? Kit.MINER : Kit.BETTER_BOW.getName().equals(display) ? Kit.BETTER_BOW : Kit.BETTER_SWORD.getName().equals(display) ? Kit.BETTER_SWORD : Kit.BETTER_ARMOR.getName().equals(display) ? Kit.BETTER_ARMOR : Kit.MERLIN.getName().equals(display) ? Kit.MERLIN : null;
                if (clickedKit == null) { return; }
                if (clickedKit == Kit.MINER && data.getMiner() <= 0 || clickedKit == Kit.BETTER_BOW && data.getBetterBow() <= 0 || clickedKit == Kit.BETTER_SWORD && data.getBetterSword() <= 0 || clickedKit == Kit.BETTER_ARMOR && data.getBetterArmor() <= 0 || clickedKit == Kit.MERLIN && data.getMerlin() <= 0) {
                    player.sendMessage(Main.prefix + ChatColor.GRAY + "Vous ne possédez pas ce kit !");
                } else {
                    Kit.setPlayerKit(player, clickedKit);
                    player.sendMessage(Main.prefix + ChatColor.GRAY + "Kit séléctionné : " + ChatColor.GOLD + clickedKit.getName());
                    player.closeInventory();
                }
            }
        }
    }
}
