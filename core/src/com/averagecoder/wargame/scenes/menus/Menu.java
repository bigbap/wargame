package com.averagecoder.wargame.scenes.menus;

import com.averagecoder.wargame.WargameCampaign;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.HashMap;

public abstract class Menu {
    Stage stage;
    Table table;
    Skin skin;
    WargameCampaign game;

    float window_width;
    float window_height;

    int fieldWidth = 150;

    static HashMap<WargameCampaign.Menus, Menu> menus = new HashMap<WargameCampaign.Menus, Menu>();
    public static Menu activeMenu;

    public Menu(){}

    public Menu(WargameCampaign game){
        this.game = game;

        table = new Table();

        window_width = game.WIDTH;
        window_height = game.HEIGHT;
    }

    public static void showMenu(WargameCampaign.Menus menu){
        for(WargameCampaign.Menus key: menus.keySet()){
            if(!menu.equals(key)){
                menus.get(key).hide();
            }
        }

        menus.get(menu).show();
        activeMenu = menus.get(menu);
    }

    public abstract void init();
    public abstract void update(float delta);
    public abstract void draw();

    public void show(){
        table.setVisible(true);
    }

    public void hide(){
        table.setVisible(false);
    }
}
