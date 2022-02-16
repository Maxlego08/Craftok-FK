package fr.mael.fk;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import fr.mael.fk.event.FKListener;
import fr.mael.fk.event.block.BlockBreak;
import fr.mael.fk.event.block.BlockFade;
import fr.mael.fk.event.block.BlockPlace;
import fr.mael.fk.event.inventory.InventoryClick;
import fr.mael.fk.event.server.ServerCommand;
import fr.mael.fk.event.server.ServerListPing;
import fr.mael.fk.event.weather.ThunderChange;
import fr.mael.fk.event.weather.WeatherChange;
import fr.mael.fk.utils.Cuboid;
import fr.mael.fk.utils.FileUtils;
import fr.mael.fk.utils.ReflectionHandler;
import fr.mael.fk.event.player.*;
import fr.mael.fk.handler.*;
import lombok.SneakyThrows;
import fr.mael.fk.event.entity.CreatureSpawn;
import fr.mael.fk.event.entity.EntityDamage;
import fr.mael.fk.event.entity.EntityDamageByPlayer;
import fr.mael.fk.event.entity.EntityDeath;
import fr.mael.fk.event.entity.EntityExplode;
import fr.mael.fk.event.entity.EntityTarget;
import fr.mael.fk.event.entity.FoodLevelChange;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Main extends JavaPlugin {

    public static Main instance;
    public static String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "FK" + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " ";

    private static Main ins;

    private ScheduledExecutorService executorMonoThread;
    private ScheduledExecutorService scheduledExecutorService;

    public World world;
    public Location lobbyLocation;
    private Map<UUID, PlayerData> data = new HashMap<>();

    @SneakyThrows
    @Override
    public void onLoad() {
        Bukkit.unloadWorld("world", false);
        File worldContainer = this.getServer().getWorldContainer();
        File worldFolder = new File(worldContainer, "world");
        File copyFolder = new File(worldContainer, "fk");
        if (copyFolder.exists()) {
            ReflectionHandler.getClass("RegionFileCache", ReflectionHandler.PackageType.MINECRAFT_SERVER).getMethod("a").invoke(null);
            FileUtils.delete(worldFolder);
            FileUtils.copyFolder(copyFolder, worldFolder);
        }
    }

    @Override
    public void onEnable() {
        Main.instance = this;

        ins = this;
        scheduledExecutorService = Executors.newScheduledThreadPool(16);
        executorMonoThread = Executors.newScheduledThreadPool(1);

        Step.setCurrentStep(Step.LOBBY);
        State.setState(State.NONE);

        ConfigurationSerialization.registerClass(Cuboid.class);
        world = Bukkit.getWorlds().get(0);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setTime(6000);
        this.load();

        this.register(BlockBreak.class, BlockFade.class, BlockPlace.class, CreatureSpawn.class, EntityDamage.class, EntityDamageByPlayer.class, EntityDeath.class, EntityExplode.class, EntityTarget.class, FoodLevelChange.class, InventoryClick.class, AsyncPlayerChat.class, PlayerAchievementAwarded.class, PlayerCommandPreprocess.class, PlayerDamage.class, PlayerDamageByPlayer.class, PlayerDeath.class, PlayerDropItem.class, PlayerInteract.class, PlayerKick.class, PlayerLogin.class, PlayerMove.class, PlayerPickupItem.class, PlayerQuit.class, PlayerRespawn.class, ServerCommand.class, ServerListPing.class, ThunderChange.class, WeatherChange.class, PlayerQuit.class, PlayerJoin.class);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @SneakyThrows
    private void register(Class<? extends FKListener>... classes) {
        for (Class<? extends FKListener> clazz : classes) {
            Constructor<? extends FKListener> constructor = clazz.getConstructor(Main.class);
            Bukkit.getPluginManager().registerEvents(constructor.newInstance(this), this);
        }
    }

    @Override
    public void onDisable() {
        this.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Vous devez être un joueur.");
            return true;
        }
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("fk")) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.YELLOW + "Plugin Fallen Kingdoms v1.0.");
            } else {
                String sub = args[0];
                if (sub.equalsIgnoreCase("help")) {
                    player.sendMessage(ChatColor.GOLD + "Aide du plugin Fallen Kingdoms :");
                    player.sendMessage("/fk setlobby" + ChatColor.YELLOW + " - définit le lobby du jeu");
                    player.sendMessage("/fk setspawn <couleur>" + ChatColor.YELLOW + " - définit le spawn de l'équipe <couleur>");
                    player.sendMessage("/fk setcuboid <couleur>" + ChatColor.YELLOW + " - définit la base de l'équipe <couleur>");
                    player.sendMessage("/fk setwither <couleur>" + ChatColor.YELLOW + " - définit l'emplacement du wither de l'équipe <couleur>");
                } else if (sub.equalsIgnoreCase("setlobby")) {
                    lobbyLocation = player.getLocation();
                    player.sendMessage(ChatColor.GREEN + "Vous avez défini le lobby avec succés.");
                    this.getConfig().set("lobby", this.toString(player.getLocation()));
                    this.saveConfig();
                } else if (sub.equalsIgnoreCase("setspawn")) {
                    if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("green") && !args[1].equalsIgnoreCase("yellow") && !args[1].equalsIgnoreCase("spec")) {
                        player.sendMessage(ChatColor.RED + "L'équipe " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " n'existe pas.");
                    } else {
                        Location location = player.getLocation();
                        Team team = Team.getTeam(args[1]);
                        player.sendMessage(ChatColor.GREEN + "Vous avez défini avec succés le spawn de l'équipe " + team.getColor() + team.getDisplayName());
                        team.setSpawnLocation(location);
                        this.getConfig().set("teams." + args[1] + ".spawn", this.toString(location));
                        this.saveConfig();
                    }
                } else if (sub.equalsIgnoreCase("setnexus")) {
                    if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("green") && !args[1].equalsIgnoreCase("yellow")) {
                        player.sendMessage(ChatColor.RED + "L'équipe " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " n'existe pas.");
                    } else {
                        Location location = player.getLocation();

                        location.setPitch(0);
                        location.setYaw(0);

                        Team team = Team.getTeam(args[1]);
                        player.sendMessage(ChatColor.GREEN + "Vous avez défini l'emplacement du nexus de l'équipe " + team.getColor() + team.getDisplayName());
                        team.setNexusLocation(location);
                        this.getConfig().set("teams." + args[1] + ".nexus", this.toString(location));
                        this.saveConfig();
                    }
                } else if (sub.equalsIgnoreCase("setcuboid")) {
                    if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue") && !args[1].equalsIgnoreCase("green") && !args[1].equalsIgnoreCase("yellow")) {
                        player.sendMessage(ChatColor.RED + "L'équipe " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " n'existe pas.");
                    } else {
                        Team team = Team.getTeam(args[1]);
                        Cuboid cuboid = new Cuboid(this.toLocation(player.getMetadata("pos1").get(0).asString()), this.toLocation(player.getMetadata("pos2").get(0).asString()));
                        player.sendMessage(ChatColor.GREEN + "Vous avez défini avec succés la base de l'équipe " + team.getColor() + team.getDisplayName());
                        team.setCuboid(cuboid);
                        this.getConfig().set("teams." + args[1] + ".cuboid", cuboid);
                        this.saveConfig();
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Mauvais arguments ou commande inexistante. Tapez " + ChatColor.DARK_RED + "/fk help" + ChatColor.RED + " pour de l'aide.");
                }
                return true;
            }
        }
        return false;
    }

    private void load() {
        this.saveDefaultConfig();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team team : Team.values()) {
            team.createTeam(scoreboard);
        }
        ConfigurationSection teams = this.getConfig().getConfigurationSection("teams");
        if (teams != null) {
            Objective objective = scoreboard.getObjective("teams");
            if (objective == null) {
                objective = scoreboard.registerNewObjective("teams", "dummy");
            }
            objective.setDisplayName(ChatColor.DARK_GRAY + "[-" + ChatColor.YELLOW + "Fallen Kingdoms" + ChatColor.DARK_GRAY + "-]");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            for (String key : teams.getKeys(false)) {
                Team team = Team.getTeam(key);
                ConfigurationSection section = teams.getConfigurationSection(key);
                if (section.isString("spawn")) {
                    Location spawnLoc = this.toLocation(section.getString("spawn"));
                    team.setSpawnLocation(spawnLoc);
                    spawnLoc.getChunk().load(true);
                }
                if (section.isString("nexus")) {
                    Location nexusLoc = this.toLocation(section.getString("nexus"));
                    team.setNexusLocation(nexusLoc);
                    nexusLoc.getChunk().load(true);
                    team.loadNexus(nexusLoc, 1000);
                }
                team.setCuboid((Cuboid) section.get("cuboid"));
                if (team != Team.SPEC) {
                    team.setScore(0);
                }
            }
        }
        String defaultLoc = this.toString(world.getSpawnLocation());
        lobbyLocation = this.toLocation(this.getConfig().getString("lobby", defaultLoc));
    }

    public PlayerData getData(Player player) {
        PlayerData data = this.data.get(player.getUniqueId());
        if (data == null) {
            if(player.hasPermission("games.vip")) {
                data = new PlayerData(player.getUniqueId(), player.getName(), 5, 5, 5, 5, 5, 0);
            } else {
                data = new PlayerData(player.getUniqueId(), player.getName(), 1, 1, 1, 1, 1, 0);
            }
            this.loadData(player);
        }
        return data;
    }

    public void loadData(final Player player) {
        PlayerData data = null;

        if(player.hasPermission("games.vip")) {
            data = new PlayerData(player.getUniqueId(), player.getName(), 5, 5, 5, 5, 5, 0);
        } else {
            data = new PlayerData(player.getUniqueId(), player.getName(), 1, 1, 1, 1, 1, 0);
        }
        Main.this.data.put(player.getUniqueId(), data);
    }

    private void save() {
        this.getConfig().set("lobby", this.toString(lobbyLocation));
        for (Team team : Team.values()) {
            String name = team.getName();
            if (team.getSpawnLocation() != null) {
                this.getConfig().set("teams." + name + ".spawn", this.toString(team.getSpawnLocation()));
            }
            if (team.getNexusLocation() != null) {
                this.getConfig().set("teams." + name + ".nexus", this.toString(team.getNexusLocation()));
            }
            if (team.getCuboid() != null) {
                this.getConfig().set("teams." + name + ".cuboid", team.getCuboid());
            }
        }
        this.saveConfig();
    }

    public void setSpectator(Player player, boolean lose) {
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        if (lose && Team.getPlayerTeam(player) != Team.SPEC) {
            this.removePlayer(player);
        }
        Team.SPEC.addPlayer(player);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (player != online) {
                player.showPlayer(online);
                if (Team.getPlayerTeam(online) != Team.SPEC) {
                    online.hidePlayer(player);
                }
            }
        }
    }

    public void removePlayer(Player player) {
        final Team team = Team.getPlayerTeam(player);
        if (team != Team.SPEC) {
            team.removePlayer(player);
            Kit.setPlayerKit(player, null);
            if (Step.isStep(Step.LOBBY)) {
                data.remove(player.getUniqueId());
            } else if (Step.isStep(Step.IN_GAME) && team.getOnlinePlayers().size() == 0) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        for (EnderCrystal enderCrystal : Bukkit.getWorlds().get(0).getEntitiesByClass(EnderCrystal.class)) {
                            if (team.isNexus(enderCrystal)) {
                                team.loose(enderCrystal);
                                break;
                            }
                        }
                        Bukkit.broadcastMessage(Main.prefix + ChatColor.GRAY + "L'équipe " + team.getColor() + team.getDisplayName() + ChatColor.GRAY + " est éliminée !");
                        List<Team> aliveTeams = Team.getAliveTeams();
                        if (aliveTeams.size() == 1) {
                            Team winnerTeam = aliveTeams.get(0);
                            Bukkit.broadcastMessage(Main.prefix + ChatColor.GOLD + ChatColor.BOLD + "Victoire de l'équipe " + winnerTeam.getColor() + ChatColor.BOLD + winnerTeam.getDisplayName() + " " + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.BOLD + " Félicitations " + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|");
                            Main.this.stopGame(winnerTeam);
                        }
                    }
                }.runTaskLater(this, 1);
            }
        }
    }

    public void stopGame(Team winnerTeam) {
        Step.setCurrentStep(Step.POST_GAME);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setAllowFlight(true);
        }
        for (Entry<UUID, PlayerData> entry : data.entrySet()) {
            final String uuid = entry.getKey().toString().replaceAll("-", "");
            final PlayerData data = entry.getValue();
            if (winnerTeam != null) {
                Player online = Bukkit.getPlayer(entry.getKey());
                if (online != null && online.isOnline()) {
                    if (Team.getPlayerTeam(online) == winnerTeam) {
                        data.addCoins(10, false);
                    } else {
                        data.addCoins(2, false);
                    }
                }
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Main.this.teleportToLobby(online);
                }
            }
        }.runTaskLater(this, 300);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.shutdown();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"restart");
            }
        }.runTaskLater(this, 400);
    }

    public void teleportToLobby(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("lobby");
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    private Location toLocation(String string) {
        String[] splitted = string.split("_");
        World world = Bukkit.getWorld(splitted[0]);
        if (world == null || splitted.length < 6) {
            world = this.world;
        }
        return new Location(world, Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Double.parseDouble(splitted[3]), Float.parseFloat(splitted[4]), Float.parseFloat(splitted[5]));
    }

    public String toString(Location location) {
        World world = location.getWorld();
        return world.getName() + "_" + location.getX() + "_" + location.getY() + "_" + location.getZ() + "_" + location.getYaw() + "_" + location.getPitch();
    }

    public static Main getInstance() {
        return ins;
    }
}
