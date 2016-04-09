package com.averagecoder.wargame.objects;

import com.averagecoder.wargame.WargameCampaign;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class GameObject {

    protected TextureRegion spritesheet;
    protected Array<TextureRegion> frames = new Array<TextureRegion>();
    protected WargameCampaign game;
    protected SpriteBatch batch;
    protected int frame = 0;
    protected Vector2 pos = new Vector2(0, 0);
    protected int numFrames;

    public float width = 0;
    public float height = 0;

    public boolean factoryDraw = true;
    public boolean visible = true;

    protected GameObject(){

    }

    public GameObject(WargameCampaign wargameCampaign){
        game = wargameCampaign;
        batch = game.batch;
        numFrames = 1;
    }

    protected void init(TextureRegion ss){
        spritesheet = ss;

        for(int i = 0; i < numFrames; i++)
            frames.add(new TextureRegion(spritesheet, i * (int)width, 0, (int)width, (int)height));
    }

    public abstract void update(float delta);

    public void draw(){
        if(visible)
            batch.draw(frames.get(frame), pos.x, pos.y);
    }

    static boolean checkCollision(GameObject a, GameObject b){
        Rectangle rectA = new Rectangle((int)a.pos.x, (int)a.pos.y, a.width, a.height);
        Rectangle rectB = new Rectangle((int)b.pos.x, (int)b.pos.y, b.width, b.height);

        return rectA.overlaps(rectB);
    }

    static boolean checkCollision(GameObject a, Vector2 point){
        Rectangle rectA = new Rectangle((int)a.pos.x, (int)a.pos.y, a.width, a.height);

        return rectA.contains(point);
    }

}
