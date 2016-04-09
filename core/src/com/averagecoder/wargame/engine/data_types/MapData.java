package com.averagecoder.wargame.engine.data_types;

public class MapData{
    public int mapID;
    public String mapName;
    public String mapData;
    public int width;
    public int height;

    public MapData(){}

    public MapData(int id, String name, String data, int w, int h){
        mapID = id;
        mapName = name;
        mapData = data;
        width = w;
        height = h;
    }
}
