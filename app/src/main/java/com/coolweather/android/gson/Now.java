package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("fl")
    public String flTmp;

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond_code")
    public String condCode;

    @SerializedName("cond_txt")
    public String condTxt;

    @SerializedName("wind_deg")
    public String windDeg;

    @SerializedName("wind_dir")
    public String windDir;

    @SerializedName("wind_sc")
    public String windSc;

    @SerializedName("wind_spd")
    public String windSpd;

    @SerializedName("hum")
    public String hum;

    @SerializedName("pcpn")
    public String pcpn;

    @SerializedName("pres")
    public String pres;

    @SerializedName("vis")
    public String vis;

    @SerializedName("cloud")
    public String cloud;

}
