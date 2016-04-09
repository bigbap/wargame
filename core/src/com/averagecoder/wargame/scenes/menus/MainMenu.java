package com.averagecoder.wargame.scenes.menus;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.WargameCampaignStart;
import com.averagecoder.wargame.engine.data_types.LoadedGameData;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MainMenu extends Menu {

    public MainMenu(Stage s, WargameCampaign wargameCampaign, Skin sk){
        super(wargameCampaign);

        stage = s;
        skin = sk;

        table.setSkin(skin);

        Menu.menus.put(WargameCampaign.Menus.MAIN_MENU, this);
    }

    @Override
    public void init(){
        table.setVisible(false);
        table.setPosition((game.WIDTH / 2) - (table.getWidth() / 2), (game.HEIGHT / 2) - (table.getHeight() / 2));

        TextButton newGameBtn = new TextButton("New Game", skin);
        newGameBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (WargameCampaignStart.newGameMenu == null) {
                    WargameCampaignStart.newGameMenu = new NewGameMenu(stage, game, skin);
                    WargameCampaignStart.newGameMenu.init();
                }

                Menu.showMenu(WargameCampaign.Menus.NEW_GAME_MENU);
            }
        });
        TextButton loadGameBtn = new TextButton("Load Game", skin);
        loadGameBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(WargameCampaignStart.loadGameMenu == null){
                    WargameCampaignStart.loadGameMenu = new LoadGameMenu(stage, game, skin);
                    WargameCampaignStart.loadGameMenu.init();
                }

                Menu.showMenu(WargameCampaign.Menus.LOAD_GAME_MENU);
            }
        });
        TextButton addFriendBtn = new TextButton("Friends", skin);
        addFriendBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(WargameCampaignStart.friendMenu == null){
                    WargameCampaignStart.friendMenu = new FriendMenu(stage, game, skin);
                    WargameCampaignStart.friendMenu.init();
                }

                Menu.showMenu(WargameCampaign.Menus.FRIEND_MENU);
            }
        });

        TextButton logoutBtn = new TextButton("Logout", skin);
        logoutBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                WargameCampaign.userid = 0;
                WargameCampaign.username = null;
                WargameCampaign.password = null;
                WargameCampaign.gameList = null;
                WargameCampaign.pendingGameList = null;
                WargameCampaign.friendList = null;
                WargameCampaign.friendRequestList = null;
                WargameCampaign.thisPlayer = null;
                WargameCampaign.loadedGameData = new LoadedGameData();
                WargameCampaign.networkController.connected = false;

                Menu.showMenu(WargameCampaign.Menus.LOGIN_MENU);
            }
        });

        table.add(newGameBtn).width(fieldWidth);
        table.row().fill().expandX();
        table.add(loadGameBtn).width(fieldWidth).padTop(10);
        table.row().fill().expandX();
        table.add(addFriendBtn).width(fieldWidth).padTop(10);
        table.row().fill().expandX();
        table.add(logoutBtn).width(fieldWidth).padTop(20);

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
}
