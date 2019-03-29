package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class DailyForecast {

    public String date;

    @SerializedName("sr")
    public String sunRiseTime;

    @SerializedName("ss")
    public String sunSlowTime;

    @SerializedName("mr")
    public String moonRiseTime;

    @SerializedName("ms")
    public String moonSlowTime;

    @SerializedName("tmp_max")
    public String tmpMax;

    @SerializedName("tmp_min")
    public String tmpMin;

    @SerializedName("cond_code_d")
    public String dayTimeCode;

    @SerializedName("cond_code_n")
    public String nightTimeCode;

    @SerializedName("cond_txt_d")
    public String dayTimeTxt;

    @SerializedName("cond_txt_n")
    public String nightTimeTxt;

    @SerializedName("wind_deg")
    public String windDegree;

    @SerializedName("wind_dir")
    public String windDir;

    @SerializedName("wind_sc")
    public String windSc;

    @SerializedName("wind_spd")
    public String windSpeed;

    @SerializedName("hum")
    public String hum;

    @SerializedName("pcpn")
    public String pcpn;

    @SerializedName("pop")
    public String pop;

    @SerializedName("pres")
    public String pres;

    @SerializedName("uv_index")
    public String uvIndex;

    @SerializedName("vis")
    public String vis;
}
