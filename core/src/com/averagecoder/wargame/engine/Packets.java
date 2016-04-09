package com.averagecoder.wargame.engine;

import com.averagecoder.wargame.engine.data_types.BattleGroup;
import com.averagecoder.wargame.engine.data_types.MapData;
import com.averagecoder.wargame.engine.data_types.Player;
import com.averagecoder.wargame.engine.data_types.TileInfo;
import com.averagecoder.wargame.engine.data_types.UnitType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class Packets {

    public static abstract class Packet{}

    public static class PacketSecurity extends Packet{
        public String username;
        public String password;
        public String email;
        public String task;
        public String message;
    }

    public static class PacketUserData extends Packet{
        public int userid;
        public Array<String> gameList = new Array<String>();
        public Array<String> pendingGameList = new Array<String>();
        public Array<String> mapData;
        public Array<TileInfo> tileData;
        public Array<UnitType> unitTypes;
        public String task;
        public String message;
    }

    public static class PacketGameData extends Packet{
        public String mapKey;
        public TileInfo[] tileInfo;
        public int gameID;
        public int playerID;
        public String gameName;
        public int points;
        public int mapid;
        public int status;
        public Array<BattleGroup> battleGroups = new Array<BattleGroup>();
        public Array<Player> players = new Array<Player>();
        public Array<MapData> mapData;
        public String task;
        public String message;
    }

    public static class PacketGameTasks extends Packet{
        public int gameID;
        public String gameName;
        public int userid;
        public int points;
        public int mapid;
        public Array<String> otherUsers;
        public Array<BattleGroup> battleGroups = new Array<BattleGroup>();

        public String task;
        public String message;
    }

    public static class PacketFriendTasks extends Packet{
        public Array<String> pending = null;
        public Array<String> friends = null;
        public int userid;
        public int friendid;
        public String username; //friend username
        public boolean accepted;
        public String task;
        public String message;
    }
}
