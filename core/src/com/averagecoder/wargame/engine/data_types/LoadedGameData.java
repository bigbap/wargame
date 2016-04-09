package com.averagecoder.wargame.engine.data_types;

import com.averagecoder.wargame.WargameCampaignMain;
import com.averagecoder.wargame.objects.Unit;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class LoadedGameData {
    public int gameID;
    public String gameName;
    public int points;
    public int mapid;
    public int status;
    public WargameCampaignMain.GameStates gameState;
    public Array<BattleGroup> battleGroups = new Array<BattleGroup>();
    public HashMap<Integer, Player> players = new HashMap<Integer, Player>();
}
