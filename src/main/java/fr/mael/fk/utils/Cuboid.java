package fr.mael.fk.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class Cuboid implements Iterable<Block>, ConfigurationSerializable {
    public static Cuboid createFromLocationRadius(Location location, double radius) {
        return Cuboid.createFromLocationRadius(location, radius, radius, radius);
    }

    public static Cuboid createFromLocationRadius(Location location, double xRadius, double yRadius, double zRadius) {
        Validate.notNull(location);
        if (xRadius < 0 || yRadius < 0 || zRadius < 0) { throw new IllegalArgumentException("The radius cannot be negative!"); }
        return xRadius > 0 || yRadius > 0 || zRadius > 0 ? new Cuboid(location.clone().subtract(xRadius, yRadius, zRadius), location.clone().add(xRadius, yRadius, zRadius)) : new Cuboid(location);
    }

    public static Cuboid deserialize(Map<String, Object> serializedCuboid) {
        return new Cuboid(serializedCuboid);
    }

    private String worldName = "";

    private int x1 = 0, y1 = 0, z1 = 0;

    private int x2 = 0, y2 = 0, z2 = 0;

    public Cuboid(Location location) {
        this(location, location);
    }

    public Cuboid(Location location, Location location2) {
        Validate.notNull(location);
        Validate.notNull(location2);
        if (!location.getWorld().getUID().equals(location2.getWorld().getUID())) { throw new IllegalArgumentException("Location 1 must be in the same world as Location 2!"); }

        worldName = location.getWorld().getName();

        x1 = Math.min((int) location.getX(), (int) location2.getX());
        y1 = Math.min((int) location.getY(), (int) location2.getY());
        z1 = Math.min((int) location.getZ(), (int) location2.getZ());

        x2 = Math.max((int) location.getX(), (int) location2.getX());
        y2 = Math.max((int) location.getY(), (int) location2.getY());
        z2 = Math.max((int) location.getZ(), (int) location2.getZ());
    }

    private Cuboid(Map<String, Object> serializedCuboid) {
        Validate.notNull(serializedCuboid);
        worldName = serializedCuboid.containsKey("World") ? (String) serializedCuboid.get("World") : "";
        x1 = serializedCuboid.containsKey("X1") ? (Integer) serializedCuboid.get("X1") : 0;
        y1 = serializedCuboid.containsKey("Y1") ? (Integer) serializedCuboid.get("Y1") : 0;
        z1 = serializedCuboid.containsKey("Z1") ? (Integer) serializedCuboid.get("Z1") : 0;
        x2 = serializedCuboid.containsKey("X2") ? (Integer) serializedCuboid.get("X2") : 0;
        y2 = serializedCuboid.containsKey("Y2") ? (Integer) serializedCuboid.get("Y2") : 0;
        z2 = serializedCuboid.containsKey("Z2") ? (Integer) serializedCuboid.get("Z2") : 0;
    }

    public boolean contains(int x, int y, int z) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public boolean contains(Location l) {
        if (!worldName.equals(l.getWorld().getName())) { return false; }
        return this.contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public List<Block> getBlocks() {
        List<Block> blockList = new ArrayList<Block>();
        World cuboidWorld = this.getWorld();
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    blockList.add(cuboidWorld.getBlockAt(x, y, z));
                }
            }
        }
        return blockList;
    }

    public int getVolume() {
        return (x2 - x1 + 1) * (y2 - y1 + 1) * (z2 - z1 + 1);
    }

    public World getWorld() {
        World cuboidWorld = Bukkit.getServer().getWorld(worldName);
        if (cuboidWorld == null) {
            cuboidWorld = Bukkit.getServer().createWorld(WorldCreator.name(worldName));
        }
        return cuboidWorld;
    }

    @Override
    public ListIterator<Block> iterator() {
        return this.getBlocks().listIterator();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializedCuboid = new HashMap<String, Object>();
        serializedCuboid.put("World", worldName);
        serializedCuboid.put("X1", x1);
        serializedCuboid.put("Y1", y1);
        serializedCuboid.put("Z1", z1);
        serializedCuboid.put("X2", x2);
        serializedCuboid.put("Y2", y2);
        serializedCuboid.put("Z2", z2);
        return serializedCuboid;
    }
}
