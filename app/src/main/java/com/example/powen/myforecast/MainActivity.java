package com.example.powen.myforecast;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    Button btnSearch;
    Button btnClear;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private String streetStr ="";
    private String cityStr="";
    private String stateStr="";
    private String unitStr="";
    private RadioGroup unitGroup;
    private RadioButton unitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText streetText =  (EditText) findViewById(R.id.streetText);
        EditText cityText =  (EditText) findViewById(R.id.cityText);
        streetText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView warningText = (TextView) findViewById(R.id.warningText);
                warningText.setVisibility(View.INVISIBLE);
            }
        });
        cityText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView warningText = (TextView) findViewById(R.id.warningText);
                warningText.setVisibility(View.INVISIBLE);
            }
        });
        Spinner stateSpinner = (Spinner) findViewById(R.id.stateSpinner);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                TextView warningText = (TextView) findViewById(R.id.warningText);
                if(selectedItemView.toString().equals("Select")) {
                    warningText.setText("Please select a State");
                    warningText.setVisibility(View.VISIBLE);
                    return;
                }
                else
                    warningText.setVisibility(View.INVISIBLE);
                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        unitGroup = (RadioGroup) findViewById(R.id.unitRadio);
    }

    public boolean validateForm(){
        EditText editText = (EditText) findViewById(R.id.streetText);
        streetStr = editText.getText().toString().trim();
        TextView warningText = (TextView) findViewById(R.id.warningText);
        if(streetStr.equals("")){
            warningText.setText("Please enter a Street Address");
            warningText.setVisibility(View.VISIBLE);
            return false;
        }
        editText = (EditText) findViewById(R.id.cityText);
        cityStr = editText.getText().toString().trim();
        if(cityStr.equals("")){
            warningText.setText("Please enter a City");
            warningText.setVisibility(View.VISIBLE);
            return false;
        }
        Spinner stateSpinner=(Spinner) findViewById(R.id.stateSpinner);
        stateStr = stateSpinner.getItemAtPosition(stateSpinner.getSelectedItemPosition()).toString().trim();
        if(stateStr.equals("Select")){
            warningText.setText("Please select a State");
            warningText.setVisibility(View.VISIBLE);
            return false;
        }
        int selectedId = unitGroup.getCheckedRadioButtonId();
        unitButton = (RadioButton) findViewById(selectedId);
        unitStr = unitButton.getText().toString().toLowerCase();
        return true;
    }
    public void clearSearch(View view){
        EditText editText = (EditText) findViewById(R.id.streetText);
        editText.setText("");
        editText = (EditText) findViewById(R.id.cityText);
        editText.setText("");
        Spinner stateSpinner=(Spinner) findViewById(R.id.stateSpinner);
        stateSpinner.setSelection(0);
        TextView warningText = (TextView) findViewById(R.id.warningText);
        warningText.setVisibility(View.INVISIBLE);
    }


    private class PHPJSONTask extends AsyncTask<URL, Integer, String> {
        protected String doInBackground(URL... urls) {
            //urls[0] is the url of the PHP in hw8
            String resultJSON="";
            HttpURLConnection c = null;
            try {
                 c = (HttpURLConnection) urls[0].openConnection();
                c.connect();
                int status =c.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        resultJSON= sb.toString();
                }
               // if(resultJSON.equals("ERROR: No results returned, please try another address")){

                //}


            } catch (IOException e) {
                resultJSON="Error in connection of "+urls[0].toString()+"\n"+ e.toString();
            }finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                    }
                }
            }
            return resultJSON;
        }
        protected void onPostExecute(String result) {
            //result is the JSON returned by the PHP, pass the JSON to next activity(ResultActivity)
            String[] toPass = new String [4];// contains 0:JSON, 1: city, 2: state, 3: unit
            toPass[0] = result;
            toPass[1] = cityStr;
            toPass[2] = stateStr;
            toPass[3] = unitStr;
            passJSON(toPass);

        }
    }
    public void toResult(View view) throws MalformedURLException {
        if( validateForm()){
            //retrieve JSON from php
            String phpQuery = null;
            try {
                //ex: http://54.68.117.94/HW8.php?city=usc&street=la&degree=fahrenheit&state=CA
                phpQuery ="http://54.68.117.94/HW8.php?city=" + URLEncoder.encode( (cityStr ), "UTF-8")+ "&street=" + URLEncoder.encode( (streetStr), "UTF-8")+ "&degree=" + unitStr + "&state=" + stateStr ;
                Log.d("phpQuery",phpQuery);
                URL phpUrl = new URL(phpQuery);
                PHPJSONTask phpTask = new PHPJSONTask();
                phpTask.execute(phpUrl);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }



        }
    }
    public void passJSON(String[] JSONStr){
        try {
            JSONObject thisJson = new JSONObject(JSONStr[0]);
        } catch (JSONException e) {
            TextView warningText = (TextView) findViewById(R.id.warningText);

            warningText.setText("No results returned");
            warningText.setVisibility(View.VISIBLE);
            return;
        }
        Intent intent = new Intent(this,ResultActivity.class);
        intent.putExtra("toResultJSON", JSONStr);
        startActivity(intent);

    }
    public void toAbout(View view) {
        Intent intent = new Intent(this,AboutActivity.class);
        startActivity(intent);
    }
    public void toForecast(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://forecast.io"));
        startActivity(browserIntent);
    }
}
