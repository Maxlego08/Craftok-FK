package fr.mael.fk.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerData {
    private UUID uuid;
    private String name;
    private int miner;
    private int betterBow;
    private int betterSword;
    private int betterArmor;
    private int merlin;
    private double coins;

    public void addCoins(double coins) {
        this.addCoins(coins, true);
    }

    public void addCoins(double coins, boolean msg) {
        Player player = Bukkit.getPlayer(name);
        if (player != null && player.isOnline()) {
            int booster = player.hasPermission("boost.200") ? 200 : player.hasPermission("boost.175") ? 175 : player.hasPermission("boost.150") ? 150 : player.hasPermission("boost.125") ? 125 : player.hasPermission("boost.100") ? 100 : player.hasPermission("boost.75") ? 75 : player.hasPermission("boost.50") ? 50 : player.hasPermission("boost.25") ? 25 : 0;
            coins += coins / 100 * booster;
            this.coins += coins;
            if (msg) {
                Bukkit.getPlayer(name).sendMessage(ChatColor.GRAY + "Gain de FunCoins + " + ChatColor.GOLD + String.valueOf(coins).replace(".", ",") + (booster > 1 ? ChatColor.YELLOW + " (" + ChatColor.LIGHT_PURPLE + booster + "%" + ChatColor.YELLOW + ")" : ""));
            }
        }
    }
}
