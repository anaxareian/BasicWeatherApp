package net.x_sm.basicweatherapp;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // Constants
    static final String API_URL = "https://openweathermap.org/data/2.5/weather?q=%s,us&units=imperial&appid=b6907d289e10d714a6e88b30761fae22";
    static final String API_KEY_WEATHER = "weather";
    static final String API_KEY_MAIN = "main";
    static final String API_KEY_DESCRIPTION = "description";
    static final String API_KEY_TEMPARATURE = "temp";
    static final String API_KEY_ICON = "icon";

    // Model
    protected String temp;
    protected String weatherMainDesc;
    protected String weatherDescription;
    protected String icon;
    protected Context context = this;

    // Views
    protected TextView tempTextView;
    protected TextView weatherMainTextView;
    protected TextView weatherDescriptionTextView;
    protected ImageView weatherIconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempTextView = (TextView) findViewById(R.id.tempTextView);
        weatherMainTextView = (TextView) findViewById(R.id.weatherMainTextView);
        weatherDescriptionTextView = (TextView) findViewById(R.id.weatherDescriptionTextView);
        weatherIconImageView = (ImageView) findViewById(R.id.weatherIconImageView);

        Button getWeatherBtn = (Button) findViewById(R.id.getWeatherBtn);
        getWeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usCityEditText = (EditText) findViewById(R.id.usCityEditText);
                DownloadTask task = new DownloadTask();
                task.execute(String.format(API_URL, usCityEditText.getText()));
                task = null;
            };
        });
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inStream);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            Log.i("JSON", s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString(API_KEY_WEATHER);
                JSONArray arr = new JSONArray( weatherInfo );
                JSONObject weatherInstance = arr.getJSONObject(0);
                JSONObject mainInfo = new JSONObject(jsonObject.getString(API_KEY_MAIN));
                temp = mainInfo.getString(API_KEY_TEMPARATURE) + " F";
                weatherMainDesc =  weatherInstance.getString(API_KEY_MAIN);
                weatherDescription = weatherInstance.getString(API_KEY_DESCRIPTION);

                icon = "w" + weatherInstance.getString(API_KEY_ICON);
                int id = getResources().getIdentifier(icon, "drawable", context.getPackageName());
                weatherIconImageView.setImageResource(id);
                weatherIconImageView.refreshDrawableState();
/*               Log.i(API_KEY_WEATHER + "[0]", weatherInstance.toString());
                Log.i("Weather:" + API_KEY_MAIN, weatherInstance.getString(API_KEY_MAIN));
                Log.i(API_KEY_DESCRIPTION, weatherInstance.getString(API_KEY_DESCRIPTION));
                Log.i(API_KEY_MAIN, mainInfo.toString());
                Log.i(API_KEY_TEMPARATURE, temp);
*/
                tempTextView.setText(temp);
                weatherMainTextView.setText(weatherMainDesc);
                weatherDescriptionTextView.setText(weatherDescription);

            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

}