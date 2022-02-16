package fr.mael.fk.event.entity;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.handler.Team;
import fr.mael.fk.utils.MathUtils;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDeath extends FKListener {

    public EntityDeath(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            return;
        } else if (event.getEntity() instanceof Creeper && ((Creeper) event.getEntity()).isPowered()) {
            int amount = MathUtils.random(1, 3);
            event.getDrops().add(new ItemStack(Material.TNT, amount));
        } else if (event.getEntity() instanceof Wither) {
            EnderCrystal enderCrystal = (EnderCrystal) event.getEntity();
            for (final Team team : Team.values()) {
                if (team == Team.SPEC || team.getCuboid() == null) {
                    continue;
                } else if (team.isNexus(enderCrystal)) {
                    team.loose(enderCrystal);
                    /*if (enderCrystal.getKiller() != null) {
                        plugin.getData(enderCrystal.getKiller()).addCoins(8, false);
                        enderCrystal.getKiller().sendMessage(ChatColor.GRAY + "Gain de FunCoins + " + ChatColor.GOLD + "8.0" + ChatColor.GRAY + " (" + ChatColor.YELLOW + "Une équipe en moins !" + ChatColor.GRAY + ")");
                    }*/
                    //break;
                }
            }
        }
    }
}
