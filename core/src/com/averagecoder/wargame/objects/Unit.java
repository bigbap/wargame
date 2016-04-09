package com.averagecoder.wargame.objects;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.WargameCampaignMain;
import com.averagecoder.wargame.engine.InputController;
import com.averagecoder.wargame.engine.data_types.BattleGroup;
import com.averagecoder.wargame.engine.data_types.Player;
import com.averagecoder.wargame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Unit extends Hexagon{
    public enum PlayerStates{
        INVITED_STATE(0),
        GROUP_STATE(1),
        DEPLOYMENT_STATE(2),
        ACTIVE_STATE(3),
        END_TURN_STATE(4);

        public final int state;

        PlayerStates(int state){
            this.state = state;
        }
    }

    Vector2 startPos = new Vector2();
    boolean active = false;
    boolean selected = false;
    int movesDone;
    int maxMoves;
    int visibilityRange;
    float animationTimer;
    int mainFrame;

    public int movesLeft;
    public Tile unitTile;
    public String unitID;
    public String unitName;
    public Array<Tile> path = new Array<Tile>();
    public boolean clearPath = false;
    public int playerID;
    public BattleGroup battleGroup;
    public boolean engaged = false;

    public Unit(WargameCampaign wargameCampaign, BattleGroup bg, int numF, Vector2 tileP, int fram){
        super(wargameCampaign, tileP);

        numFrames = numF;
        mainFrame = fram;
        frame = fram;
        playerID = bg.playerID;
        unitID = Integer.toString(bg.groupID);
        unitName = bg.groupName;
        maxMoves = bg.maxMoves;
        unitTile = Map.getTile(tilePos);
        unitTile.passable = false;
        unitTile.unitOnTile = this;
        visibilityRange = bg.visibility;
        battleGroup = bg;
        engaged = bg.engaged;

        resetAnimationTimer();
        resetTurn();
    }

    @Override
    public void update(float delta){}

    @Override
    public void draw(){
        if(visible)
            super.draw();
    }

    public void resetTurn(){
        if(WargameCampaign.thisPlayer.state == PlayerStates.ACTIVE_STATE.state) {
            movesLeft = maxMoves;
            movesDone = 0;
        }else
            movesLeft = 0;
    }

    public void toggleSelected(){
        selected = !selected;
        if(selected)
            highlight(true);
        else
            highlight(false);
    }
    public boolean isSelected(){
        return selected;
    }

    private void highlight(boolean highlight){
        frame = highlight ? 4 : mainFrame;
        active = highlight;
    }

    private void turnOffActiveUnit(){
        if(game.activeUnit != null)
            game.activeUnit.highlight(false);
    }

    private Vector2 getPixelPos(Vector2 tilePos){
        return Map.getTile(tilePos).pos;
    }

    private void resetAnimationTimer(){
        animationTimer = 0.1f;
    }

    public void highlightPath(){
        if(clearPath)
            Map.clearActiveTiles();

        for(Tile t: InputController.path) {
            if(t != null)
                t.highlight(true);
        }
    }

    public int calculateTotalMovesInPath(){
        int totalMoves = 0;
        for(Tile t: InputController.path){
            if(t != null)
                totalMoves += t.moves;
        }

        return totalMoves;
    }

    public void moveUnit(Tile nextTile){
        unitTile.passable = true;
        unitTile.active = false;

        battleGroup.path.add(tilePos);

        tilePos = nextTile.tilePos;
        setPos();

        battleGroup.tileX = (int)tilePos.x;
        battleGroup.tileY = (int)tilePos.y;

        unitTile.unitOnTile = null;
        unitTile = nextTile;
        unitTile.unitOnTile = this;
        unitTile.passable = false;

        collisionPoly = calcCollisionPoly();

        movesLeft -= nextTile.moves;
        movesDone += nextTile.moves;
    }

    public void deployUnit(Tile tile){
        unitTile.passable = true;

        tilePos = tile.tilePos;
        unitTile = tile;
        pos = getPixelPos(tilePos);
        collisionPoly = calcCollisionPoly();

        tile.passable = false;
    }

    private void drawMovesLeft() {
        WargameCampaign.font.draw(batch, Integer.toString(movesLeft), pos.x + 8, pos.y + 20);
    }

    public int getMovesLeft(){
        return movesLeft;
    }

    public int getMaxMoves(){
        return maxMoves;
    }

    public int getVisibilityRange(){
        return visibilityRange;
    }

    public Vector2 getTilePos(){
        return tilePos;
    }

    public void centerUnit(){
        game.camera.position.x = pos.x + (width / 2);
        game.camera.position.y = pos.y + (height / 2);
        game.checkForOutOfBounds();
        game.camera.update();
    }

    public void setPos(){
        pos = getPixelPos(tilePos);
    }
}
