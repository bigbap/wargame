package com.averagecoder.wargame;

import com.averagecoder.wargame.scenes.menus.AddBattleGroupsMenu;
import com.averagecoder.wargame.scenes.menus.FriendMenu;
import com.averagecoder.wargame.scenes.menus.LoadGameMenu;
import com.averagecoder.wargame.scenes.menus.LoginMenu;
import com.averagecoder.wargame.scenes.menus.MainMenu;
import com.averagecoder.wargame.scenes.menus.Menu;
import com.averagecoder.wargame.scenes.menus.NewGameMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class WargameCampaignStart extends ScreenAdapter {

    public Stage stage;

    WargameCampaign game;
    Skin skin;

    public static LoginMenu loginMenu;
    public static MainMenu mainMenu;
    public static NewGameMenu newGameMenu;
    public static LoadGameMenu loadGameMenu;
    public static FriendMenu friendMenu;
    public static AddBattleGroupsMenu groupMenu;

    public BitmapFont font;

    public WargameCampaignStart(WargameCampaign wargameCampaign){
        game = wargameCampaign;

        WargameCampaign.username = WargameCampaign.prefs.getString("username", null);
        WargameCampaign.password = WargameCampaign.prefs.getString("password", null);

        skin = WargameCampaign.skin;

        stage = new Stage(game.viewport, game.batch);

        WargameCampaign.multiplexer = new InputMultiplexer();
        WargameCampaign.multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(WargameCampaign.multiplexer);

        loginMenu = new LoginMenu(stage, game, skin);
        loginMenu.init();
        Menu.showMenu(WargameCampaign.Menus.LOGIN_MENU);

        mainMenu = new MainMenu(stage, game, skin);
        mainMenu.init();

        groupMenu = new AddBattleGroupsMenu(stage, game, skin);

        newGameMenu = null;
        loadGameMenu = null;
        friendMenu = null;
    }

    @Override
    public void render(float delta){
        Gdx.gl20.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(stage.getCamera().combined);

        update(delta);
        draw();
    }

    @Override
    public void dispose(){
        stage.dispose();
    }

    @Override
    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

    private void update(float delta){
        Menu.activeMenu.update(delta);
    }

    private void draw(){
        Menu.activeMenu.draw();
    }

}
