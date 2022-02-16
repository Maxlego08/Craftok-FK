package fr.mael.fk.handler;

import java.util.*;

import fr.mael.fk.Main;
import fr.mael.fk.Nexus;
import fr.mael.fk.utils.Cuboid;
import fr.mael.fk.utils.ItemBuilder;

import org.bukkit.*;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public enum Team {
    BLUE("blue", "Bleue", Material.BANNER, DyeColor.BLUE.getDyeData(), ChatColor.BLUE),
    RED("red", "Rouge", Material.BANNER, DyeColor.RED.getDyeData(), ChatColor.RED),
    GREEN("green", "Verte", Material.BANNER, DyeColor.LIME.getDyeData(), ChatColor.GREEN),
    YELLOW("yellow", "Jaune", Material.BANNER, DyeColor.YELLOW.getDyeData(), ChatColor.YELLOW),
    SPEC("spec", "Spec", null, (short) 0, ChatColor.GRAY);

    public static Team getPlayerTeam(Player player) {
        if (player == null) {
            return SPEC;
        } else if (!player.hasMetadata("team")) {
            for (Team team : Team.values()) {
                if (team.craftTeam.getPlayers().contains(player)) {
                    return team;
                }
            }
        } else {
            String teamName = player.getMetadata("team").get(0).asString();
            return Team.getTeam(teamName);
        }
        return SPEC;
    }

    public static Team getRandomTeam() {
        Team lastTeam = Team.BLUE;
        for (Team team : Team.values()) {
            if (team != SPEC && lastTeam != team && team.craftTeam.getSize() < lastTeam.craftTeam.getSize()) {
                lastTeam = team;
            }
        }
        return lastTeam;
    }

    public static Team getTeam(String name) {
        for (Team team : Team.values()) {
            if (team.craftTeam != null && team.craftTeam.getName().equalsIgnoreCase(name)) { return team; }
        }
        return SPEC;
    }

    public static Team getTeam(ChatColor color) {
        for (Team team : Team.values()) {
            if (team.color == color) { return team; }
        }
        return null;
    }

    public static List<Team> getAliveTeams() {
        List<Team> aliveTeams = new ArrayList<>();
        for (Team team : Team.values()) {
            if (team != Team.SPEC && team.getOnlinePlayers().size() > 0) {
                aliveTeams.add(team);
            }
        }
        return aliveTeams;
    }

    public static List<Team> getListTeams() {
        List<Team> listTeams = new ArrayList<>();
        for (Team team : Team.values()) {
            if (team != Team.SPEC) {
                listTeams.add(team);
            }
        }

        Collections.reverse(listTeams);

        return listTeams;
    }

    public static Nexus getNexusFromLocation(EnderCrystal enderCrystal) {
        for (Team team : Team.values()) {
            if (team.getNexus().getLocation().equals(enderCrystal.getLocation())) {
                return team.getNexus();
            }
        }
        return null;
    }

    private String name;
    private String displayName;
    private ItemStack icon;
    private ChatColor color;
    private org.bukkit.scoreboard.Team craftTeam;
    private Location spawnLocation;
    private Location nexusLocation;
    private Cuboid cuboid;
    private Nexus nexus;

    private Team(String name, String displayName, Material material, short durability, ChatColor color) {
        this.name = name;
        this.displayName = displayName;
        if (material != null) {
            icon = new ItemBuilder(material, 1, durability).setTitle(color + "Rejoindre l'équipe " + displayName).build();
        }
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public org.bukkit.scoreboard.Team getCraftTeam() {
        return craftTeam;
    }

    public void setCraftTeam(org.bukkit.scoreboard.Team craftTeam) {
        this.craftTeam = craftTeam;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getNexusLocation() {
        return nexusLocation;
    }

    public void setNexusLocation(Location nexusLocation) {
        this.nexusLocation = nexusLocation;
    }
    public void loadNexus(Location loc, int health) {
        if (this != Team.SPEC || cuboid != null) {
            this.nexus = new Nexus(this, loc, health);
        }
    }

    public Nexus getNexus() {
        if (this != Team.SPEC || cuboid != null) {
            return this.nexus;
        }
        return null;
    }

    public boolean isNexus(EnderCrystal enderCrystal) {
        if (this == Team.SPEC || cuboid == null) {
            return false;
        }
        return enderCrystal.getCustomName().equals(color + "§c§lNEXUS §r" + color + displayName + " §7- §cVie » " + nexus.getHealth());
    }

    public void loose(EnderCrystal enderCrystal) {
        for (final Player player : this.getOnlinePlayers()) {
            player.damage(0);
            player.setHealth(0);
            Main.instance.setSpectator(player, true);
        }
        cuboid = null;
        enderCrystal.getWorld().strikeLightning(nexusLocation);
        enderCrystal.getWorld().playEffect(enderCrystal.getLocation(), Effect.FLAME, 2);
        enderCrystal.remove();
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public void addPlayer(Player player) {
        player.setMetadata("team", new FixedMetadataValue(Main.instance, name));
        player.setPlayerListName(color + (player.getName().length() > 14 ? player.getName().substring(0, 14) : player.getName()));
        craftTeam.addPlayer(player);
        if (this != Team.SPEC) {
            Score score = this.getScore();
            this.setScore(score.getScore() + 1);
        }
    }

    public void removePlayer(Player player) {
        player.removeMetadata("team", Main.instance);
        craftTeam.removePlayer(player);
        if (this != Team.SPEC) {
            Score score = this.getScore();
            this.setScore(score.getScore() - 1);
        }
    }

    public Score getScore() {
        Score objScore = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("teams").getScore(color + "Equipe " + displayName);
        return objScore;
    }

    public void setScore(int score) {
        if (Step.isStep(Step.LOBBY)) {
            Score objScore = this.getScore();
            if (score == 0) {
                objScore.setScore(1);
            }
            objScore.setScore(score);
        }
    }

    public Set<Player> getOnlinePlayers() {
        Set<Player> players = new HashSet<>();
        for (OfflinePlayer offline : craftTeam.getPlayers()) {
            if (offline instanceof Player && offline.isOnline()) {
                players.add((Player) offline);
            }
        }
        return players;
    }

    public void broadcastMessage(String msg) {
        for (Player player : this.getOnlinePlayers()) {
            player.sendMessage(msg);
        }
    }

    public void createTeam(Scoreboard scoreboard) {
        craftTeam = scoreboard.getTeam(name);
        if (craftTeam == null) {
            craftTeam = scoreboard.registerNewTeam(name);
        }
        craftTeam.setPrefix(color.toString());
        craftTeam.setDisplayName(name);
        craftTeam.setAllowFriendlyFire(false);
    }
}