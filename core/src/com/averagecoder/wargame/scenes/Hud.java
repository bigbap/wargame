package com.averagecoder.wargame.scenes;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.WargameCampaignMain;
import com.averagecoder.wargame.objects.Map;
import com.averagecoder.wargame.objects.ObjectFactory;
import com.averagecoder.wargame.objects.Unit;
import com.averagecoder.wargame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Hud {
    public Stage stage;

    Viewport viewport;
    WargameCampaign game;
    TextButton toggleAnimationBtn;
    Skin skin;
    Window infoBoxCont;

    static Table table;
    static TextButton endState;

    public Hud(SpriteBatch sb, WargameCampaign wargameCampaign){
        game = wargameCampaign;

        skin = WargameCampaign.skin;
        viewport = new ExtendViewport(game.WIDTH, game.HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        WargameCampaign.multiplexer.addProcessor(stage);

        table = new Table(skin);
        table.bottom();
        table.setFillParent(true);

        endState = new TextButton("End Deployment", skin);
        endState.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(WargameCampaignMain.state == WargameCampaignMain.GameStates.DEPLOYMENT)
                    WargameCampaignMain.endDeployment();
                else if(WargameCampaignMain.state == WargameCampaignMain.GameStates.ACTIVE)
                    WargameCampaignMain.newTurn();
            }
        });

        String toggleStr = Map.animatePath ? "ON" : "OFF";
        toggleAnimationBtn = new TextButton("Path Animation Toggle: " + toggleStr, skin);
        toggleAnimationBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Map.animatePath = !Map.animatePath;
                String toggleStr = Map.animatePath ? "ON" : "OFF";
                toggleAnimationBtn.setText("Path Animation Toggle: " + toggleStr);
            }
        });

        infoBoxCont = new Window("Battle Group Info", skin);
        infoBoxCont.setWidth(200.0f);
        infoBoxCont.setVisible(false);
        infoBoxCont.setPosition(game.WIDTH - infoBoxCont.getWidth() - 10, 10);

        resetHud();
        table.add(endState);
        table.add(toggleAnimationBtn).padLeft(5);

        stage.addActor(table);
        stage.addActor(infoBoxCont);
    }

    public void addInfoBox(Unit unit){
        infoBoxCont.clear();

        Label unitID = new Label("ID: " + unit.unitID, skin);
        Label maxMoves = new Label("Maximum moves: " + Integer.toString(unit.getMaxMoves()), skin);
        Label movesLeft = new Label("Moves left: " + Integer.toString(unit.getMovesLeft()), skin);
        Label visRange = new Label("Visibility range: " + Integer.toString(unit.getVisibilityRange()), skin);
        Label tilePos = new Label("Tile Position: [" + Integer.toString((int)unit.getTilePos().x) + "," + Integer.toString((int)unit.getTilePos().y) + "]", skin);
        Label engaged = new Label("Engaged: " + Boolean.toString(unit.engaged), skin);

        infoBoxCont.add(unitID).minWidth(150).expandX().fillX();
        infoBoxCont.row().fill().expandX();
        infoBoxCont.add(maxMoves).minWidth(150).expandX().fillX();
        infoBoxCont.row().fill().expandX();
        infoBoxCont.add(movesLeft).minWidth(150).expandX().fillX();
        infoBoxCont.row().fill().expandX();
        infoBoxCont.add(visRange).minWidth(150).expandX().fillX();
        infoBoxCont.row().fill().expandX();
        infoBoxCont.add(tilePos).minWidth(150).expandX().fillX();
        infoBoxCont.row().fill().expandX();
        infoBoxCont.add(engaged).minWidth(150).expandX().fillX();
        infoBoxCont.setVisible(true);
    }
    public void removeInfoBox(){
        infoBoxCont.setVisible(false);
    }

    public static void resetHud(){
        if(WargameCampaignMain.state == WargameCampaignMain.GameStates.ACTIVE)
            endState.setText("End Turn");
        else if(WargameCampaignMain.state == WargameCampaignMain.GameStates.DEPLOYMENT)
            endState.setText("End Deployment");
    }

    public void dispose(){
        stage.dispose();
    }
}
