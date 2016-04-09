package com.averagecoder.wargame.engine.data_types;

public class TileInfo{
    public int tileID;
    public boolean passable;
    public int moves;
    public int frame;

    public TileInfo(){}

    public TileInfo(int id, boolean pas, int mov, int fram){
        tileID = id;
        passable = pas;
        moves = mov;
        frame = fram;
    }
}