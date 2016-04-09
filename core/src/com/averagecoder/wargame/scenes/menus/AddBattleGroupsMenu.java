package com.averagecoder.wargame.scenes.menus;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.WargameCampaignStart;
import com.averagecoder.wargame.engine.NetworkController;
import com.averagecoder.wargame.engine.Packets;
import com.averagecoder.wargame.engine.data_types.BattleGroup;
import com.averagecoder.wargame.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Select;

public class AddBattleGroupsMenu extends Menu {

    Table battleGroups;
    Table addGroupTable;
    int btnWidth = 50;
    Label totalPointsLeft;
    int columns;
    float refreshTimer = 15.0f;

    final TextField groupNameAdd;
    final SelectBox<Integer> groupMovesAdd;
    final SelectBox<Integer> groupVisibilityAdd;
    final SelectBox<Integer> groupPointsAdd;
    TextButton addBtn;
    TextButton readyBtn;
    Label groupActionLabel;
    Label gameMsg;
    Array<Integer> movesVisList = new Array<Integer>(new Integer[]{2,4,6,8,10,12});
    Array<Integer> pointsIncrements = new Array<Integer>();

    BattleGroup battleGroup;

    int pointsLeft = WargameCampaign.loadedGameData.points;

    public static boolean groupsJustReloaded = false;

    public AddBattleGroupsMenu(Stage s, WargameCampaign wargameCampaign, Skin sk){
        super(wargameCampaign);

        stage = s;
        skin = sk;

        battleGroup = new BattleGroup();

        table.setSkin(skin);

        groupNameAdd = new TextField("", skin);
        groupMovesAdd = new SelectBox<Integer>(skin);
        groupVisibilityAdd = new SelectBox<Integer>(skin);
        groupPointsAdd = new SelectBox<Integer>(skin);

        Menu.menus.put(WargameCampaign.Menus.ADD_BATTLE_GROUPS_MENU, this);
    }

