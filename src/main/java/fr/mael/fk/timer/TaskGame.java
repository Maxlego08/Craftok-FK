package fr.mael.fk.timer;

import fr.mael.fk.handler.Step;
import fr.mael.fk.Main;
import fr.mael.fk.handler.State;
import fr.mael.fk.handler.Team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskGame extends BukkitRunnable {
    private Main plugin;
    public static int day = 1;
    public static int hour = 6;
    public static int minutes = 0;
    public static int nextHour;
    private static String nextState;
    private static boolean deathMatch;

    public static int pvp = 360;
    public static int assault = 1260;
    public static int deathmatch = 2460;

    public TaskGame(Main plugin) {
        this.plugin = plugin;
        this.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void run() {
        if (!Step.isStep(Step.IN_GAME)) {
            this.cancel();
            plugin.stopGame(null);
            return;
        }
        for (Team team : Team.values()) {
            if (team == Team.SPEC) {
                continue;
            }
            //String name = team.getColor() + "Wither " + (team.getDisplayName().endsWith("e") && team != Team.RED && team != Team.YELLOW ? team.getDisplayName().substring(0, team.getDisplayName().length() - 1) : team.getDisplayName());
            if (team.getCuboid() == null) {
            } else {
                for (EnderCrystal enderCrystal : Bukkit.getWorlds().get(0).getEntitiesByClass(EnderCrystal.class)) {
                    if (!team.isNexus(enderCrystal)) {
                        continue;
                    } else if (TaskGame.deathMatch) {
                        team.getNexus().damage(1, enderCrystal, false);
                    }
                    if (enderCrystal.getLocation().distanceSquared(team.getNexusLocation()) >= 0.5) {
                        enderCrystal.teleport(team.getNexusLocation());
                    }
                }
            }
        }
        if (TaskGame.minutes == 60) {
            TaskGame.hour++;
            TaskGame.minutes = 0;
            TaskGame.nextHour -= 1;
            if (TaskGame.hour == 24) {
                TaskGame.hour = 0;
                TaskGame.day++;
                return;
            }
        }

        if(TaskGame.pvp > 0) {
            TaskGame.pvp--;
        }

        if(TaskGame.assault > 0) {
            TaskGame.assault--;
        }

        if(TaskGame.deathmatch > 0) {
            TaskGame.deathmatch--;
        }

        if (TaskGame.hour == 6 && TaskGame.minutes == 0) {
            plugin.world.setTime(23000);
            if (TaskGame.day == 1) {
                Bukkit.broadcastMessage(Main.prefix + ChatColor.AQUA + "Le jour " + ChatColor.GOLD + TaskGame.day + ChatColor.AQUA + " se lève...");
                if (State.isState(State.NONE)) {
                    TaskGame.nextState = "PvP";
                    plugin.world.setTime(23000);
                    Bukkit.broadcastMessage(Main.prefix + ChatColor.GRAY + "Préparez-vous avant de pouvoir vous battre.");
                    State.setState(State.PREPARATION);
                    TaskGame.nextHour = 6;
                }
            }
        } else if (TaskGame.hour == 12 && TaskGame.minutes == 0) {
            plugin.world.setTime(6000);
            if (TaskGame.day == 1 && State.isState(State.PREPARATION)) {
                TaskGame.nextHour = 15;
                TaskGame.nextState = "A l'assaut !";
                Bukkit.broadcastMessage(Main.prefix + ChatColor.AQUA + "Vous pouvez désormais PvP.");
                State.setState(State.PVP);
            }
        } else if (TaskGame.hour == 3 && TaskGame.minutes == 0) {
            if (TaskGame.day == 2 && State.isState(State.PVP)) {
                TaskGame.nextHour = 20;
                TaskGame.nextState = "Mort subite";
                Bukkit.broadcastMessage(Main.prefix + ChatColor.AQUA + "Les assauts sont maintenants actifs, bonne chance.");
                State.setState(State.ASSAULT);
            }
        } else if (TaskGame.hour == 23 && TaskGame.minutes == 0) {
            if (TaskGame.day == 2 && State.isState(State.ASSAULT)) {
                Bukkit.broadcastMessage(Main.prefix + ChatColor.RED + "Deathmatch !" + ChatColor.AQUA + " Les nexus perderont 1 de vie chaque seconde !");
                State.setState(State.DEATHMATCH);
                TaskGame.deathMatch = true;
                return;
            }
        }
        TaskGame.minutes++;
    }
}