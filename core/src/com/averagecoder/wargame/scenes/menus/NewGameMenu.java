package com.averagecoder.wargame.scenes.menus;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.engine.NetworkController;
import com.averagecoder.wargame.engine.Packets;
import com.averagecoder.wargame.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class NewGameMenu extends Menu {

    Array<String> otherPlayers = new Array<String>();
    Table pendingTable;

    public NewGameMenu(Stage s, WargameCampaign wargameCampaign, Skin sk){
        super(wargameCampaign);

        stage = s;
        skin = sk;

        table.setSkin(skin);
        pendingTable = new Table(skin);

        Menu.menus.put(WargameCampaign.Menus.NEW_GAME_MENU, this);
    }

    @Override
    public void init() {
        table.setVisible(false);
        table.setPosition((game.WIDTH / 2) - (table.getWidth() / 2), (game.HEIGHT / 2) - (table.getHeight() / 2));

        Label newGameLabel = new Label("Create new Game", skin);
        newGameLabel.setAlignment(2);

        Label gameNameLabel = new Label("Game Name: ", skin);
        final TextField gameName = new TextField("", skin);

        Label gamePointsLabel = new Label("Total Points: ", skin);
        final TextField gamePoints = new TextField("", skin);

        Label gameMapLabel = new Label("Game Map: ", skin);

        Array<String> maps = new Array<String>();
        for(String val: WargameCampaign.mapList.values()){
            maps.add(val);
        }
        final SelectBox<String> gameMap = new SelectBox<String>(skin);
        if(maps.size > 0)
            gameMap.setItems(maps);

        TextButton newGameBtn = new TextButton("Create New Game", skin);
        newGameBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int mapid = 0;
                String mapName = gameMap.getSelected();
                for(int id: WargameCampaign.mapList.keySet()){
                    if(WargameCampaign.mapList.get(id).equals(mapName)){
                        mapid = id;
                        break;
                    }
                }

                Packets.PacketGameTasks newGame = new Packets.PacketGameTasks();
                newGame.gameName = gameName.getText();
                newGame.userid = WargameCampaign.userid;
                newGame.points = Integer.parseInt(gamePoints.getText());
                newGame.mapid = mapid;
                newGame.otherUsers = otherPlayers;
                newGame.task = "newGameRequest";
                newGame.message = "New game request: userid = " + WargameCampaign.userid;
                NetworkController.newRequest(newGame);
            }
        });

        TextButton backBtn = new TextButton("Back to Main Menu", skin);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Menu.showMenu(WargameCampaign.Menus.MAIN_MENU);
            }
        });

        table.add(pendingTable).colspan(2);

        loadPending();

        table.row().fill().expandX();
        table.add(newGameLabel).align(2).colspan(2).padTop(20);
        table.row().fill().expandX();
        table.add(gameNameLabel).width(100).padTop(10).padRight(5);
        table.add(gameName).width(fieldWidth).padTop(10);
        table.row().fill().expandX();
        table.add(gamePointsLabel).width(100).padTop(10).padRight(5);
        table.add(gamePoints).width(fieldWidth).padTop(10);
        table.row().fill().expandX();
        table.add(gameMapLabel).width(100).padTop(10).padRight(5);
        table.add(gameMap).width(fieldWidth).padTop(10);

        loadFriends();

        table.row().fill().expandX();
        table.add(newGameBtn).width(fieldWidth).colspan(2).padTop(10);
        table.row().fill().expandX();
        table.add(backBtn).width(fieldWidth).colspan(2).padTop(10);

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

    private void loadFriends(){
        if(WargameCampaign.friendList == null || WargameCampaign.friendList.size == 0)
            return;

        Label gameFriendsLabel = new Label("Friends to Invite: ", skin);
        table.row().fill().expandX();
        table.add(gameFriendsLabel).width(fieldWidth).colspan(2).padTop(10);
        for(String username: WargameCampaign.friendList){
            String[] fields = username.split("::");
            final CheckBox checkBox = new CheckBox(fields[2], skin);
            checkBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(checkBox.isChecked())
                        otherPlayers.add(checkBox.getLabel().getText().toString());
                    else
                        otherPlayers.removeValue(checkBox.getLabel().getText().toString(), false);
                }
            });

            table.row().fill().expandX();
            table.add(checkBox).padTop(10).center();
        }
    }

    private void loadPending(){
        if(WargameCampaign.pendingGameList == null || WargameCampaign.pendingGameList.size() == 0)
            return;

        Label pendingLabel = new Label("Pending Game Requests", skin);
        pendingLabel.setAlignment(2);

        pendingTable.add(pendingLabel).align(2).colspan(3);
        pendingTable.row().fill().expandX();

        for(final Integer gameID : WargameCampaign.pendingGameList.keySet()){
            String[] gameData = WargameCampaign.pendingGameList.get(gameID);

            String gameName = gameData[0];
            String gamePoints = gameData[1];
            String mapName = gameData[2];

            TextField gameLabel = new TextField("game name: " + gameName + ", points value: " + gamePoints + ", map: " + mapName, skin);
            gameLabel.setDisabled(true);

            TextButton acceptBtn = new TextButton("Accept", skin);
            acceptBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Packets.PacketGameTasks acceptGame = new Packets.PacketGameTasks();
                    acceptGame.gameID = gameID;
                    acceptGame.userid = WargameCampaign.userid;
                    acceptGame.task = "acceptGameRequest";
                    acceptGame.message = "Accept game request: gameID = " + gameID;
                    NetworkController.newRequest(acceptGame);
                }
            });

            TextButton declineBtn = new TextButton("Decline", skin);
            declineBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Packets.PacketGameTasks declineGame = new Packets.PacketGameTasks();
                    declineGame.gameID = gameID;
                    declineGame.userid = WargameCampaign.userid;
                    declineGame.task = "declineGameRequest";
                    declineGame.message = "Decline game request: gameID = " + gameID;
                    NetworkController.newRequest(declineGame);
                }
            });

            pendingTable.add(gameLabel).padTop(5).padRight(5).width(500);
            pendingTable.add(acceptBtn).width(100).padTop(5).padRight(5);
            pendingTable.add(declineBtn).width(100).padTop(5);
            pendingTable.row().fill().expandX();

        }
    }
}
