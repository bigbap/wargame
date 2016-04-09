package com.averagecoder.wargame.scenes.menus;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.engine.NetworkController;
import com.averagecoder.wargame.engine.Packets;
import com.averagecoder.wargame.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class FriendMenu extends Menu {
    Table pendingFriends;
    float friendRefreshTimer = 15;

    public FriendMenu(Stage s, WargameCampaign wargameCampaign, Skin sk){
        super(wargameCampaign);

        stage = s;
        skin = sk;

        table.setSkin(skin);

        pendingFriends = new Table(skin);

        Menu.menus.put(WargameCampaign.Menus.FRIEND_MENU, this);
    }

    @Override
    public void init() {
        table.setVisible(false);
        table.setPosition((game.WIDTH / 2) - (table.getWidth() / 2), (game.HEIGHT / 2) - (table.getHeight() / 2));

        Label usernameLabel = new Label("Username: ", skin);
        final TextField username = new TextField("", skin);

        TextButton addFriendBtn = new TextButton("Send Request", skin);
        addFriendBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Packets.PacketFriendTasks friendTask = new Packets.PacketFriendTasks();
                friendTask.userid = WargameCampaign.userid;
                friendTask.username = username.getText();
                friendTask.task = "friendRequest";
                friendTask.message = "New friend request: username = " + username;
                NetworkController.newRequest(friendTask);
            }
        });

        TextButton backBtn = new TextButton("Back to Main Menu", skin);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Menu.showMenu(WargameCampaign.Menus.MAIN_MENU);
            }
        });

        loadPendingRequests();

        table.add(usernameLabel).width(100).right();
        table.add(username).width(fieldWidth).left();
        table.row().fill().expandX();
        table.add(addFriendBtn).colspan(2).width(fieldWidth).padTop(10);
        table.row().fill().expandX();
        table.add(pendingFriends).colspan(2).padTop(10);
        table.row().fill().expandX();
        table.row().fill().expandX();
        table.add(backBtn).colspan(2).width(fieldWidth).padTop(10);

        stage.addActor(table);
    }

    @Override
    public void update(float delta) {
        friendRefreshTimer -= delta;

        if(friendRefreshTimer <= 0){
            // load friend request list
            WargameCampaign.networkController.refreshPendingFriendsList();

            friendRefreshTimer = 15;
        }

        if(WargameCampaign.refreshFriends)
            loadPendingRequests();

        stage.act(delta);
    }

    @Override
    public void draw() {
        stage.draw();
    }

    public void loadPendingRequests(){
        WargameCampaign.refreshFriends = false;
        pendingFriends.clear();

        if(WargameCampaign.friendRequestList == null)
            return;
        if(WargameCampaign.friendRequestList.size == 0)
            return;

        Label label = new Label("Pending Requests", skin);
        label.setAlignment(2);
        pendingFriends.add(label).colspan(3);

        for(String request: WargameCampaign.friendRequestList){
            String[] splitArray = request.split("::");

            final int friendid = Integer.parseInt(splitArray[0]);
            String friendUsername = splitArray[2];

            Label friendLabel = new Label(friendUsername + ": ", skin);
            TextButton acceptBtn = new TextButton("Accept", skin);
            acceptBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Packets.PacketFriendTasks friendTask = new Packets.PacketFriendTasks();
                    //these fields are swapped because the user accepting the request is always the friend
                    friendTask.userid = friendid;
                    friendTask.friendid = WargameCampaign.userid;
                    friendTask.task = "friendAccept";
                    friendTask.message = "New friend accepted: friendid = " + friendid;
                    NetworkController.newRequest(friendTask);
                }
            });
            TextButton declineBtn = new TextButton("Decline", skin);
            declineBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Packets.PacketFriendTasks friendTask = new Packets.PacketFriendTasks();
                    //these fields are swapped because the user declining the request is always the friend
                    friendTask.userid = friendid;
                    friendTask.friendid = WargameCampaign.userid;
                    friendTask.task = "friendDecline";
                    friendTask.message = "New friend declined: friendid = " + friendid;
                    NetworkController.newRequest(friendTask);
                }
            });

            pendingFriends.row().fill().expandX();
            pendingFriends.add(friendLabel).width(100).padTop(10).right();
            pendingFriends.add(acceptBtn).width(75).padTop(10).left();
            pendingFriends.add(declineBtn).width(75).padTop(10).left();
        }
    }
}
