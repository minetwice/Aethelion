package com.twicefear.aethelion.api;

/**
 * Runtime state of a playing animation
 */
public class AnimationState {
    private final String id;
    private final AnimationData data;
    private int currentTick = 0;
    private boolean finished = false;
    private final float speedMultiplier;
    
    public AnimationState(AnimationData data) {
        this(data, 1.0f);
    }
    
    public AnimationState(AnimationData data, float speedMultiplier) {
        this.id = data.getId();
        this.data = data;
        this.speedMultiplier = speedMultiplier;
    }
    
    public String getId() { return id; }
    public AnimationData getData() { return data; }
    public int getCurrentTick() { return currentTick; }
    public boolean isFinished() { return finished; }
    public float getSpeedMultiplier() { return speedMultiplier; }
    
    public void advance() {
        currentTick += speedMultiplier;
        if (currentTick >= data.getLength()) {
            if (data.isLoop()) {
                currentTick = 0;
            } else {
                finished = true;
            }
        }
    }
}
