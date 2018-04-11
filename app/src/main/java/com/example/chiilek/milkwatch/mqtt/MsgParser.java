package com.example.chiilek.milkwatch.mqtt;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by chiilek on 1/4/2018.
 */

public class MsgParser {

    private String message;

    private float pH_value;
    private float capacitance;
    private String deviceName;

    public float getpH_value() {
        return pH_value;
    }

    public void setpH_value(float pH_value) {
        this.pH_value = pH_value;
    }

    public float getCapacitance() {
        return capacitance;
    }

    public void setCapacitance(float capacitance) {
        this.capacitance = capacitance;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public MsgParser(String message) {
        this.message = message;
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(message);

        if (jsonTree.isJsonObject()){
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            deviceName = jsonObject.get("device").getAsString();
            Log.d("json", "Device: " + deviceName);
            pH_value = jsonObject.get("pH value").getAsFloat();
            Log.d("json", "pH: " + pH_value);
            capacitance = jsonObject.get("cap value").getAsFloat();
            Log.d("json", "Capacitance: " + capacitance);

        } else {
            Log.d("json", "Unable to parse json String");
        }
    }
}
