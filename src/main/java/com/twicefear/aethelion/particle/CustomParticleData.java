package com.twicefear.aethelion.particle;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;

public class CustomParticleData {
    private final String id;
    private final Particle type;
    private final Color color;
    private final float size;
    private final Material material;
    private final int count;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double speed;

    public CustomParticleData(String id, Particle type, Color color, float size, Material material, int count, double offsetX, double offsetY, double offsetZ, double speed) {
        this.id = id;
        this.type = type != null ? type : Particle.REDSTONE;
        this.color = color;
        this.size = size;
        this.material = material;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
    }

    public String getId() { return id; }
    public Particle getType() { return type; }
    public Color getColor() { return color; }
    public float getSize() { return size; }
    public Material getMaterial() { return material; }
    public int getCount() { return count; }
    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
    public double getOffsetZ() { return offsetZ; }
    public double getSpeed() { return speed; }
}
