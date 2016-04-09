package com.averagecoder.wargame.utils;

import java.util.Random;

public class Utils {
    private static Random random = new Random(System.currentTimeMillis());

    private Utils(){}

    public static void log(Object obj){
        System.out.println(obj);
    }

    public static int getUniqueID(){
        return Math.abs(random.nextInt());
    }
}
