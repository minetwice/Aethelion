package com.yourname.emoteengine.api;

public class AnimationState {
    private final String id;
    private final AnimationData data;
    private int currentTick = 0;
    private boolean finished = false;
    
    public AnimationState(AnimationData data) {
        this.id = data.getId();
        this.data = data;
    }
    
    public String getId() { return id; }
    public AnimationData getData() { return data; }
    public int getCurrentTick() { return currentTick; }
    public boolean isFinished() { return finished; }
    
    public void advance() {
        currentTick++;
        if (currentTick >= data.getLength()) {
            if (data.isLoop()) {
                currentTick = 0;
            } else {
                finished = true;
            }
        }
    }
}
