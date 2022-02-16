package fr.mael.fk.utils;

import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collection;

public class Scoreboard {
    private boolean created = false;
    private  String[] lines = new String[16];
    private PacketPlayOutScoreboardTeam[] teams = new PacketPlayOutScoreboardTeam[16];
    private final Player player;
    private String objectiveName;
    
    public Scoreboard(Player player, String objectiveName) {
        this.player = player;
        this.objectiveName = objectiveName;
    }

    public Scoreboard(Player player, String objectiveName, String[] lines) {
        this.player = player;
        this.objectiveName = objectiveName;
        this.lines = lines;
    }

    public void create() {
        if (created) return;


        PlayerConnection player = getPlayer();
        player.sendPacket(createObjectivePacket(0, objectiveName));
        player.sendPacket(setObjectiveSlot());
        int i = 0;
        while (i < lines.length)
        {
            sendLine(i++);
        }
        created = true;


    }
    
    public void update() {
    	
        if (!created) return;

        PlayerConnection player = getPlayer();
        player.sendPacket(setObjectiveSlot());

    	
    }

    public void destroy() {
        if (!created)
            return;

        getPlayer().sendPacket(createObjectivePacket(1, null));

        created = false;
    }

    private PlayerConnection getPlayer() {
        return ((CraftPlayer) player).getHandle().playerConnection;
    }

    @SuppressWarnings("rawtypes")
	private Packet sendLine(int line) {
        if (line > 15)
            return null;
        if (line < 0)
            return null;
        if (!created)
            return null;
        PacketPlayOutScoreboardTeam teamPacket = apply(line);
        int score = (line * 1);
        getPlayer().sendPacket(teamPacket);
        return sendScore(ChatColor.values()[line].toString() + ChatColor.RESET , score);
    }
            
    public void setObjectiveName(String name) {
        this.objectiveName = name;
        if (created)
            getPlayer().sendPacket(createObjectivePacket(2, name));
    }

    public void setLine(int line, String value) {
        lines[line] = value;
        getPlayer().sendPacket(sendLine(line));
    }
                
    public void removeLine(int line) {
        String oldLine = ChatColor.values()[line].toString() + ChatColor.RESET;
        if (oldLine != null && created)
            getPlayer().sendPacket(removeLine(oldLine));

        lines[line] = null;
    }

    public String getLine(int line) {
        if (line > 15)
            return null;
        if (line < 0)
            return null;
        return lines[line];
    }

    /*
    Factories
     */
    private PacketPlayOutScoreboardObjective createObjectivePacket(int mode, String displayName) {
        PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
        try {
            // Nom de l'objectif
            Field name = packet.getClass().getDeclaredField("a");
            name.setAccessible(true);
            name.set(packet, player.getName());

            // Mode
            // 0 : cr�er
            // 1 : Supprimer
            // 2 : Mettre � jour
            Field modeField = packet.getClass().getDeclaredField("d");
            modeField.setAccessible(true);
            modeField.set(packet, mode);

            if (mode == 0 || mode == 2) {
                Field displayNameField = packet.getClass().getDeclaredField("b");
                displayNameField.setAccessible(true);
                displayNameField.set(packet, displayName);

                Field display = packet.getClass().getDeclaredField("c");
                display.setAccessible(true);
                display.set(packet, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

    private PacketPlayOutScoreboardDisplayObjective setObjectiveSlot() {
        PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective();
        try {
            // Slot de l'objectif
            Field position = packet.getClass().getDeclaredField("a");
            position.setAccessible(true);
            position.set(packet, 1); // SideBar

            Field name = packet.getClass().getDeclaredField("b");
            name.setAccessible(true);
            name.set(packet, player.getName());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }

    private PacketPlayOutScoreboardScore sendScore(String line, int score) {
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(line);
        try {
            Field name = packet.getClass().getDeclaredField("b");
            name.setAccessible(true);
            name.set(packet, player.getName());

            Field scoreField = packet.getClass().getDeclaredField("c");
            scoreField.setAccessible(true);
            scoreField.set(packet, score); // SideBar

            Field action = packet.getClass().getDeclaredField("d");
            action.setAccessible(true);
            action.set(packet, PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return packet;
    }
    
    private PacketPlayOutScoreboardScore removeLine(String line) {
        return new PacketPlayOutScoreboardScore(line);
    }

    public Player getBukkitPlayer() {
        return player;
    }

    public String getDisplayName() {
        return objectiveName;
    }

    public String[] getLines() {
        return lines;
    }


    private String[] splitString(String string)
    {
        Validate.isTrue(string.length() <= 32, "String can't have more than 32 characters " );
        if (string.isEmpty()) {
            return new String[] { " ", "" };
        }
        StringBuilder prefix = new StringBuilder(string.substring(0, string.length() >= 16 ? 16 : string.length()));
        StringBuilder suffix = new StringBuilder(string.length() > 16 ? string.substring(16) : "");
        if (prefix.charAt(prefix.length() - 1) == '§')
        {
            prefix.deleteCharAt(prefix.length() - 1);
            suffix.insert(0, '§');
        }
        if(!suffix.toString().isEmpty())
            suffix.insert(0, ChatColor.getLastColors(prefix.toString()));

        return new String[] { prefix.toString().length() > 16 ? prefix.toString().substring(0, 16) : prefix.toString(), suffix.toString().length() > 16 ? suffix.toString().substring(0, 16) : suffix.toString() };
    }


    private PacketPlayOutScoreboardTeam apply(int line){
        if(teams[line] != null)
        {
            setField(getTeamLine(line),"h", 2);
            setField(getTeamLine(line), "c", splitString(getLine(line))[0]);
            setField(getTeamLine(line), "d", splitString(getLine(line))[1]);
        }


        return getTeamLine(line);
    }

    private PacketPlayOutScoreboardTeam getTeamLine(int line) {
        if(teams[line] == null)
        {
            PacketPlayOutScoreboardTeam team = new PacketPlayOutScoreboardTeam();
            setField(team, "a", line + "");
            setField(team, "b", line + "");
            setField(team, "c", splitString(getLine(line))[0]);
            setField(team, "d", splitString(getLine(line))[1]);
            addEntry(team, ChatColor.values()[line].toString() + ChatColor.RESET);
            teams[line] = team;
        }
        return teams[line];
    }


    private static void setField(PacketPlayOutScoreboardTeam packet, String field, Object value) {
        try {
            Field f = packet.getClass().getDeclaredField(field);
            f.setAccessible(true); 
            f.set(packet, value); 
            f.setAccessible(false);
        } catch (Exception ex) {ex.printStackTrace();}
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void addEntry(PacketPlayOutScoreboardTeam packet, String entry) {
        Field f = null;
        try {
            f = packet.getClass().getDeclaredField("g");
            f.setAccessible(true);
            ((Collection) f.get(packet)).add(entry);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * Reset all & leave bar & scrolling text
     */
    public void reset() {
        for(int i = 1; i <14; i++)
            removeLine(i);
    }
}