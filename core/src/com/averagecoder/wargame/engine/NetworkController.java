package com.averagecoder.wargame.engine;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.WargameCampaignMain;
import com.averagecoder.wargame.WargameCampaignStart;
import com.averagecoder.wargame.engine.data_types.BattleGroup;
import com.averagecoder.wargame.engine.data_types.MapData;
import com.averagecoder.wargame.engine.data_types.Player;
import com.averagecoder.wargame.engine.data_types.TileInfo;
import com.averagecoder.wargame.engine.data_types.UnitType;
import com.averagecoder.wargame.objects.Unit;
import com.averagecoder.wargame.scenes.menus.AddBattleGroupsMenu;
import com.averagecoder.wargame.scenes.menus.Menu;
import com.averagecoder.wargame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import javax.rmi.CORBA.Util;

public class NetworkController implements Runnable {
    WargameCampaign game;

    String ipAddress = "10.0.0.6";
    //String ipAddress = "10.0.0.108";
    int portSocket = 25565;

    boolean debug = false;
    boolean running = false;

    public Client client;
    public boolean connected = false;

    float serverQueryDelay = 500.0f;
    float serverQueryDelta = serverQueryDelay;
    public Array<NetworkRequest> queue = new Array<NetworkRequest>();

    public NetworkController(WargameCampaign wargameCampaign){

        game = wargameCampaign;

        client = new Client(2048, 2048);
        ClientListener clientListener = new ClientListener();
        clientListener.init(this);

        registerPackets();
        client.addListener(clientListener);

        new Thread(client).start();

        connect();

        running = true;
        new Thread(this).start();
    }

    public void connect(){
        try{
            client.connect(5000, ipAddress, portSocket);
        }catch(IOException e){
            Utils.log("[CLIENT] >> unable to connect to server " + ipAddress + ":" + portSocket);

            if(debug)
                e.printStackTrace();
        }
    }

    private void registerPackets(){
        Kryo kryo = client.getKryo();

        kryo.register(Packets.PacketSecurity.class);
        kryo.register(Packets.PacketUserData.class);
        kryo.register(Packets.PacketGameData.class);
        kryo.register(Packets.PacketGameTasks.class);
        kryo.register(Packets.PacketFriendTasks.class);
        kryo.register(HashMap.class);
        kryo.register(Vector2.class);
        kryo.register(String.class);
        kryo.register(Array.class);
        kryo.register(Object[].class);
        kryo.register(int[].class);
        kryo.register(TileInfo.class);
        kryo.register(MapData.class);
        kryo.register(UnitType.class);
        kryo.register(BattleGroup.class);
        kryo.register(Player.class);
    }

    /*******************************/
    /*********SERVER REQUESTS*******/
    /*******************************/
    public void refreshGameList(){
        Packets.PacketUserData gameListRequest = new Packets.PacketUserData();
        gameListRequest.userid = WargameCampaign.userid;
        gameListRequest.task = "getGameList";
        gameListRequest.message = "New game list request: userid = " + WargameCampaign.userid;
        newRequest(gameListRequest);
    }

    public void refreshGameState(){
        Packets.PacketGameData gameStateRequest = new Packets.PacketGameData();
        gameStateRequest.gameID = WargameCampaignMain.gameID;
        gameStateRequest.playerID = 0;
        gameStateRequest.task = "getGameState";
        gameStateRequest.message = "New game state request: gameID = " + WargameCampaignMain.gameID;
        newRequest(gameStateRequest);
    }

    public void refreshPendingFriendsList(){
        Packets.PacketFriendTasks pendingFriendListRequest = new Packets.PacketFriendTasks();
        pendingFriendListRequest.userid = WargameCampaign.userid;
        pendingFriendListRequest.task = "pendingFriendsList";
        pendingFriendListRequest.message = "New pending friend list request: userid = " + WargameCampaign.userid;
        newRequest(pendingFriendListRequest);
    }
    public void refreshFriendsList(){
        Packets.PacketFriendTasks friendListRequest = new Packets.PacketFriendTasks();
        friendListRequest.userid = WargameCampaign.userid;
        friendListRequest.task = "friendsList";
        friendListRequest.message = "New friend list request: userid = " + WargameCampaign.userid;
        newRequest(friendListRequest);
    }

