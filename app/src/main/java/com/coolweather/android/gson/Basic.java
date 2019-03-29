package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("location")
    public String cityName;

    @SerializedName("cid")
    public String weatherId;

    @SerializedName("lat")
    public String lat;

    @SerializedName("lon")
    public String lon;

    @SerializedName("parent_city")
    public String parentCityName;

    @SerializedName("admin_area")
    public String adminAreaName;

    @SerializedName("cnty")
    public String country;

    @SerializedName("tz")
    public String tz;
}
