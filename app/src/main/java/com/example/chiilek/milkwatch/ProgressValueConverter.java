package com.example.chiilek.milkwatch;

/**
 * Created by chiilek on 4/4/2018.
 */

public class ProgressValueConverter {

    public static float pHProgressToValue(float progress){
        return progress / 100 + 6.0f;
    }

    public static float pHValueToProgress(float val){
        return (val - 6) * 100;
    }

    public static float capProgressToValue(float progress){
        return 300f + progress * 2;
    }

    public static float capValueToProgress(float val){
        return (val - 300) / 2;
    }
}
