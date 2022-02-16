package fr.mael.fk.scoreboard;

import fr.mael.fk.Main;
import fr.mael.fk.handler.Step;
import fr.mael.fk.handler.Team;
import fr.mael.fk.timer.TaskGame;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class Scoreboard {

    private static Date d = Calendar.getInstance().getTime();
    private static DateFormat df = new java.text.SimpleDateFormat("HH:mm:ss");
    private static String time = df.format(d);

    public static void createScoreBoard(Player p) {

        final fr.mael.fk.utils.Scoreboard sb = new fr.mael.fk.utils.Scoreboard(p, "§c§lFallenkingdom");


        sb.create();

        if(Step.isStep(Step.LOBBY)) {
            sb.setLine(8, "§8§m----------");

            for(int i = 0; i < Team.getListTeams().size(); i++) {
                sb.setLine(i+3, Team.getListTeams().get(i).getColor() + Team.getListTeams().get(i).getDisplayName() + " : §f" + Team.getListTeams().get(i).getOnlinePlayers().size());
            }

            sb.setLine(2, "§8§m----------");
            sb.setLine(1, "§c§o§lplay.sirihack.fr");
        } else if(Step.isStep(Step.IN_GAME)) {

            sb.setLine(10, "§8§m----------");

            for(int i = 0; i < Team.getListTeams().size(); i++) {
                sb.setLine(i+5, Team.getListTeams().get(i).getColor() + Team.getListTeams().get(i).getDisplayName() + " : §f" + Team.getListTeams().get(i).getOnlinePlayers().size());
            }

            sb.setLine(4, "§8§m----------");


            if(TaskGame.pvp > 0) {
                sb.setLine(3, "§6PvP: §e" + formatDurationMS(TaskGame.pvp));
            } else {
                sb.setLine(3, "§6Assault: §e" + formatDurationMS(TaskGame.assault));
            }

            sb.setLine(2, "§8§m----------");

            sb.setLine(1, "§c§o§lplay.sirihack.fr");


        } else if(Step.isStep(Step.POST_GAME)) {



        }

        new BukkitRunnable() {
            @Override
            public void run() {

                Date d = Calendar.getInstance().getTime();
                DateFormat df = new java.text.SimpleDateFormat("HH:mm:ss");
                String time = df.format(d);

                if (!p.isOnline()) {
                    cancel();
                    return;
                }

                if(Step.isStep(Step.LOBBY)) {
                    sb.setLine(8, "§8§m----------");

                    for(int i = 0; i < Team.getListTeams().size(); i++) {
                        sb.setLine(i+3, Team.getListTeams().get(i).getColor() + Team.getListTeams().get(i).getDisplayName() + " : §f" + Team.getListTeams().get(i).getOnlinePlayers().size());
                    }

                    sb.setLine(2, "§8§m----------");
                } else if(Step.isStep(Step.IN_GAME)) {

                    //sb.setObjectiveName(ChatColor.GOLD + "Jour " + TaskGame.day + ChatColor.GREEN + " " + (TaskGame.hour < 10 ? "0" : "") + TaskGame.hour + "H" + (TaskGame.minutes < 10 ? "0" : "") + TaskGame.minutes);
                    sb.setObjectiveName("§cFK §8| " + ChatColor.GOLD + "Jour " + TaskGame.day + ChatColor.GREEN + " " + (TaskGame.hour < 10 ? "0" : "") + TaskGame.hour + "H" + (TaskGame.minutes < 10 ? "0" : "") + TaskGame.minutes);

                    sb.setLine(10, "§8§m----------");

                    for(int i = 0; i < Team.getListTeams().size(); i++) {
                        sb.setLine(i+5, Team.getListTeams().get(i).getColor() + Team.getListTeams().get(i).getDisplayName() + " : §f" + Team.getListTeams().get(i).getOnlinePlayers().size());
                    }

                    sb.setLine(4, "§8§m----------");


                    if(TaskGame.pvp > 0) {
                        sb.setLine(3, "§6PvP: §e" + formatDurationMS(TaskGame.pvp));
                    } else if(TaskGame.assault > 0) {
                        sb.setLine(3, "§6Assault: §e" + formatDurationMS(TaskGame.assault));
                    } else if(TaskGame.deathmatch > 0){
                        sb.setLine(3, "§6DeathMatch: §e" + formatDurationMS(TaskGame.deathmatch));
                    } else {
                        sb.setLine(3, "§6DeathMatch: §a✔");
                    }

                    sb.setLine(2, "§8§m----------");


                } else if(Step.isStep(Step.POST_GAME)) {



                }

            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0L, 20L);

        new BukkitRunnable() {

            private int current = 0;

            @Override
            public void run() {

                String text = "play.sirihack.fr";

                String display = "§c" + text.substring(0, current) + "§f" + text.substring(current, current + 1) + "§c" + text.substring(current + 1);

                sb.setLine(1,display);

                current++;

                if (current == text.indexOf(' ')) {
                    current++;
                }

                if (current == text.length()) {
                    current = 0;
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0L, 2L);


    }

    private static String formatDurationMS(int seconds) {
        int minutes = seconds % 3600 / 60;
        seconds %= 60;
        return minutes + ":" + twoDigitString(seconds);
    }

    private static String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        } else {
            return number / 10 == 0 ? "0" + number : String.valueOf(number);
        }
    }

}