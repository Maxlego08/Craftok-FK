package fr.mael.fk.event.player;

import fr.mael.fk.handler.Step;

import fr.mael.fk.Main;
import fr.mael.fk.event.FKListener;
import fr.mael.fk.utils.ItemBuilder;
import fr.mael.fk.utils.MathUtils;

import fr.mael.fk.handler.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerInteract extends FKListener {
    public PlayerInteract(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (Step.isStep(Step.IN_GAME)) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Team playerTeam = Team.getPlayerTeam(player);
                if (playerTeam == Team.SPEC) {
                    event.setCancelled(true);
                    return;
                }
                Location loc = event.getClickedBlock().getLocation();
                if (playerTeam.getCuboid().contains(loc)) {
                    if (event.getClickedBlock().getState().getType() == Material.WOODEN_DOOR || event.getClickedBlock().getState().getType() == Material.FENCE_GATE || event.getClickedBlock().getState().getType() == Material.TRAP_DOOR) {
                        final Block door = event.getClickedBlock();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if ((door.getData() & 0x4) == 0) { return; }
                                Block topHalf = door.getRelative(BlockFace.UP);
                                door.setData((byte) (door.getData() ^ 0x4));
                                door.getWorld().playEffect(door.getLocation(), Effect.DOOR_TOGGLE, 0);
                                if (topHalf.getType() == Material.WOODEN_DOOR || topHalf.getType() == Material.FENCE_GATE || topHalf.getType() == Material.TRAP_DOOR) {
                                    topHalf.setData((byte) (topHalf.getData() ^ 0x4));
                                }
                            }
                        }.runTaskLater(plugin, 100);
                    }
                    return;
                }
                Material blockMat = event.getClickedBlock().getType();
                Material mat = event.hasItem() ? event.getItem().getType() : Material.AIR;
                boolean canPlaceOutdoor = mat == Material.TNT && blockMat != Material.WOODEN_DOOR && blockMat != Material.FENCE_GATE && blockMat != Material.WOOD_DOOR && (State.isState(State.ASSAULT) || State.isState(State.DEATHMATCH)) || mat == Material.FLINT_AND_STEEL && blockMat != Material.WOODEN_DOOR && blockMat != Material.FENCE_GATE && blockMat != Material.WOOD_DOOR;
                if (!canPlaceOutdoor) {
                    for (Team team : Team.values()) {
                        if (team == Team.SPEC || team == playerTeam || team.getCuboid() == null) {
                            continue;
                        } else if (team.getCuboid().contains(loc)) {
                            event.setCancelled(true);
                            if (event.hasItem() && event.getItem().getType().isBlock()) {
                                player.damage(0.5D);
                                player.sendMessage(Main.prefix + ChatColor.RED + "Vous ne pouvez pas poser de blocs dans les bases ennemies.");
                            }
                            break;
                        }
                    }
                }
            }
        } else if (Step.isStep(Step.LOBBY)) {
            if (!player.isOp()) {
                event.setCancelled(true);
            }
            if (event.hasItem()) {
                ItemStack item = event.getItem();
                if (item.getType() == Material.WOOD_AXE && player.isOp()) {
                    event.setCancelled(true);
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        player.removeMetadata("pos1", plugin);
                        player.setMetadata("pos1", new FixedMetadataValue(plugin, plugin.toString(event.getClickedBlock().getLocation())));
                        player.sendMessage(ChatColor.GREEN + "Vous venez de séléctionner le point " + ChatColor.AQUA + "1.");
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        player.removeMetadata("pos2", plugin);
                        player.setMetadata("pos2", new FixedMetadataValue(plugin, plugin.toString(event.getClickedBlock().getLocation())));
                        player.sendMessage(ChatColor.GREEN + "Vous venez de séléctionner le point " + ChatColor.AQUA + "2.");
                        player.sendMessage(ChatColor.GRAY + "Tapez " + ChatColor.GOLD + "/fk setcuboid <couleur>" + ChatColor.GRAY + " pour confirmer votre sélection.");
                    }
                } else if (item.getType() == Material.NAME_TAG && item.hasItemMeta()) {
                    PlayerData data = plugin.getData(player);
                    Kit playerKit = Kit.getPlayerKit(player);
                    Inventory inventory = Bukkit.createInventory(player, 9, "Séléction : " + ChatColor.YELLOW + (playerKit == null ? "Aucune" : playerKit.getName()));
                    inventory.addItem(new ItemBuilder(Kit.MINER.getMaterial(), Kit.MINER.getDurability()).setTitle(Kit.MINER.getName()).addLores((data.getMiner() == 0 ? ChatColor.GRAY + "Améliore votre pioche\n\n" + ChatColor.RED + "Achetez le sur le Hub !" : ChatColor.GRAY + "Améliore votre pioche\n\n" + ChatColor.GRAY + "Pioche en " + (data.getMiner() < 3 ? "pierre" : data.getMiner() < 5 ? "fer" : "diamant") + (data.getMiner() == 1 || data.getMiner() == 3 ? " très usée" : data.getMiner() == 2 ? " usée" : "") + (data.getMiner() > 1 && data.getMiner() < 4 ? "\n" + ChatColor.AQUA + (data.getMiner() - 1) + ChatColor.GRAY + " fer" + (data.getMiner() == 2 ? "" : "s") : data.getMiner() > 3 ? "\n" + ChatColor.AQUA + (data.getMiner() - 3) + ChatColor.GRAY + " diamant" + (data.getMiner() == 4 ? "" : "s") : "") + "\n\n" + ChatColor.GOLD + "Niveau actuel : " + ChatColor.YELLOW + data.getMiner()).split("\n")).build());
                    inventory.addItem(new ItemBuilder(Kit.BETTER_BOW.getMaterial(), Kit.BETTER_BOW.getDurability()).setTitle(Kit.BETTER_BOW.getName()).addLores((data.getBetterBow() == 0 ? ChatColor.GRAY + "Améliore votre arc\n\n" + ChatColor.RED + "Achetez le sur le Hub !" : ChatColor.GRAY + "Améliore votre arc\n\n" + ChatColor.GRAY + "Infinity I\n" + ChatColor.GRAY + "Punch " + (data.getBetterBow() < 3 ? "I" : "II") + (data.getBetterBow() > 1 ? "\n" + ChatColor.GRAY + "Power " + (data.getBetterBow() < 4 ? "I" : data.getBetterBow() == 4 ? "II" : "II") : "") + "\n\n" + ChatColor.GOLD + "Niveau actuel : " + ChatColor.YELLOW + data.getBetterBow()).split("\n")).build());
                    inventory.addItem(new ItemBuilder(Kit.BETTER_SWORD.getMaterial(), Kit.BETTER_SWORD.getDurability()).setTitle(Kit.BETTER_SWORD.getName()).addLores((data.getBetterSword() == 0 ? ChatColor.GRAY + "Améliore votre épée\n\n" + ChatColor.RED + "Achetez le sur le Hub !" : ChatColor.GRAY + "Améliore votre épée\n\n" + ChatColor.GRAY + "Epée en " + (data.getBetterSword() > 3 ? "diamant" : "fer") + (data.getBetterSword() > 1 && data.getBetterSword() != 4 ? "\n" + ChatColor.GRAY + "Tranchant " + (data.getBetterSword() == 3 ? "II" : "I") : "") + "\n\n" + ChatColor.GOLD + "Niveau actuel : " + ChatColor.YELLOW + data.getBetterSword()).split("\n")).build());
                    inventory.addItem(new ItemBuilder(Kit.BETTER_ARMOR.getMaterial(), Kit.BETTER_ARMOR.getDurability()).setTitle(Kit.BETTER_ARMOR.getName()).addLores((data.getBetterArmor() == 0 ? ChatColor.GRAY + "Améliore votre armure\n\n" + ChatColor.RED + "Achetez le sur le Hub !" : ChatColor.GRAY + "Améliore votre armure\n\n" + ChatColor.GRAY + "1 casque en " + (data.getBetterArmor() < 5 ? "fer" + (data.getBetterArmor() > 1 ? "P" + (data.getBetterArmor() < 4 ? data.getBetterArmor() - 1 : 3) : "") : "diamant P1") + "\n" + ChatColor.GRAY + "1 plastron en fer" + (data.getBetterArmor() > 1 ? "P" + (data.getBetterArmor() < 4 ? data.getBetterArmor() - 1 : 3) : "") + "\n" + ChatColor.GRAY + "1 pantalon en fer" + (data.getBetterArmor() > 1 ? "P" + (data.getBetterArmor() < 4 ? data.getBetterArmor() - 1 : 3) : "") + "\n" + ChatColor.GRAY + "2 bottes en " + (data.getBetterArmor() < 5 ? "fer" + (data.getBetterArmor() > 1 ? "P" + (data.getBetterArmor() < 4 ? data.getBetterArmor() - 1 : 3) : "") : "diamant P1") + "\n\n" + ChatColor.GOLD + "Niveau actuel : " + ChatColor.YELLOW + data.getBetterArmor()).split("\n")).build());
                    inventory.addItem(new ItemBuilder(Kit.MERLIN.getMaterial(), Kit.MERLIN.getDurability()).setTitle(Kit.MERLIN.getName()).addLores((data.getMerlin() == 0 ? ChatColor.GRAY + "Améliore votre magie\n\n" + ChatColor.RED + "Achetez le sur le Hub !" : ChatColor.GRAY + "Améliore votre magie\n\n" + ChatColor.AQUA + (data.getMerlin() == 1 ? "8" : data.getMerlin() == 2 ? "16" : data.getMerlin() == 3 ? "32" : data.getMerlin() == 4 ? "64" : "128") + ChatColor.GRAY + " bouteilles d'xp\n" + ChatColor.AQUA + (data.getMerlin() < 4 ? "1" : "2") + ChatColor.GRAY + " livre" + (data.getMerlin() < 4 ? "" : "s") + " T" + (data.getMerlin() < 5 ? "1" : "2") + (data.getMerlin() > 1 ? "\n" + ChatColor.AQUA + (data.getMerlin() < 4 ? "1" : "2") + ChatColor.GRAY + " livre" + (data.getMerlin() < 4 ? "" : "s") + " P" + (data.getMerlin() < 4 ? "1" : "2") + "\n" : "") + (data.getMerlin() > 2 ? "\n" + ChatColor.AQUA + (data.getMerlin() < 4 ? "1" : "2") + ChatColor.GRAY + " livre" + (data.getMerlin() < 4 ? "" : "s") + " épineux " + (data.getMerlin() < 5 ? "I" : "II") : "") + (data.getMerlin() > 3 ? "\n" + ChatColor.AQUA + "1" + ChatColor.GRAY + " livre Recul II" : "") + "\n" + ChatColor.GRAY + "Enclume" + (data.getMerlin() < 4 ? " très usée" : data.getMerlin() < 5 ? " usée" : "") + "\n\n" + ChatColor.GOLD + "Niveau actuel : " + ChatColor.YELLOW + data.getMerlin()).split("\n")).build());
                    player.openInventory(inventory);
                } else if (item.getType() == Material.BANNER && item.hasItemMeta()) {
                    for (Team team : Team.values()) {
                        if (item.isSimilar(team.getIcon())) {
                            String displayName = team.getDisplayName();
                            Team playerTeam = Team.getPlayerTeam(player);
                            if (playerTeam != team) {
                                if (Bukkit.getOnlinePlayers().size() > 2 && team.getOnlinePlayers().size() >= 2 && team.getOnlinePlayers().size() >= MathUtils.ceil(Bukkit.getOnlinePlayers().size() / (Team.values().length - 2))) {
                                    player.sendMessage(Main.prefix + ChatColor.GRAY + "Impossible de rejoindre cette équipe, trop de joueurs !");
                                } else {
                                    if (playerTeam != null) {
                                        playerTeam.removePlayer(player);
                                    }
                                    team.addPlayer(player);
                                    player.sendMessage(Main.prefix + ChatColor.GRAY + "Vous rejoignez l'équipe " + team.getColor() + displayName);
                                }
                            }
                            break;
                        }
                    }
                    player.updateInventory();
                }
            }
        }
    }
}
