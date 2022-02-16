package fr.mael.fk.event.entity;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.Nexus;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.handler.State;
import fr.mael.fk.handler.Team;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class EntityDamageByPlayer extends FKListener {

    public EntityDamageByPlayer(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event) {
        if(Step.isStep(Step.IN_GAME) && State.isState(State.ASSAULT) || Step.isStep(Step.IN_GAME) && State.isState(State.DEATHMATCH)) {
            if(event.getDamager() instanceof TNTPrimed && event.getEntity() instanceof EnderCrystal || event.getDamager() instanceof Creeper && event.getEntity() instanceof EnderCrystal) {
                event.setCancelled(true);

                EnderCrystal endercrystal = (EnderCrystal) event.getEntity();
                Nexus nexus = Team.getNexusFromLocation(endercrystal);
                if(nexus == null) return;
                nexus.damage((int) event.getDamage(), endercrystal, true);
            }
        } else {
            if(event.getDamager() instanceof TNTPrimed && event.getEntity() instanceof EnderCrystal || event.getDamager() instanceof Creeper && event.getEntity() instanceof EnderCrystal) {
               event.setCancelled(true);
            }
        }

        if (event.getDamager() instanceof Skeleton || event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Skeleton) {

            if(event.getEntity() instanceof  EnderCrystal){
                event.setCancelled(true);
            }

        }

        if (event.getDamager() instanceof Player || event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            final Player player = event.getDamager() instanceof Player ? (Player) event.getDamager() : (Player) ((Projectile) event.getDamager()).getShooter();
            if (!Step.isStep(Step.IN_GAME) || Team.getPlayerTeam(player) == Team.SPEC) {
                event.setCancelled(true);
            } else if (Step.isStep(Step.IN_GAME) && event.getEntity() instanceof EnderCrystal) {
                Team playerTeam = Team.getPlayerTeam(player);
                EnderCrystal endercrystal = (EnderCrystal) event.getEntity();
                if (playerTeam.isNexus(endercrystal) || !State.isState(State.ASSAULT) && !State.isState(State.DEATHMATCH)) {
                    event.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                    player.setVelocity(player.getLocation().getDirection().multiply(-1));
                    if (!playerTeam.isNexus(endercrystal)) {
                        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                        player.sendMessage(Main.prefix + ChatColor.RED + "Vous devez attendre l'assaut pour attaquer le nexus ennemis.");
                    }
                } else {
                    event.setCancelled(true);
                    Nexus nexus = Team.getNexusFromLocation(endercrystal);
                    if(nexus == null) return;
                    player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1.0F, 0.5F);
                    nexus.damage((int) event.getDamage(), endercrystal, true);
                }
            }
        }
    }
}
