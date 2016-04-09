package com.averagecoder.wargame.objects;

import com.averagecoder.wargame.WargameCampaign;
import com.badlogic.gdx.math.Vector2;

public class Tile extends Hexagon {

    public boolean passable;
    int moves;
    boolean active;

    public float cost;
    public final String ID;
    public Unit unitOnTile = null;

    public boolean deployable = true;

    public Tile(WargameCampaign wargameCampaign, Vector2 tileP, int tile, boolean pas, int mov){
        super(wargameCampaign, tileP);

        ID = Integer.toString((int)tilePos.x) + "" + Integer.toString((int)tilePos.y);

        numFrames = Map.tileFrames;
        frame = tile;
        passable = pas;
        moves = mov;

        visible = false;
        deployable = false;
    }

    @Override
    public void update(float delta){}

    @Override
    public void draw(){
        batch.draw(frames.get(frame), pos.x, pos.y);

        if(this.active)
            batch.draw(frames.get(4), pos.x, pos.y);

        if(!this.visible)
            batch.draw(frames.get(6), pos.x, pos.y);

        if(this.deployable)
            batch.draw(frames.get(5), pos.x, pos.y);

        //drawTileID();
    }

    private void drawTilePos(){
        String str = Integer.toString((int) tilePos.x) + "," + Integer.toString((int) tilePos.y);
        WargameCampaign.font.draw(batch, str, pos.x + 25, pos.y + 45);

        str = Integer.toString((int) pos.x) + "," + Integer.toString((int) pos.y);
        WargameCampaign.font.draw(batch, str, pos.x + 25, pos.y + 30);
    }

    private void drawTileID() {
        WargameCampaign.font.draw(batch, ID, pos.x + 8, pos.y + 20);
    }

    private void drawVisibility() {
        String str = visible ? "1" : "0";
        WargameCampaign.font.draw(batch, str, pos.x + 8, pos.y + 20);
    }

    public void highlight(boolean highlight){
        active = highlight;
        Map.activeTiles.add(this);
    }

}
