package com.averagecoder.wargame.objects;

import com.averagecoder.wargame.WargameCampaign;
import com.averagecoder.wargame.utils.Utils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Hexagon extends GameObject {

    public Vector2 tilePos;
    Polygon collisionPoly;
    Vector2 centerPoint;

    static float sideSize;

    public Hexagon(WargameCampaign wargameCampaign, Vector2 tileP){
        super(wargameCampaign);

        width = game.TILE_SIZE;
        height = game.TILE_SIZE;

        tilePos = tileP;
        sideSize = game.TILE_SIZE / 2;
        pos = Map.getPixelPos(Map.hexToCube(tilePos));
        numFrames = 5;

        collisionPoly = calcCollisionPoly();
    }

    @Override
    public void update(float delta) {

    }

    Polygon calcCollisionPoly(){
        centerPoint = new Vector2(pos.x + (width / 2), pos.y + (height / 2));

        Vector2 point1 = new Vector2(centerPoint.x + sideSize * (float)Math.sin(Math.toRadians(60)), centerPoint.y + sideSize * (float)Math.cos(Math.toRadians(60)));
        Vector2 point2 = new Vector2(centerPoint.x + sideSize * (float)Math.sin(Math.toRadians(120)), centerPoint.y + sideSize * (float)Math.cos(Math.toRadians(120)));
        Vector2 point3 = new Vector2(centerPoint.x + sideSize * (float)Math.sin(Math.toRadians(180)), centerPoint.y + sideSize * (float)Math.cos(Math.toRadians(180)));
        Vector2 point4 = new Vector2(centerPoint.x + sideSize * (float)Math.sin(Math.toRadians(240)), centerPoint.y + sideSize * (float)Math.cos(Math.toRadians(240)));
        Vector2 point5 = new Vector2(centerPoint.x + sideSize * (float)Math.sin(Math.toRadians(300)), centerPoint.y + sideSize * (float)Math.cos(Math.toRadians(300)));
        Vector2 point6 = new Vector2(centerPoint.x + sideSize * (float)Math.sin(Math.toRadians(360)), centerPoint.y + sideSize * (float)Math.cos(Math.toRadians(360)));

        return new Polygon(new float[]{point1.x, point1.y, point2.x, point2.y, point3.x, point3.y, point4.x, point4.y, point5.x, point5.y, point6.x, point6.y});
    }

    void drawCollisionPoly(){
        WargameCampaign.shapeRenderer.setProjectionMatrix(game.camera.combined);
        WargameCampaign.shapeRenderer.begin();
        WargameCampaign.shapeRenderer.polygon(collisionPoly.getVertices());
        WargameCampaign.shapeRenderer.end();
    }
}
