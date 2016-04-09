package com.averagecoder.wargame.engine.data_types;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class TurnInfo {
    public int turnID;
    public HashMap<Integer, HashMap> players;
    public HashMap<Integer, Vector2> unitPositions;
}
