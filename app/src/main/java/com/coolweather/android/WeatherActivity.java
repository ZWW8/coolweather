package com.coolweather.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.DailyForecast;
import com.coolweather.android.gson.LifeStyle;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;
    private TextView titleUpdateTime;

    private TextView degreeText;
    private TextView weatherInfoText;

    private TextView timeText;
    private TextView maxDegreeText;
    private TextView minDegreeText;

    private LinearLayout forcastLayout;
    private LinearLayout lifestyleLayout;

    private TextView condText;
    private TextView windScText;
    private TextView humText;
    private TextView visText;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;

    public DrawerLayout drawerLayout;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21){
            setTransparent(this);
        }
        setContentView(R.layout.activity_weather);

        //初始化各控件
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_uspdate_time);

        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);

        forcastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        lifestyleLayout = (LinearLayout) findViewById(R.id.lifestyle_layout);

        timeText = (TextView) findViewById(R.id.time_text);
        maxDegreeText = findViewById(R.id.max_tmp_text);
        minDegreeText = findViewById(R.id.min_tmp_text);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无法缓存时去服务器查天气
            mWeatherId = getIntent().getStringExtra("weather_Id");
            weatherLayout.setVisibility(View.VISIBLE);
            requestWeather(mWeatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic !=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
    }

    /**
     * 设置状态栏全透明
     *
     * @param activity 需要设置的activity
     */
    public static void setTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        setRootView(activity);
    }

    /**
     * 使状态栏透明
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void transparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个flag contentView才能延伸到状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //状态栏覆盖在contentView上面，设置透明使contentView的背景透出来
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            //让contentView延伸到状态栏并且设置状态栏颜色透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置根布局参数
     */
    private static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId){
        String weatherUrl = "https://free-api.heweather.net/s6/weather?location="+weatherId+"&key=4f8b8dcedea6496c9c3932779ecfa321";
//        Log.d("WeatherActivity",weatherId);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败2",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败1",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    /**
     * 加载每日必应一图
     */
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.locTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.condTxt;
        String days = "";
        String mon = "";
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        Calendar calendar = Calendar.getInstance();
        String time="";
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        days = ""+day;
        if(month < 10)
        {
            mon = "0"+month;
        }
        if(day <10)
        {
            days = "0"+day;
        }
        time = year+"-"+mon+"-"+days;
        Log.d("WeatherAcyivity",time);
        Log.d("WeatherActivity.class",weather.update.locTime.split(" ")[0]);
        if(time.equals(weather.update.locTime.split(" ")[0])){
            timeText.setText("今天");
        }

        for (DailyForecast dailyForecast : weather.forecastList) {
            if(dailyForecast.date.equals(time)){
                maxDegreeText.setText(dailyForecast.tmpMax);
                minDegreeText.setText(dailyForecast.tmpMin);
            }
        }


        forcastLayout.removeAllViews();
        for (DailyForecast dailyForecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forcastLayout,false);
            TextView dataText = (TextView) view.findViewById(R.id.data_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_txt);
            TextView minText = (TextView) view.findViewById(R.id.min_txt);
            dataText.setText(dailyForecast.date);
            infoText.setText(dailyForecast.dayTimeTxt);
            maxText.setText(dailyForecast.tmpMax);
            minText.setText(dailyForecast.tmpMin);
            forcastLayout.addView(view);
        }

        condText = (TextView) findViewById(R.id.cond_text);
        windScText = (TextView) findViewById(R.id.wind_sc_text);
        humText = (TextView) findViewById(R.id.hum_text);
        visText = (TextView) findViewById(R.id.vis_text);
        condText.setText(weather.now.condTxt);
        windScText.setText(weather.now.windSc);
        humText.setText(weather.now.hum);
        visText.setText(weather.now.vis);

        lifestyleLayout.removeAllViews();
        for (LifeStyle lifeStyle : weather.lifeStyleList) {

//            Log.d("WeatherActivity",lifeStyle.type);

            View view = LayoutInflater.from(this).inflate(R.layout.lifestyle_item,lifestyleLayout,false);
            TextView typeText = (TextView) view.findViewById(R.id.type_text);
            TextView brfText = (TextView) view.findViewById(R.id.brf_text);
            TextView suggestionText = (TextView) view.findViewById(R.id.suggestion_text);


            if("comf".equals(lifeStyle.type))
            {
                typeText.setText("舒适度:");
            }else if("cw".equals(lifeStyle.type)){
                typeText.setText("洗车:");
            }else if("drsg".equals(lifeStyle.type)){
                typeText.setText("穿衣:");
            }else if("flu".equals(lifeStyle.type)){
                typeText.setText("感冒:");
            }else if("sport".equals(lifeStyle.type)){
                typeText.setText("运动:");
            }else if("trav".equals(lifeStyle.type)){
                typeText.setText("旅游:");
            }else if("uv".equals(lifeStyle.type)){
                typeText.setText("紫外线:");
            }else if("air".equals(lifeStyle.type)){
                typeText.setText("空气污染:");
            }else if("ac".equals(lifeStyle.type)){
                typeText.setText("空调:");
            }else if("ag".equals(lifeStyle.type)){
                typeText.setText("过敏:");
            }else if("gl".equals(lifeStyle.type)){
                typeText.setText("太阳镜:");
            }else if("mu".equals(lifeStyle.type)){
                typeText.setText("化妆:");
            }else if("airc".equals(lifeStyle.type)){
                typeText.setText("晾晒:");
            }else if("ptfc".equals(lifeStyle.type)){
                typeText.setText("交通:");
            }else if("fsh".equals(lifeStyle.type)){
                typeText.setText("钓鱼:");
            }else if("spi".equals(lifeStyle.type)){
                typeText.setText("防晒:");
            }
//            switch (lifeStyle.type){
//                case "comf":
//                    typeText.setText("舒适度");
//                case "cw":
//                    typeText.setText("洗车");
//                case "drsg":
//                    typeText.setText("穿衣");
//                case "flu":
//                    typeText.setText("感冒");
//                case "sport":
//                    typeText.setText("运动");
//                case "trav":
//                    typeText.setText("旅游");
//                case "uv":
//                    typeText.setText("紫外线");
//                case "air":
//                    typeText.setText("空气污染扩散");
//                case "ac":
//                    typeText.setText("空调");
//                case "ag":
//                    typeText.setText("过敏");
//                case "gl":
//                    typeText.setText("太阳镜");
//                case "mu":
//                    typeText.setText("化妆");
//                case "airc":
//                    typeText.setText("晾晒");
//                case "ptfc":
//                    typeText.setText("交通");
//                case "fsh":
//                    typeText.setText("钓鱼");
//                case "spi":
//                    typeText.setText("防晒");
//            }
            brfText.setText(lifeStyle.brf);
            suggestionText.setText(lifeStyle.suggestion);
            lifestyleLayout.addView(view);
        }
    }
}
