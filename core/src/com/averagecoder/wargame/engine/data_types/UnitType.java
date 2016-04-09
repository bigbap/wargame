package com.averagecoder.wargame.engine.data_types;

public class UnitType {
    public int unitTypeID;
    public String unitName;
    public int moves;
    public int visibility;
    public int pointsValue;

    public UnitType(){}

    public UnitType(int id, String name, int mov, int vis, int points){
        unitTypeID = id;
        unitName = name;
        moves = mov;
        visibility = vis;
        pointsValue = points;
    }
}
