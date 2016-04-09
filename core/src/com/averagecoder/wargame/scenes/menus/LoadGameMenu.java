package com.averagecoder.wargame.scenes.menus;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.WargameCampaignMain;
import com.averagecoder.wargame.engine.NetworkController;
import com.averagecoder.wargame.engine.Packets;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.HashMap;

public class LoadGameMenu extends Menu {

    Table gameTable;

    public LoadGameMenu(Stage s, WargameCampaign wargameCampaign, Skin sk){
        super(wargameCampaign);

        stage = s;
        skin = sk;

        table.setSkin(skin);
        gameTable = new Table(skin);

        Menu.menus.put(WargameCampaign.Menus.LOAD_GAME_MENU, this);
    }

    @Override
    public void init() {
        table.setVisible(false);
        table.setPosition((game.WIDTH / 2) - (table.getWidth() / 2), (game.HEIGHT / 2) - (table.getHeight() / 2));

        Label label = new Label("Load Game", skin);
        label.setAlignment(2);
        table.add(label).width(150).center();
        table.row().fill().expandX();
        table.add(gameTable);

        loadGameList();

        TextButton backBtn = new TextButton("Back to Main Menu", skin);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Menu.showMenu(WargameCampaign.Menus.MAIN_MENU);
            }
        });

        table.row().fill().expandX();
        table.add(backBtn).width(fieldWidth).padTop(20);

        stage.addActor(table);
    }

    @Override
    public void update(float delta) {
        stage.act(delta);
    }

    @Override
    public void draw() {
        stage.draw();
    }

    private void loadGameList(){
        if(WargameCampaign.gameList == null)
            return;

        for(Integer key: WargameCampaign.gameList.keySet()){
            final int gameID = key;

            TextButton gameBtn = new TextButton("Load " + WargameCampaign.gameList.get(key)[0], skin);
            gameBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Packets.PacketGameTasks loadGame = new Packets.PacketGameTasks();
                    loadGame.gameID = gameID;
                    loadGame.userid = WargameCampaign.userid;
                    loadGame.task = "loadGameRequest";
                    loadGame.message = "Load game request: gameID = " + gameID;
                    NetworkController.newRequest(loadGame);
                }
            });

            gameTable.add(gameBtn).width(fieldWidth).padTop(10);
            gameTable.row().fill().expandX();
        }
    }
}