    /*******************************/
    /*********RESPONSE PARSERS******/
    /*******************************/
    public void parseUserData(Packets.PacketUserData userData){
        if(userData.userid > 0){
            WargameCampaign.userid = userData.userid;
            connected = true;

            WargameCampaign.prefs.putString("username", WargameCampaign.username);
            WargameCampaign.prefs.putString("password", WargameCampaign.password);
            WargameCampaign.prefs.putInteger("userid", WargameCampaign.userid);
            WargameCampaign.prefs.flush();

            //get game list
            if(WargameCampaign.gameList == null){
                refreshGameList();
            }

            //get friend list
            if(WargameCampaign.friendList == null){
                refreshFriendsList();
            }

            //get pending friend requests
            if(WargameCampaign.friendRequestList == null){
                refreshPendingFriendsList();
            }
        }

        if(userData.gameList != null && userData.gameList.size > 0){
            if(WargameCampaign.gameList == null)
                WargameCampaign.gameList = new HashMap<Integer, String[]>();

            WargameCampaign.gameList.clear();

            for(String s: userData.gameList){
                String [] gameData = s.split("::");

                int gameID = Integer.parseInt(gameData[0]);
                String gameName = gameData[1];
                String gamePoints = gameData[2];
                String mapName = gameData[3];

                WargameCampaign.gameList.put(gameID, new String[]{gameName, gamePoints, mapName});
            }
        }

        if(userData.pendingGameList != null && userData.pendingGameList.size > 0){
            if(WargameCampaign.pendingGameList == null)
                WargameCampaign.pendingGameList = new HashMap<Integer, String[]>();

            WargameCampaign.pendingGameList.clear();

            for(String s: userData.pendingGameList){
                String [] gameData = s.split("::");

                int gameID = Integer.parseInt(gameData[0]);
                String gameName = gameData[1];
                String gamePoints = gameData[2];
                String mapName = gameData[3];

                WargameCampaign.pendingGameList.put(gameID, new String[]{gameName, gamePoints, mapName});
            }
        }

        if(userData.mapData != null && userData.mapData.size > 0){
            if(WargameCampaign.mapList == null)
                WargameCampaign.mapList = new HashMap<Integer, String>();

            WargameCampaign.mapList.clear();

            for(String s: userData.mapData){
                String [] gameData = s.split("::");

                int mapID = Integer.parseInt(gameData[0]);
                String mapName = gameData[1];

                WargameCampaign.mapList.put(mapID, mapName);
            }
        }

        if(userData.tileData != null && userData.tileData.size > 0)
            WargameCampaign.tileData = userData.tileData;
    }

    public void parseGameData(Packets.PacketGameData gameData){
        if(gameData.task == null)
            gameData.task = "";

        if(gameData.task.equals("loadGameData")) {
            WargameCampaignMain.GameStates state;
            if(gameData.status == 0)
                state = WargameCampaignMain.GameStates.SETUP;
            else if(gameData.status == 1)
                state = WargameCampaignMain.GameStates.DEPLOYMENT;
            else if(gameData.status == 2)
                state = WargameCampaignMain.GameStates.ACTIVE;
            else if(gameData.status == 3)
                state = WargameCampaignMain.GameStates.END_TURN;
            else
                state = WargameCampaignMain.GameStates.SETUP;

            WargameCampaign.loadedGameData.gameID = gameData.gameID;
            WargameCampaign.loadedGameData.gameName = gameData.gameName;
            WargameCampaign.loadedGameData.points = gameData.points;
            WargameCampaign.loadedGameData.mapid = gameData.mapid;
            WargameCampaign.loadedGameData.status = gameData.status;
            WargameCampaign.loadedGameData.gameState = state;
            WargameCampaign.loadedGameData.battleGroups = gameData.battleGroups;
            for(Player p: gameData.players)
                WargameCampaign.loadedGameData.players.put(p.userid, p);

            //setup this player
            int oldState = 0;
            if(WargameCampaign.thisPlayer != null)
                oldState = WargameCampaign.thisPlayer.state;
            for (Player play : gameData.players) {
                if(play.userid == WargameCampaign.userid) {
                    WargameCampaign.thisPlayer = play;

                    if(oldState == Unit.PlayerStates.END_TURN_STATE.state && WargameCampaign.thisPlayer.state == Unit.PlayerStates.ACTIVE_STATE.state){
                        for(Unit u: WargameCampaignMain.units.values()){
                            u.resetTurn();
                            WargameCampaignMain.setupNewTurn();
                        }
                    }
                }
            }

            if (state == WargameCampaignMain.GameStates.DEPLOYMENT || state == WargameCampaignMain.GameStates.ACTIVE) {
                if(game.getScreen().getClass() != WargameCampaignMain.class) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            game.setScreen(new WargameCampaignMain(game));
                        }
                    });
                }
            }else if(state == WargameCampaignMain.GameStates.SETUP){
                WargameCampaignStart.groupMenu.init();
                Menu.showMenu(WargameCampaign.Menus.ADD_BATTLE_GROUPS_MENU);
            }
        }else if(gameData.task.equals("getGroups")){
            WargameCampaign.loadedGameData.battleGroups = gameData.battleGroups;

            AddBattleGroupsMenu.groupsJustReloaded = true;
        }
    }

    public void parseFriendData(Packets.PacketFriendTasks friendData){
        if(friendData.pending != null) {
            if (WargameCampaign.friendRequestList == null)
                WargameCampaign.friendRequestList = new Array<String>();
            WargameCampaign.friendRequestList = friendData.pending;

            WargameCampaign.refreshFriends = true;
        }
        if(friendData.friends != null) {
            if (WargameCampaign.friendList == null)
                WargameCampaign.friendList = new Array<String>();
            WargameCampaign.friendList = friendData.friends;

            WargameCampaign.refreshFriends = true;
        }
    }

    //message queue management and thread
    @Override
    public void run() {
        while(running){
            if(!connected){
                connect();
                continue;
            }
            try {
                checkQueue(Gdx.graphics.getDeltaTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void checkQueue(float delta) throws InterruptedException {
        if(queue.size > 0) {
            NetworkRequest nextRequest = queue.first();

            nextRequest.parse();

            queue.removeIndex(0);
            Thread.sleep(500);
        }

        if(WargameCampaign.thisPlayer != null && WargameCampaign.thisPlayer.state == Unit.PlayerStates.END_TURN_STATE.state){
            serverQueryDelta -= delta;

            //Utils.log(serverQueryDelta);

            if(serverQueryDelta <= 0){
                refreshGameState();

                serverQueryDelta = serverQueryDelay;
            }
        }
    }

    public static void newRequest(Packets.Packet p){
        new NetworkRequest(p);
    }
}
