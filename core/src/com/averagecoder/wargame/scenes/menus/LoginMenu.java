package com.averagecoder.wargame.scenes.menus;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.WargameCampaignMain;
import com.averagecoder.wargame.engine.NetworkController;
import com.averagecoder.wargame.engine.NetworkRequest;
import com.averagecoder.wargame.engine.Packets;
import com.averagecoder.wargame.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class LoginMenu extends Menu {

    public LoginMenu(Stage s, WargameCampaign wargameCampaign, Skin sk){
        super(wargameCampaign);

        stage = s;
        skin = sk;

        table.setSkin(skin);

        Menu.menus.put(WargameCampaign.Menus.LOGIN_MENU, this);
    }

    @Override
    public void init() {
        table.setVisible(false);
        table.setPosition((game.WIDTH / 2) - (table.getWidth() / 2), (game.HEIGHT / 2) - (table.getHeight() / 2));

        Label usernameLabel = new Label("Username: ", skin);
        Label passwordLabel = new Label("Password: ", skin);
        final TextField username = new TextField(WargameCampaign.username, skin);
        final TextField password = new TextField(WargameCampaign.password, skin);

        TextButton loginBtn = new TextButton("Login", skin);
        loginBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (username.getText().length() > 0 && password.getText().length() > 0) {
                    WargameCampaign.username = username.getText();
                    WargameCampaign.password = password.getText();

                    Packets.PacketSecurity login = new Packets.PacketSecurity();
                    login.username = username.getText();
                    login.password = password.getText();
                    login.task = "login";
                    login.message = "New login request: username = " + username;
                    NetworkController.newRequest(login);
                } else
                    Utils.log("please provide a username and password");
            }
        });

        TextButton registerBtn = new TextButton("Register", skin);
        registerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (username.getText().length() > 0 && password.getText().length() > 0) {
                    WargameCampaign.username = username.getText();
                    WargameCampaign.password = password.getText();

                    Packets.PacketSecurity registerRequest = new Packets.PacketSecurity();
                    registerRequest.username = username.getText();
                    registerRequest.password = password.getText();
                    registerRequest.task = "register";
                    registerRequest.message = "New registration: username = " + username;
                    NetworkController.newRequest(registerRequest);
                } else
                    Utils.log("please provide a username and password");
            }
        });

        table.add(usernameLabel).width(100).right();
        table.add(username).width(fieldWidth).left();
        table.row().fill().expandX();
        table.add(passwordLabel).width(100).padTop(10).right();
        table.add(password).width(fieldWidth).padTop(10).left();
        table.row().fill().expandX();
        table.add(loginBtn).width(fieldWidth).colspan(2).padTop(10);
        table.row().fill().expandX();
        table.add(registerBtn).width(fieldWidth).colspan(2).padTop(10);

        stage.addActor(table);
    }

    @Override
    public void update(float delta) {
        if(WargameCampaign.userid > 0)
            Menu.showMenu(WargameCampaign.Menus.MAIN_MENU);

        stage.act(delta);
    }

    @Override
    public void draw() {
        stage.draw();
    }
}
