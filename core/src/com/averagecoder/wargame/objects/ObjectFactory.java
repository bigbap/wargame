package com.averagecoder.wargame.objects;

import com.averagecoder.wargame.WargameCampaign;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class ObjectFactory {

    WargameCampaign game;
    Array<GameObject> objects = new Array<GameObject>();

    static HashMap<String, TextureRegion> spritesheets = new HashMap<String, TextureRegion>();

    public ObjectFactory(WargameCampaign wargameCampaign){
        game = wargameCampaign;
    }

    public void addSpritesheet(String sheet){
        spritesheets.put(sheet, game.spritesheetAtlas.findRegion(sheet));
    }

    public static TextureRegion getSpritesheet(String sheet){
        return spritesheets.get(sheet);
    }

    public int addObject(GameObject object, String sheet, boolean draw){
        TextureRegion thisSheet = getSpritesheet(sheet);
        if(thisSheet == null)
            return 1;

        object.init(getSpritesheet(sheet));
        object.factoryDraw = draw;

        objects.add(object);

        return 0;
    }

    public void updateObjects(float delta){
        for(GameObject obj: objects){
            obj.update(delta);
        }
    }

    public void drawObjects(){
        for(GameObject obj: objects){
            if(obj.factoryDraw) {
                obj.draw();
            }
        }
    }

}
