package com.example.powen.myforecast;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DetailActivity extends AppCompatActivity {

    private String[] tokenGet;
    private JSONObject myJson=null;
    private String degreeUnit="\u00B0";
    private Map<String, Integer> iconMap= new HashMap<String,Integer>();
    private int timezoneOffset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle extras = getIntent().getExtras();
        tokenGet = extras.getStringArray("toDetailJSON");
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
        try {
            myJson = new JSONObject(tokenGet[0]);
            TextView summary = (TextView) findViewById(R.id.summaryDetail);
            summary.setText("More details for " + tokenGet[1] + ", " + tokenGet[2]);
            timezoneOffset = myJson.getInt("offset");
            parse7days();
            parse48Hour();
            degreeUnit += (tokenGet[3].equals("fahrenheit")?"F":"C");
            TextView degreeTitle = (TextView) findViewById(R.id.tempTitle);
            degreeTitle.setText("Temp("+degreeUnit+")");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void parse7days(){
        try {
            JSONArray daily = myJson.getJSONObject("daily").getJSONArray("data");
            for(int i=1;i<8&&i<daily.length();i++){
                String column1Name = "day"+String.valueOf(i)+"line1";
                String column2Name = "day"+String.valueOf(i)+"line2";
                String column3Name = "day"+String.valueOf(i)+"Img";
                TextView dateDetail = (TextView) findViewById(getResources().getIdentifier(column1Name, "id", getPackageName()));
                TextView tempDetail = (TextView) findViewById(getResources().getIdentifier(column2Name, "id", getPackageName()));
                ImageView iconDetail = (ImageView) findViewById(getResources().getIdentifier(column3Name, "id", getPackageName()));
                //set time string
                Long timeStamp = 1000*( daily.getJSONObject(i).getLong("time") + timezoneOffset * 3600);
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd",new Locale("en"));
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String timeText =sdf.format(new Date(timeStamp));
                dateDetail.setText(timeText);
                //set icon
                iconDetail.setImageResource(iconMap.get(daily.getJSONObject(i).getString("icon")));
                //set temp
                String lowTemp = String.valueOf(daily.getJSONObject(i).getInt("temperatureMin"));
                String highTemp = String.valueOf(daily.getJSONObject(i).getInt("temperatureMax"));
                tempDetail.setText("Min: "+lowTemp+degreeUnit+" | Max: "+highTemp+degreeUnit);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void parse48Hour(){
        try {
            JSONArray hourly = myJson.getJSONObject("hourly").getJSONArray("data");
            for(int i=0;i<24&&i<hourly.length();i++){
                String column1Name = "Hour"+String.valueOf(i+1)+"C1";
                String column2Name = "Hour"+String.valueOf(i+1)+"C2";
                String column3Name = "Hour"+String.valueOf(i+1)+"C3";
                TextView timeDetail = (TextView) findViewById(getResources().getIdentifier(column1Name, "id", getPackageName()));
                ImageView iconDetail = (ImageView) findViewById(getResources().getIdentifier(column2Name, "id", getPackageName()));
                TextView tempDetail = (TextView) findViewById(getResources().getIdentifier(column3Name, "id", getPackageName()));
                //set time string
                Long timeStamp = 1000*( hourly.getJSONObject(i).getLong("time") + timezoneOffset * 3600);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",new Locale("en"));
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String timeText =sdf.format(new Date(timeStamp));
                timeDetail.setText(timeText);
                //set icon
                iconDetail.setImageResource(iconMap.get(hourly.getJSONObject(i).getString("icon")));
                //set temp
                tempDetail.setText(String.valueOf(hourly.getJSONObject(i).getInt("temperature")));
            }
            for(int i=0;i<24&&i<hourly.length();i++){
                String column1Name = "AppendHour"+String.valueOf(i+1)+"C1";
                String column2Name = "AppendHour"+String.valueOf(i+1)+"C2";
                String column3Name = "AppendHour"+String.valueOf(i+1)+"C3";
                TextView timeDetail = (TextView) findViewById(getResources().getIdentifier(column1Name, "id", getPackageName()));
                ImageView iconDetail = (ImageView) findViewById(getResources().getIdentifier(column2Name, "id", getPackageName()));
                TextView tempDetail = (TextView) findViewById(getResources().getIdentifier(column3Name, "id", getPackageName()));
                //set time string
                Long timeStamp = 1000*( hourly.getJSONObject(i+24).getLong("time") + timezoneOffset * 3600);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",new Locale("en"));
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String timeText =sdf.format(new Date(timeStamp));
                timeDetail.setText(timeText);
                //set icon
                iconDetail.setImageResource(iconMap.get(hourly.getJSONObject(i+24).getString("icon")));
                //set temp
                tempDetail.setText(String.valueOf(hourly.getJSONObject(i+24).getInt("temperature")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void show24HRs(View view){
        TableLayout table24 = (TableLayout) findViewById(R.id.HoursTable);
        TableLayout table7 = (TableLayout) findViewById(R.id.DaysTable);
        Button btn24 = (Button) findViewById(R.id.Hours24);
        Button btn7 = (Button) findViewById(R.id.Days7);
        table24.setVisibility(View.VISIBLE);
        table7.setVisibility(View.GONE);
        btn24.setBackgroundDrawable(getResources().getDrawable(R.drawable.aquabutton));
        btn7.setBackgroundDrawable(getResources().getDrawable(R.drawable.graybutton));
    }
    public void show7Days(View view){
        TableLayout table24 = (TableLayout) findViewById(R.id.HoursTable);
        TableLayout table7 = (TableLayout) findViewById(R.id.DaysTable);
        Button btn24 = (Button) findViewById(R.id.Hours24);
        Button btn7 = (Button) findViewById(R.id.Days7);
        table24.setVisibility(View.GONE);
        table7.setVisibility(View.VISIBLE);
        btn7.setBackgroundDrawable(getResources().getDrawable(R.drawable.aquabutton));
        btn24.setBackgroundDrawable(getResources().getDrawable(R.drawable.graybutton));
    }

    public void showAppendHours(View view){
        Button append = (Button) findViewById(R.id.append);
        append.setVisibility(View.GONE);
        for(int i=1;i<=24;i++){
            String appenRowName = "append"+String.valueOf(i);
            int resID = getResources().getIdentifier(appenRowName, "id", getPackageName());
            TableRow appendRow = (TableRow) findViewById(resID);
            appendRow.setVisibility(view.VISIBLE);
        }
    }

}
