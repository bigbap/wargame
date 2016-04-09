package com.averagecoder.wargame.engine.data_types;

public class Player {
    public int userid;
    // 0 invited to a game
    // 1 battle group creation phase
    // 2 deployment phase
    // 3 open turn phase
    // 4 end turn phase
    public int state;
    public int deploymentTileX;
    public int deploymentTileY;
    public int frame;
}
