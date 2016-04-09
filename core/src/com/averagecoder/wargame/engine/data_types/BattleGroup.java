package com.averagecoder.wargame.engine.data_types;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class BattleGroup {
    public int groupID;
    public String groupName;
    public int gameID;
    public int playerID;
    public int points;
    public int maxMoves;
    public int visibility;
    public int tileX;
    public int tileY;
    public Array<Vector2> path = new Array<Vector2>();
    public boolean engaged;
}