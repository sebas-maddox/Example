package com.example.sebasmaddox.reproductor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ReproductorActivity extends AppCompatActivity implements SensorEventListener{
    ImageView Last, Play, Next, favorito, imagen;
    TextView titulo, artista, inicio, fin;
    SeekBar bar;
    SensorManager mSensorManager;
    Switch aSwitch;

    boolean w = false;

    private String url = "http://192.168.43.212:81/android/rep/select.php";

    private String url2 = "http://192.168.43.212:81/android/rep/fav.php";
    private String url3 = "http://192.168.43.212:81/android/rep/fav2.php";
    ArrayAdapter<String> adaptador;

    private ArrayList<String> alumnos;
    private JSONArray users;
    JSONArray vectorJSON;
    Bitmap bitmap;
    private static final int PICK_IMAGE = 100;
    String nom,art,path1,setAlbum,album, pickid;
    String x = "0";
    int i =1;
    int numero = 0;
    String alb,cancion;
    int y =0;
    int duracion;
    String favo ="no";

    MediaPlayer mp = new MediaPlayer();
    private Handler skHandler = new Handler();








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);


        alumnos= new ArrayList<String>();
        titulo=findViewById(R.id.txtTitulo);
        artista=findViewById(R.id.txtArtista);
        Last=findViewById(R.id.imgLast);
        Play=findViewById(R.id.imgPlay);
        Next=findViewById(R.id.imgNext);
        favorito=findViewById(R.id.imgFav);
        imagen=findViewById(R.id.imageView2);
        bar = findViewById(R.id.seekBar);
        inicio = findViewById(R.id.txtInicio);
        fin = findViewById(R.id.txtFin);
        aSwitch = findViewById(R.id.switch1);
        Boolean switchstate = aSwitch.isChecked();

        mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_NORMAL);


        favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (x.equals("0"))
                {
                    favorito.setImageResource(R.drawable.fav2);
                    x = "1";
                }
                else
                {
                    favorito.setImageResource(R.drawable.fav);
                    x = "0";
                }
                enviar();
            }
        });

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numero=numero+1;
                if(numero==vectorJSON.length())
                {
                    numero=0;
                }
                llenar(numero);
            }
        });

        Last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(numero==0)
                {
                    numero=vectorJSON.length();
                }
                else
                {
                    numero=numero-1;
                }
                llenar(numero);
            }
        });

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(bar.getProgress());
            }
        });

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (y==0) {
                    Play.setImageResource(R.drawable.pause);
                    mp.pause();
                    duracion = mp.getCurrentPosition();
                    y=1;
                }
                else
                {
                    Play.setImageResource(R.drawable.play);
                    mp.seekTo(duracion);
                    mp.start();
                    y=0;
                }
            }
        });
    }

    private String getHRM(int milliseconds )
    {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
        return ((hours<10)?"0"+hours:hours) + ":" +
                ((minutes<10)?"0"+minutes:minutes) + ":" +
                ((seconds<10)?"0"+seconds:seconds);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.Alegre1:
                favo="no";
                album = "Alegre";
                setAlbum="Album";
                numero=0;
                sendAndRequestResponse(numero);
                return true;
            case R.id.Sad:
                favo="no";
                album = "Sad";
                setAlbum="Album";
                numero=0;
                sendAndRequestResponse(numero);
                return true;
            case R.id.Angry:
                favo="no";
                album = "Angry";
                setAlbum="Album";
                numero=0;
                sendAndRequestResponse(numero);
                return true;
            case R.id.Mad:
                favo="no";
                setAlbum="Album";
                album="Mad";
                numero=0;
                sendAndRequestResponse(numero);
                return true;
            case R.id.Lust:
                favo="no";
                album = "Lust";
                setAlbum="Album";
                numero=0;
                sendAndRequestResponse(numero);
                return true;
            case R.id.Like:
                favo="si";
                numero=0;
                sendAndRequestResponse2(numero);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mp.stop();
    }


    private void sendAndRequestResponse(final int indice){
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(ReproductorActivity.this);

        mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    vectorJSON = jsonObject.getJSONArray("datos");
                    llenar(indice);

                    Toast.makeText(ReproductorActivity.this, "Conexion exitosa", Toast.LENGTH_LONG).show();

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG","Error: "+error.toString());
                Toast.makeText(ReproductorActivity.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put(setAlbum, album );
                return map;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void sendAndRequestResponse2(final int indice){
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(ReproductorActivity.this);

        mStringRequest = new StringRequest(Request.Method.GET, url3, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    vectorJSON = jsonObject.getJSONArray("datos");
                    llenar(indice);



                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG","Error: "+error.toString());
                Toast.makeText(ReproductorActivity.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        });
        mRequestQueue.add(mStringRequest);
    }

    private void llenar(int a)
    {
        try{
            titulo.setText(vectorJSON.getJSONObject(a).getString("nom").toString());
            artista.setText(vectorJSON.getJSONObject(a).getString("art").toString());
            bitmap = BitmapFactory.decodeFile(vectorJSON.getJSONObject(a).getString("img").toString());
            cancion= vectorJSON.getJSONObject(a).getString("aud").toString();
            pickid=vectorJSON.getJSONObject(a).getString("id").toString();
            x=vectorJSON.getJSONObject(a).getString("like").toString();
            imagen.setImageBitmap(bitmap);

            try {

                if (mp.isPlaying())
                {
                    mp.reset();
                }
                mp.setDataSource(cancion);
                mp.prepare();
                mp.seekTo(0);
                mp.start();

            } catch (Exception e) {
                Toast.makeText(getApplication(), "error exception", Toast.LENGTH_SHORT).show();
            }
            new tarea().execute();


            if (x.equals("1"))
            {
                favorito.setImageResource(R.drawable.fav2);
            }
            if (x.equals("0"))
            {
                favorito.setImageResource(R.drawable.fav);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }


    private void enviar(){
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(ReproductorActivity.this);

        mStringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    vectorJSON = jsonObject.getJSONArray("datos");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG","Error: "+error.toString());
                enviar();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", pickid);
                params.put("fav", x);

                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (aSwitch.isChecked()) {

            Last.setEnabled(false);
            Play.setEnabled(false);
            Next.setEnabled(false);
            switch (event.sensor.getType()) {
                case Sensor.TYPE_PROXIMITY:
                    if (event.values[0] != 0) {

                    } else {
                        if (y == 0) {
                            Play.setImageResource(R.drawable.pause);
                            mp.pause();
                            duracion = mp.getCurrentPosition();
                            y = 1;
                        } else {
                            Play.setImageResource(R.drawable.play);
                            mp.seekTo(duracion);
                            mp.start();
                            y = 0;
                        }
                    }
                    break;
                case Sensor.TYPE_LIGHT:
                    mp.setVolume(event.values[0] / 1000, event.values[0] / 1000);

                    break;


                case Sensor.TYPE_GYROSCOPE:
                    if (event.values[2] > 3) {
                        numero = numero + 1;
                        if (numero == vectorJSON.length()) {
                            numero = 0;
                        }
                        llenar(numero);
                    }
                    if (event.values[0] >= 3) {
                        if (numero == 0) {
                            numero = vectorJSON.length();
                        } else {
                            numero = numero - 1;
                        }
                        llenar(numero);
                    }

                    break;
            }
        }
        else
        {
            Last.setEnabled(true);
            Play.setEnabled(true);
            Next.setEnabled(true);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class  tarea extends AsyncTask<Integer, Integer, Integer>
    {
        int x = 0;
        int  y = 0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setMax(mp.getDuration());
            fin.setText( getHRM(mp.getDuration()) );
            x=bar.getMax();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            bar.setProgress(values[0]);
            inicio.setText(getHRM(values[0]));
        }
        @Override
        protected Integer doInBackground(Integer... integers) {

            while (bar.getProgress()<=x)
            {
                publishProgress(mp.getCurrentPosition());
                //SystemClock.sleep(100);
            }

            return null;
        }


    }

}
