package com.averagecoder.wargame.engine;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.WargameCampaignMain;
import com.averagecoder.wargame.objects.Map;
import com.averagecoder.wargame.objects.Tile;
import com.averagecoder.wargame.objects.Unit;
import com.averagecoder.wargame.utils.Utils;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class InputController implements GestureListener {
    static WargameCampaign game;

    public enum InputState{
        IDLE,
        UNIT_SELECTED,
        CALCULATE_PATH,
        MOVE_UNIT,
        DEPLOY_UNIT
    }

    public static InputState state;
    public static Unit unitSelected;
    public static Array<Tile> path = new Array<Tile>();
    public static Tile startTile = null;
    public static Tile endTile = null;
    static float animationTimer;

    public InputController(WargameCampaign wargameCampaign){
        game = wargameCampaign;
        state = InputState.IDLE;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Vector3 v3 = new Vector3(x,y,0);
        game.camera.unproject(v3);

        Tile tile = Map.getTileAtPos(v3.x, v3.y);

        if(tile != null) {
            if(tile.unitOnTile != null){
                if(unitSelected == null){
                    unitSelected = tile.unitOnTile;
                    unitSelected.toggleSelected();

                    game.hud.addInfoBox(unitSelected);

                    state = InputState.UNIT_SELECTED;
                }else if(unitSelected != tile.unitOnTile){
                    unitSelected.toggleSelected();
                    unitSelected = tile.unitOnTile;
                    unitSelected.toggleSelected();

                    game.hud.addInfoBox(unitSelected);

                    state = InputState.UNIT_SELECTED;
                }else{
                    unitSelected.toggleSelected();
                    unitSelected = null;

                    game.hud.removeInfoBox();

                    state = InputState.IDLE;
                }

            }else if(state == InputState.UNIT_SELECTED && unitSelected != null && unitSelected.playerID == WargameCampaign.userid && !unitSelected.engaged){
                if(WargameCampaignMain.state == WargameCampaignMain.GameStates.ACTIVE && unitSelected.movesLeft > 0) {
                    startTile = unitSelected.unitTile;
                    endTile = tile;

                    path.clear();
                    Map.findPath(startTile, endTile);

                    while (unitSelected.calculateTotalMovesInPath() > unitSelected.movesLeft)
                        path.pop();

                    unitSelected.highlightPath();

                    state = InputState.CALCULATE_PATH;
                }else if(WargameCampaignMain.state == WargameCampaignMain.GameStates.DEPLOYMENT){
                    if(tile.deployable) {
                        path.clear();
                        path.add(tile);

                        state = InputState.DEPLOY_UNIT;
                    }
                }
            }/*else if(state == InputState.CALCULATE_PATH && unitSelected != null){
                state = InputState.MOVE_UNIT;
            }*/

            if(state == InputState.CALCULATE_PATH && unitSelected != null){
                state = InputState.MOVE_UNIT;
            }
        }

        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        game.camera.translate(-deltaX, deltaY);
        game.checkForOutOfBounds();
        game.camera.update();

        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    public static void update(float delta){
        if(state == InputState.MOVE_UNIT && unitSelected != null && path.size > 0){

            animationTimer -= delta;

            if (animationTimer <= 0) {

                unitSelected.moveUnit(path.get(0));
                path.removeIndex(0);

                if (path.size == 0) {
                    Map.calculateVisibleTiles();
                    game.hud.addInfoBox(unitSelected);
                    unitSelected.unitTile.highlight(false);
                    state = InputState.UNIT_SELECTED;
                }

                resetAnimationTimer();
            }
        }else if(state == InputState.DEPLOY_UNIT && unitSelected != null && path.size > 0){
            unitSelected.deployUnit(path.get(0));
            path.removeIndex(0);

            Map.calculateVisibleTiles();

            state = InputState.UNIT_SELECTED;
        }
    }

    private static void resetAnimationTimer(){
        animationTimer = 0.1f;
    }
}
