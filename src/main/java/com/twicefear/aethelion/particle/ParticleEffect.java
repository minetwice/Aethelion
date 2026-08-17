package com.twicefear.aethelion.particle;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.joml.Vector3f;

import java.util.List;

/**
 * Custom particle effect definition from JSON
 */
public class ParticleEffect {
    private final String id;
    private final List<ParticleFrame> frames;
    private final int duration;
    private final boolean loop;
    
    public ParticleEffect(String id, List<ParticleFrame> frames, int duration, boolean loop) {
        this.id = id;
        this.frames = frames;
        this.duration = duration;
        this.loop = loop;
    }
    
    public String getId() { return id; }
    public List<ParticleFrame> getFrames() { return frames; }
    public int getDuration() { return duration; }
    public boolean isLoop() { return loop; }
}

/**
 * Single frame of a particle effect
 */
class ParticleFrame {
    private final int tick;
    private final ParticleType type;
    private final Vector3f offset;
    private final float speed;
    private final int count;
    private final ParticleData data;
    
    public ParticleFrame(int tick, ParticleType type, Vector3f offset, float speed, int count, ParticleData data) {
        this.tick = tick;
        this.type = type;
        this.offset = offset;
        this.speed = speed;
        this.count = count;
        this.data = data;
    }
    
    public int getTick() { return tick; }
    public ParticleType getType() { return type; }
    public Vector3f getOffset() { return offset; }
    public float getSpeed() { return speed; }
    public int getCount() { return count; }
    public ParticleData getData() { return data; }
    
    public void spawn(Location center, List<Player> viewers) {
        for (Player p : viewers) {
            if (p.getWorld().equals(center.getWorld()) && 
                p.getLocation().distance(center) <= 48) {
                
                Location spawnLoc = center.clone().add(
                    offset.x(), offset.y(), offset.z()
                );
                
                switch (type) {
                    case DUST:
                        if (data instanceof DustParticleData dust) {
                            p.spawnParticle(Particle.REDSTONE, spawnLoc, count, 
                                0.1, 0.1, 0.1, 0, dust.color);
                        }
                        break;
                    case FLAME:
                        p.spawnParticle(Particle.FLAME, spawnLoc, count, 0.1, 0.1, 0.1, speed);
                        break;
                    case SMOKE:
                        p.spawnParticle(Particle.SMOKE_NORMAL, spawnLoc, count, 0.1, 0.1, 0.1, speed);
                        break;
                    case HEART:
                        p.spawnParticle(Particle.HEART, spawnLoc, count, 0.1, 0.1, 0.1, speed);
                        break;
                    case NOTE:
                        p.spawnParticle(Particle.NOTE, spawnLoc, count, 0.1, 0.1, 0.1, speed);
                        break;
                    case CUSTOM_TEXTURE:
                        // Will be handled by packet renderer
                        break;
                }
            }
        }
    }
}

/**
 * Particle type enum
 */
enum ParticleType {
    DUST, FLAME, SMOKE, HEART, NOTE, CUSTOM_TEXTURE
}

/**
 * Base particle data interface
 */
interface ParticleData {}

/**
 * Dust particle data with color and size
 */
class DustParticleData implements ParticleData {
    public final Color color;
    public final float size;
    
    public DustParticleData(Color color, float size) {
        this.color = color;
        this.size = size;
    }
}
