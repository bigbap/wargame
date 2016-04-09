package com.averagecoder.wargame;

import com.averagecoder.wargame.engine.InputController;
import com.averagecoder.wargame.engine.NetworkController;
import com.averagecoder.wargame.engine.Packets;
import com.averagecoder.wargame.engine.data_types.BattleGroup;
import com.averagecoder.wargame.objects.Map;
import com.averagecoder.wargame.objects.ObjectFactory;
import com.averagecoder.wargame.objects.Tile;
import com.averagecoder.wargame.objects.Unit;
import com.averagecoder.wargame.scenes.Hud;
import com.averagecoder.wargame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class WargameCampaignMain extends ScreenAdapter {

    public enum GameStates{
        SETUP,
        DEPLOYMENT,
        ACTIVE,
        END_TURN
    }

    static ObjectFactory objFactory;
    static WargameCampaign game;
    SpriteBatch batch;
    OrthographicCamera camera;

    public static int gameID;
    public static GameStates state;
    public static HashMap<Integer, Unit> units = new HashMap<Integer, Unit>();

    public WargameCampaignMain(WargameCampaign wargameCampaign){
        game = wargameCampaign;
        batch = game.batch;
        camera = game.camera;
        game.objFactory = new ObjectFactory(game);
        objFactory = game.objFactory;
        state = WargameCampaign.loadedGameData.gameState;
        gameID = WargameCampaign.loadedGameData.gameID;

        WargameCampaign.multiplexer = new InputMultiplexer();
        game.hud = new Hud(batch, game);
        WargameCampaign.inputController = new GestureDetector(new InputController(game));
        WargameCampaign.multiplexer.addProcessor(WargameCampaign.inputController);
        Gdx.input.setInputProcessor(WargameCampaign.multiplexer);

        if(WargameCampaign.loadedGameData != null && WargameCampaign.loadedGameData.players.size() > 0){
            //load game
            objFactory.addSpritesheet("hexTiles" + game.TILE_SIZE);
            game.map = new Map(game, "hexTiles" + game.TILE_SIZE, "data/tilekey.json");
            objFactory.addObject(game.map, "hexTiles" + game.TILE_SIZE, true);

            objFactory.addSpritesheet("unit" + game.TILE_SIZE);

            float depX = WargameCampaign.thisPlayer.deploymentTileX;
            float depY = WargameCampaign.thisPlayer.deploymentTileY;
            for(BattleGroup bg: WargameCampaign.loadedGameData.battleGroups){
                Vector2 tileP = new Vector2(0, 0);
                if(WargameCampaign.thisPlayer.state > 2) {
                    tileP.x = bg.tileX;
                    tileP.y = bg.tileY;
                }else if(WargameCampaign.thisPlayer.state == 2){
                    tileP.x = depX;
                    tileP.y = depY;
                    Map.calculateDeployableTiles(tileP);
                }else
                    break;

                if((WargameCampaignMain.state == GameStates.DEPLOYMENT && WargameCampaign.userid == bg.playerID) || WargameCampaignMain.state != GameStates.DEPLOYMENT) {
                    Unit thisUnit = new Unit(game, bg, 5, tileP, WargameCampaign.loadedGameData.players.get(bg.playerID).frame);
                    units.put(bg.groupID, thisUnit);
                    if (objFactory.addObject(thisUnit, "unit" + game.TILE_SIZE, true) != 0)
                        System.out.println("There was a problem loading game object.");

                    thisUnit.unitTile.passable = false;
                    if(WargameCampaign.userid == bg.playerID)
                        thisUnit.centerUnit();
                }

                Array<Tile> tempArr = Map.getNeighbors(tileP);
                if(tempArr.size > 0) {
                    depX = Map.getNeighbors(tileP).get(0).tilePos.x;
                    depY = Map.getNeighbors(tileP).get(0).tilePos.y;
                }
            }
            for(Unit u: units.values())
                u.unitTile.passable = true;
            Map.calculateVisibleTiles();
            Map.calculateUnitVisibility();
        }
    }

    @Override
    public void render(float delta){
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        InputController.update(delta);
        update(delta);
        draw();
    }

    @Override
    public void dispose(){
        game.hud.dispose();
    }

    @Override
    public void resize(int width, int height){
        game.viewport.update(width, height);
        game.hud.stage.getViewport().update(width, height);
    }

    private void update(float delta){
        GameStates oldState = state;
        state = WargameCampaign.loadedGameData.gameState;
        if(!state.equals(oldState))
            Hud.resetHud();

        objFactory.updateObjects(delta);
    }

    private void draw(){
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        objFactory.drawObjects();
        batch.end();

        batch.setProjectionMatrix(game.hud.stage.getCamera().combined);
        game.hud.stage.draw();
    }

    public static void newTurn(){
        WargameCampaign.thisPlayer.state = Unit.PlayerStates.END_TURN_STATE.state;

        Packets.PacketGameData gameEndTurn = new Packets.PacketGameData();
        for(Unit u: WargameCampaignMain.units.values()){
            if(u.playerID == WargameCampaign.userid) {
                gameEndTurn.battleGroups.add(u.battleGroup);
            }

            u.resetTurn();
        }
        gameEndTurn.task = "gameEndTurn";
        gameEndTurn.gameID = gameID;
        gameEndTurn.playerID = WargameCampaign.userid;
        gameEndTurn.message = "New end turn request: playerID = " + WargameCampaign.userid + ", gameID = " + gameID;
        NetworkController.newRequest(gameEndTurn);
    }

    public static void endDeployment(){
        Map.clearDeployableTiles();

        Packets.PacketGameTasks readyGame = new Packets.PacketGameTasks();
        readyGame.gameID = gameID;
        readyGame.userid = WargameCampaign.userid;
        readyGame.task = "deplymentDoneRequest";
        readyGame.message = "Deployment done game request: gameID = " + gameID;
        for(Unit u: WargameCampaignMain.units.values()){
            BattleGroup thisBG = new BattleGroup();
            thisBG.tileX = (int)u.tilePos.x;
            thisBG.tileY = (int)u.tilePos.y;
            thisBG.groupID = Integer.parseInt(u.unitID);
            readyGame.battleGroups.add(thisBG);
        }
        NetworkController.newRequest(readyGame);

        Hud.resetHud();
    }

    public static void setupNewTurn(){
        for(BattleGroup bg: WargameCampaign.loadedGameData.battleGroups){
            Unit thisUnit = units.get(bg.groupID);
            thisUnit.tilePos = new Vector2(bg.tileX, bg.tileY);
            thisUnit.setPos();
            thisUnit.engaged = bg.engaged;
        }

        Map.calculateVisibleTiles();
        Map.calculateUnitVisibility();
    }

}
