package com.example.powen.myforecast;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class ResultActivity extends AppCompatActivity {
    private JSONObject myJson=null;
    private String[] tokenGet;
    private String cityStr;
    private String stateStr;
    private boolean isUSUnit;
    private Map<String, Integer>iconMap= new HashMap<String,Integer>();
    private String toFBSummary="";
    private String toFBIcon="";
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {

            tokenGet = extras.getStringArray("toResultJSON");// this is the JSON file from the PHP page
            cityStr = tokenGet[1];
            stateStr = tokenGet[2];
            isUSUnit = (tokenGet[3].equals("fahrenheit"));
            //build the icon mapping
            iconMap.put("clear-day",R.drawable.clear);
            iconMap.put("clear-night",R.drawable.clear_night);
            iconMap.put("rain",R.drawable.rain);
            iconMap.put("snow",R.drawable.snow);
            iconMap.put("sleet",R.drawable.sleet);
            iconMap.put("wind",R.drawable.wind);
            iconMap.put("fog",R.drawable.fog);
            iconMap.put("cloudy",R.drawable.cloudy);
            iconMap.put("partly-cloudy-day",R.drawable.cloud_day);
            iconMap.put("partly-cloudy-night",R.drawable.cloud_night);
            //parse the JSON & modify the entries in this activity
            try {
                myJson = new JSONObject(tokenGet[0]);
                parseJSON(myJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void parseJSON(JSONObject jsonObj){
        if(jsonObj==null) return;
        ImageView iconResult = (ImageView) findViewById(R.id.iconResult);
        TextView summaryResult = (TextView) findViewById(R.id.summaryDetail);
        TextView tempResult = (TextView) findViewById(R.id.tempResult);
        TextView degreeResult = (TextView) findViewById(R.id.degreeUnit);
        TextView lohiResult = (TextView) findViewById(R.id.lohiTemp);
        TextView precipResult = (TextView) findViewById(R.id.precipResult);
        TextView chanceOfRainResult = (TextView) findViewById(R.id.chanceOfRainResult);
        TextView windSpeedResult = (TextView) findViewById(R.id.windSpeedResult);
        TextView dewResult = (TextView) findViewById(R.id.dewResult);
        TextView humidityResult = (TextView) findViewById(R.id.humidityResult);
        TextView visibilityResult = (TextView) findViewById(R.id.visibilityResult);
        TextView sunriseResult = (TextView) findViewById(R.id.sunriseResult);
        TextView sunsetResult = (TextView) findViewById(R.id.sunsetResult);
        try {
            JSONObject currently = jsonObj.getJSONObject("currently");
            iconResult.setImageResource(iconMap.get(currently.getString("icon")));
            summaryResult.setText(currently.getString("summary") + " in " + cityStr + ", " + stateStr);

            String degreeSymbol = (isUSUnit)?" \u2109":"\u2103";
            String speedUnit = (isUSUnit)?"mph":"m/s";
            String distUnit = (isUSUnit)?"mi":"km";
            tempResult.setText(String.valueOf(currently.getInt("temperature")));
            degreeResult.setText(degreeSymbol);
            toFBSummary = currently.getString("summary")+", "+String.valueOf(currently.getInt("temperature"))+degreeSymbol;
            toFBIcon = currently.getString("icon");
            JSONObject daily = jsonObj.getJSONObject("daily");
            String lohiStr = String.valueOf(daily.getJSONArray("data").getJSONObject(0).getInt("temperatureMin"));
            lohiStr += "\u00B0 | "+ String.valueOf(daily.getJSONArray("data").getJSONObject(0).getInt("temperatureMax"))+"\u00B0";
            lohiResult.setText(lohiStr);
            double precipValue = currently.getDouble("precipIntensity")/((isUSUnit)?1.00:25.4);
            String precipIntensity;
            if(precipValue<0.002)
                precipIntensity ="None";
            else if(precipValue<0.017)
                precipIntensity ="Very Light";
            else if(precipValue<0.1)
                precipIntensity ="Light";
            else if(precipValue<0.4)
                precipIntensity ="Moderate";
            else
                precipIntensity ="Heavy";
            precipResult.setText(precipIntensity);
            chanceOfRainResult.setText(String.valueOf((int) (currently.getDouble("precipProbability")*100.0))+"%");
            windSpeedResult.setText(String.valueOf(currently.getDouble("windSpeed"))+speedUnit);
            dewResult.setText(String.valueOf( currently.getInt("dewPoint"))+"\u00B0");
            humidityResult.setText(String.valueOf((int) (currently.getDouble("humidity")*100.0))+"%");
            visibilityResult.setText(String.valueOf(currently.getDouble("visibility"))+distUnit);
            Long timezoneOffset = myJson.getLong("offset");
            Long sunriseEpoch = 1000*( daily.getJSONArray("data").getJSONObject(0).getLong("sunriseTime") + timezoneOffset * 3600);
            Long sunsetEpoch =1000*( daily.getJSONArray("data").getJSONObject(0).getLong("sunsetTime")+timezoneOffset*3600);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String sunRise = sdf.format(new Date(sunriseEpoch));
            String sunSet = sdf.format(new Date(sunsetEpoch));
            sunriseResult.setText(sunRise);
            sunsetResult.setText(sunSet);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void toDetail(View view) {
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra("toDetailJSON", tokenGet);
        startActivity(intent);
    }
    public void toMap(View view) {
        Intent intent = new Intent(this,MapActivity.class);
        String[] place = new String[2];
        try {
            place[0] = String.valueOf(myJson.getDouble("latitude"));
            place[1] = String.valueOf(myJson.getDouble("longitude"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("toMapPlace", place);
        startActivity(intent);
    }
    public void toShare(View view){

        ShareDialog shareDialog;
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getApplicationContext(), "Facebook post successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Posted cancelled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Posted cancelled", Toast.LENGTH_SHORT).show();

            }
        });

         Map<String,String>iconMap2= new HashMap<String,String>();
        iconMap2.put("clear-day","clear.png");
        iconMap2.put("clear-night","clear_night.png");
        iconMap2.put("rain","rain.png");
        iconMap2.put("snow","snow.png");
        iconMap2.put("sleet","sleet.png");
        iconMap2.put("wind","wind.png");
        iconMap2.put("fog", "fog.png");
        iconMap2.put("cloudy","cloudy.png");
        iconMap2.put("partly-cloudy-day","cloud_day.png");
        iconMap2.put("partly-cloudy-night", "cloud_night.png");
        String imgURL = "http://cs-server.usc.edu:45678/hw/hw8/images/"+iconMap2.get(toFBIcon);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Current weather in "+cityStr+", "+stateStr).setImageUrl(Uri.parse(imgURL))
                    .setContentDescription(
                            toFBSummary)
                    .setContentUrl(Uri.parse("http://forecast.io"))
                    .build();

            shareDialog.show(linkContent);

        }
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