    @Override
    public void init() {
        if(WargameCampaign.thisPlayer != null && WargameCampaign.thisPlayer.state > 1)
            columns = 4;
        else
            columns = 5;

        table.clear();
        table.top();

        table.setPosition((game.WIDTH / 2) - (table.getWidth() / 2), game.HEIGHT - table.getHeight() - 50);
        table.setVisible(true);

        table.top();
        battleGroups = new Table(skin);
        addGroupTable = new Table(skin);

        int pointsTot = 0;
        for(BattleGroup bg: WargameCampaign.loadedGameData.battleGroups){
            if(bg.playerID == WargameCampaign.userid)
                pointsTot += bg.points;
        }
        pointsLeft = WargameCampaign.loadedGameData.points - pointsTot;
        totalPointsLeft = new Label("Points Left: " + Integer.toString(pointsLeft), skin);

        Label groupNameLabel = new Label("Name", skin);
        Label groupMovesLabel = new Label("Max Moves", skin);
        Label groupVisibilityLabel = new Label("Visibility", skin);
        Label groupPointsLabel = new Label("Points Value", skin);
        groupActionLabel = new Label("Action", skin);

        groupNameLabel.setAlignment(2);
        groupMovesLabel.setAlignment(2);
        groupVisibilityLabel.setAlignment(2);
        groupPointsLabel.setAlignment(2);
        groupActionLabel.setAlignment(2);

        groupMovesAdd.setItems(movesVisList);
        groupVisibilityAdd.setItems(movesVisList);

        pointsIncrements.clear();
        for(int i = 500; i <= pointsLeft; i += 500)
            pointsIncrements.add(i);
        groupPointsAdd.setItems(pointsIncrements);

        //add form
        addBtn = new TextButton("Add", skin);
        addBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                battleGroup.gameID = WargameCampaign.loadedGameData.gameID;
                battleGroup.playerID = WargameCampaign.userid;
                battleGroup.groupName = groupNameAdd.getText();
                battleGroup.maxMoves = groupMovesAdd.getSelected();
                battleGroup.visibility = groupVisibilityAdd.getSelected();
                battleGroup.points = groupPointsAdd.getSelected();

                Packets.PacketGameData groupTask = new Packets.PacketGameData();

                groupTask.task = "addGroup";
                groupTask.battleGroups.add(battleGroup);
                groupTask.message = "Request Group add: groupName = " + battleGroup.groupName;

                NetworkController.newRequest(groupTask);
            }
        });

        //ready button
        readyBtn = new TextButton("Groups Ready - Start Deployment", skin);
        readyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Packets.PacketGameTasks readyGame = new Packets.PacketGameTasks();
                readyGame.gameID = WargameCampaign.loadedGameData.gameID;
                readyGame.userid = WargameCampaign.userid;
                readyGame.task = "readyGameRequest";
                readyGame.message = "Ready game request: gameID = " + WargameCampaign.loadedGameData.gameID;
                NetworkController.newRequest(readyGame);
            }
        });
        readyBtn.setVisible(false);

        TextButton backBtn = new TextButton("Back to Main Menu", skin);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Menu.showMenu(WargameCampaign.Menus.MAIN_MENU);
            }
        });

        addGroupTable.add(groupNameAdd).width(100).padTop(5);
        addGroupTable.add(groupMovesAdd).width(75).padLeft(5).padTop(5);
        addGroupTable.add(groupVisibilityAdd).width(75).padLeft(5).padTop(5);
        addGroupTable.add(groupPointsAdd).width(75).padLeft(5).padTop(5);
        addGroupTable.add(addBtn).width((btnWidth * 2) + 5).padLeft(5).padTop(5);

        table.add(totalPointsLeft).colspan(columns);
        table.row().fill().expandX();
        table.add(groupNameLabel).width(100).padTop(5);
        table.add(groupMovesLabel).width(75).padLeft(5).padTop(5);
        table.add(groupVisibilityLabel).width(75).padLeft(5).padTop(5);
        table.add(groupPointsLabel).width(75).padLeft(5).padTop(5);
        table.add(groupActionLabel).width((btnWidth * 2) + 5).padLeft(5).padTop(5);
        table.row().fill().expandX();
        table.add(addGroupTable).colspan(columns);
        table.row().fill().expandX();
        table.add(battleGroups).colspan(columns);

        loadGroups();

        gameMsg = new Label("", skin);

        table.row().fill().expandX();
        table.add(gameMsg).colspan(columns).padTop(15);
        table.row().fill().expandX();
        table.add(readyBtn).width(300).colspan(columns).padTop(5);
        table.row().fill().expandX();
        table.add(backBtn).width(300).colspan(columns).padTop(5);

        stage.addActor(table);
    }

    @Override
    public void update(float delta) {
        stage.act(delta);

        if(AddBattleGroupsMenu.groupsJustReloaded) {
            int pointsTot = 0;
            for(BattleGroup bg: WargameCampaign.loadedGameData.battleGroups){
                if(bg.playerID == WargameCampaign.userid)
                    pointsTot += bg.points;
            }
            pointsLeft = WargameCampaign.loadedGameData.points - pointsTot;
            pointsIncrements.clear();
            for(int i = 500; i <= pointsLeft; i += 500)
                pointsIncrements.add(i);
            groupPointsAdd.setItems(pointsIncrements);

            loadGroups();
        }

        if(WargameCampaign.thisPlayer != null && WargameCampaign.thisPlayer.state > 1) {
            readyBtn.remove();
            groupActionLabel.remove();
            gameMsg.setText("Your Battle Groups have been finalize.  You are waiting for other players.");

            refreshTimer -= delta;
            if(refreshTimer <= 0){
                Packets.PacketGameTasks loadGame = new Packets.PacketGameTasks();
                loadGame.gameID = WargameCampaign.loadedGameData.gameID;
                loadGame.userid = WargameCampaign.userid;
                loadGame.task = "loadGameRequest";
                loadGame.message = "Load game request: gameID = " + WargameCampaign.loadedGameData.gameID;
                NetworkController.newRequest(loadGame);
                refreshTimer = 15.0f;
            }
        }

        if(pointsLeft == 0){
            addGroupTable.remove();

            readyBtn.setVisible(true);
        }

        totalPointsLeft.setText("Points Left: " + Integer.toString(pointsLeft));
    }

    @Override
    public void draw() {
        stage.draw();
    }

    private void loadGroups(){
        battleGroups.clear();

        for(BattleGroup bg: WargameCampaign.loadedGameData.battleGroups){
            final int groupID = bg.groupID;
            final int points = bg.points;

            if(WargameCampaign.thisPlayer != null && WargameCampaign.thisPlayer.state < 1) {
                final TextField groupNameUpdate = new TextField(bg.groupName, skin);
                final SelectBox<Integer> groupMovesUpdate = new SelectBox<Integer>(skin);
                final SelectBox<Integer> groupVisibilityUpdate = new SelectBox<Integer>(skin);
                final Label groupPointsUpdate = new Label(Integer.toString(points), skin);

                groupMovesUpdate.setItems(movesVisList);
                groupVisibilityUpdate.setItems(movesVisList);

                groupMovesUpdate.setSelected(bg.maxMoves);
                groupVisibilityUpdate.setSelected(bg.visibility);

                TextButton updateBtn = new TextButton("Update", skin);
                updateBtn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        battleGroup.gameID = WargameCampaign.loadedGameData.gameID;
                        battleGroup.playerID = WargameCampaign.userid;
                        battleGroup.groupID = groupID;
                        battleGroup.groupName = groupNameUpdate.getText();
                        battleGroup.maxMoves = groupMovesUpdate.getSelected();
                        battleGroup.visibility = groupVisibilityUpdate.getSelected();
                        battleGroup.points = points;

                        Packets.PacketGameData groupTask = new Packets.PacketGameData();

                        groupTask.task = "updateGroup";
                        groupTask.battleGroups.add(battleGroup);
                        groupTask.message = "Request Group update: groupid = " + groupID;

                        NetworkController.newRequest(groupTask);
                    }
                });

                TextButton deleteBtn = new TextButton("Delete", skin);
                deleteBtn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        battleGroup.gameID = WargameCampaign.loadedGameData.gameID;
                        battleGroup.groupID = groupID;
                        battleGroup.playerID = WargameCampaign.userid;

                        Packets.PacketGameData groupTask = new Packets.PacketGameData();

                        groupTask.task = "deleteGroup";
                        groupTask.battleGroups.add(battleGroup);
                        groupTask.message = "Request Group delete: groupid = " + groupID;

                        NetworkController.newRequest(groupTask);
                    }
                });

                battleGroups.row().fill().expandX();
                battleGroups.add(groupNameUpdate).width(100).padTop(5);
                battleGroups.add(groupMovesUpdate).width(75).padLeft(5).padTop(5);
                battleGroups.add(groupVisibilityUpdate).width(75).padLeft(5).padTop(5);
                battleGroups.add(groupPointsUpdate).width(75).padLeft(5).padTop(5);
                battleGroups.add(updateBtn).width(btnWidth).padLeft(5).padTop(5);
                battleGroups.add(deleteBtn).width(btnWidth).padLeft(5).padTop(5);
            }else{
                Label groupNameUpdate = new Label(bg.groupName, skin);
                Label groupMovesUpdate = new Label(Integer.toString(bg.maxMoves), skin);
                Label groupVisibilityUpdate = new Label(Integer.toString(bg.visibility), skin);
                Label groupPointsUpdate = new Label(Integer.toString(points), skin);

                battleGroups.row().fill().expandX();
                battleGroups.add(groupNameUpdate).width(100).padTop(5);
                battleGroups.add(groupMovesUpdate).width(75).padLeft(5).padTop(5);
                battleGroups.add(groupVisibilityUpdate).width(75).padLeft(5).padTop(5);
                battleGroups.add(groupPointsUpdate).width(75).padLeft(5).padTop(5);
            }
        }

        AddBattleGroupsMenu.groupsJustReloaded = false;
    }
}
