package fr.mael.fk.timer;

import fr.mael.fk.handler.Step;
import fr.mael.fk.Main;
import fr.mael.fk.handler.Kit;
import fr.mael.fk.handler.PlayerData;
import fr.mael.fk.handler.Team;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class TaskStart extends BukkitRunnable {
    public static boolean started = false;
    public static int timeUntilStart = 60;

    private Main plugin;

    public TaskStart(Main plugin) {
        this.plugin = plugin;
        TaskStart.started = true;
        this.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void run() {
        if (TaskStart.timeUntilStart > 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setExp(TaskStart.timeUntilStart / 60.0F);
                player.setLevel(TaskStart.timeUntilStart);
            }
        } else {
            this.cancel(); // TODO
            if (Bukkit.getOnlinePlayers().size() <= 7) {
                Bukkit.broadcastMessage(Main.prefix + ChatColor.RED + "Il n'y a pas assez de joueurs !");
                TaskStart.timeUntilStart = 60;
                TaskStart.started = false;
            } else {
                Step.setCurrentStep(Step.IN_GAME);
                Bukkit.broadcastMessage(Main.prefix + ChatColor.AQUA + "La partie vient de commencer, bon jeu !");

                //Scoreboard
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                Objective health = scoreboard.getObjective("health");
                if (health != null) {
                    health.unregister();
                }
                health = scoreboard.registerNewObjective("health", "health");
                health.setDisplayName(ChatColor.RED + "♥");
                health.setDisplaySlot(DisplaySlot.BELOW_NAME);


                for (Player player : Bukkit.getOnlinePlayers()) {
                    Team team = Team.getPlayerTeam(player);
                    if (team == Team.SPEC) {
                        team = Team.getRandomTeam();
                        team.addPlayer(player);
                    }



                    TaskStart.resetPlayer(player);
                    player.setExp(0);
                    player.setLevel(0);
                    PlayerData data = plugin.getData(player);
                    Kit kit = Kit.getPlayerKit(player);
                    if (kit == Kit.MINER) {
                        ItemStack pickaxe = new ItemStack(data.getMiner() < 3 ? Material.STONE_PICKAXE : data.getMiner() < 5 ? Material.IRON_PICKAXE : Material.DIAMOND_PICKAXE);
                        if (data.getMiner() < 4) {
                            pickaxe.setDurability(data.getMiner() == 2 ? (short) 50 : (short) 3);
                        }
                        player.getInventory().addItem(pickaxe);
                        if (data.getMiner() == 2 || data.getMiner() == 3) {
                            ItemStack iron = new ItemStack(Material.IRON_INGOT, data.getMiner() - 1);
                            player.getInventory().addItem(iron);
                        } else if (data.getMiner() == 4 || data.getMiner() == 5) {
                            ItemStack diamond = new ItemStack(Material.DIAMOND, data.getMerlin() - 3);
                            player.getInventory().addItem(diamond);
                        }
                    } else if (kit == Kit.BETTER_BOW) {
                        ItemStack bow = new ItemStack(Material.BOW);
                        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                        bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, data.getBetterBow() < 3 ? 1 : 2);
                        if (data.getBetterBow() > 1) {
                            bow.addEnchantment(Enchantment.ARROW_DAMAGE, data.getBetterBow() < 4 ? 1 : data.getBetterBow() == 4 ? 2 : 3);
                        }
                        player.getInventory().addItem(bow);
                        player.getInventory().setItem(9, new ItemStack(Material.ARROW));
                    } else if (kit == Kit.BETTER_SWORD) {
                        ItemStack sword = new ItemStack(data.getBetterSword() < 4 ? Material.IRON_SWORD : Material.DIAMOND_SWORD);
                        if (data.getBetterSword() > 1 && data.getBetterSword() != 4) {
                            sword.addEnchantment(Enchantment.DAMAGE_ALL, data.getBetterSword() == 2 || data.getBetterSword() == 5 ? 1 : 2);
                        }
                        player.getInventory().addItem(sword);
                    } else if (kit == Kit.BETTER_ARMOR) {
                        ItemStack helmet = new ItemStack(data.getBetterArmor() == 5 ? Material.DIAMOND_HELMET : Material.IRON_HELMET);
                        ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
                        ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
                        ItemStack boots = new ItemStack(data.getBetterArmor() == 5 ? Material.DIAMOND_BOOTS : Material.IRON_BOOTS);
                        if (data.getBetterArmor() > 1) {
                            helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, data.getBetterArmor() < 5 ? data.getBetterArmor() - 1 : 1);
                            chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, data.getBetterArmor() < 4 ? data.getBetterArmor() - 1 : 3);
                            leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, data.getBetterArmor() < 4 ? data.getBetterArmor() - 1 : 3);
                            boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, data.getBetterArmor() < 5 ? data.getBetterArmor() - 1 : 1);
                        }
                        player.getInventory().setHelmet(helmet);
                        player.getInventory().setChestplate(chestplate);
                        player.getInventory().setLeggings(leggings);
                        player.getInventory().setBoots(boots);
                    } else if (kit == Kit.MERLIN) {
                        ItemStack bottles = new ItemStack(Material.EXP_BOTTLE, data.getMerlin() == 1 ? 8 : data.getMerlin() == 2 ? 16 : data.getMerlin() == 3 ? 32 : 64);
                        ItemStack sharpness = new ItemStack(Material.ENCHANTED_BOOK, data.getMerlin() < 4 ? 1 : 2);
                        this.enchantBook(sharpness, Enchantment.DAMAGE_ALL, data.getMerlin() < 5 ? 1 : 2);
                        player.getInventory().addItem(bottles);
                        if (data.getMerlin() >= 5) {
                            player.getInventory().addItem(bottles);
                        }
                        player.getInventory().addItem(sharpness);
                        if (data.getMerlin() > 1) {
                            ItemStack anvil = new ItemStack(Material.ANVIL, 1, data.getMerlin() < 4 ? (short) 2 : data.getMerlin() == 4 ? (short) 1 : (short) 0);
                            ItemStack protection = new ItemStack(Material.ENCHANTED_BOOK, data.getMerlin() == 2 ? 1 : data.getMerlin() - 2);
                            this.enchantBook(protection, Enchantment.PROTECTION_ENVIRONMENTAL, data.getMerlin() < 1 ? 1 : 2);
                            player.getInventory().addItem(protection);
                            if (data.getMerlin() > 2) {
                                ItemStack thorns = new ItemStack(Material.ENCHANTED_BOOK, data.getMerlin() < 4 ? 1 : 2);
                                this.enchantBook(thorns, Enchantment.THORNS, data.getMerlin() < 5 ? 1 : 2);
                                player.getInventory().addItem(thorns);
                                if (data.getMerlin() > 3) {
                                    ItemStack knockback = new ItemStack(Material.ENCHANTED_BOOK);
                                    this.enchantBook(knockback, Enchantment.KNOCKBACK, 2);
                                    player.getInventory().addItem(knockback);
                                }
                            }
                            player.getInventory().addItem(anvil);
                        }
                    } else {
                        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                        LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
                        meta.setColor(team.getColor() == ChatColor.BLUE ? Color.BLUE : team.getColor() == ChatColor.RED ? Color.RED : team.getColor() == ChatColor.GREEN ? Color.GREEN : Color.YELLOW);
                        chestplate.setItemMeta(meta);
                        player.getInventory().setChestplate(chestplate);
                    }
                    health.getScore(player).setScore(20);
                    player.teleport(team.getSpawnLocation());
                }

                Objective kills = scoreboard.getObjective("kills");
                if (kills != null) {
                    kills.unregister();
                }
                kills = scoreboard.registerNewObjective("kills", "dummy");
                kills.setDisplaySlot(DisplaySlot.PLAYER_LIST);

                new TaskGame(plugin);
                new TaskTime(plugin);
            }
            return;
        }
        int remainingMins = TaskStart.timeUntilStart / 60 % 60;
        int remainingSecs = TaskStart.timeUntilStart % 60;
        if (TaskStart.timeUntilStart % 30 == 0 || remainingMins == 0 && (remainingSecs % 10 == 0 || remainingSecs <= 5)) {
            Bukkit.broadcastMessage(Main.prefix + ChatColor.GOLD + "Démarrage du jeu dans " + ChatColor.YELLOW + (remainingMins > 0 ? remainingMins + " minute" + (remainingMins > 1 ? "s" : "") : "") + (remainingSecs > 0 ? (remainingMins > 0 ? " " : "") + remainingSecs + " seconde" + (remainingSecs > 1 ? "s" : "") : "") + ".");

            if (remainingMins == 0 && remainingSecs <= 10) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), remainingSecs == 1 ? Sound.ANVIL_LAND : Sound.CLICK, 1f, 1f);
                }
            }
        }
        TaskStart.timeUntilStart--;
    }

    private ItemStack enchantBook(ItemStack item, Enchantment enchant, int level) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        meta.addStoredEnchant(enchant, level, false);
        item.setItemMeta(meta);
        return item;
    }

    public static void resetPlayer(Player player) {
        player.setFireTicks(0);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExhaustion(5.0F);
        player.setFallDistance(0);
        player.setExp(0.0F);
        player.setLevel(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.closeInventory();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    private Location toLocation(String string) {
        String[] splitted = string.split("_");
        World world = Bukkit.getWorld(splitted[0]);
        if (world == null || splitted.length < 6) {
            world = Main.getInstance().world;
        }
        return new Location(world, Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Double.parseDouble(splitted[3]), Float.parseFloat(splitted[4]), Float.parseFloat(splitted[5]));
    }

}
