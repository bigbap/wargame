package com.averagecoder.wargame.utils;

import com.badlogic.gdx.math.Vector2;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GameMath {

    private GameMath(){}

    public static Vector2 normalize(Vector2 a, Vector2 b, float distance){

        return new Vector2((a.x - b.x) / distance, (a.y - b.y) / distance);
    }

    public static float getAngle(Vector2 origin, Vector2 target) {
        return (float) Math.atan2(target.y - origin.y, target.x - origin.x);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
