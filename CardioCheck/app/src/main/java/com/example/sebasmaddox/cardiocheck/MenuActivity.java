package com.example.sebasmaddox.cardiocheck;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RemoteViews;
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

import java.util.Timer;
import java.util.TimerTask;

public class MenuActivity extends AppCompatActivity {
    Button btnConsulta, btnDatos, btnCerrar;
    TextView ultima;
    SharedPreferences preferences;
    private String url = "http://192.168.0.100:81/android/ultimopulso.php";
    JSONArray vectorJSON;
    String pulso;
    Switch activar;
    NotificationCompat.Builder notificacion;
    private static int idUnica=51623;
    String NOTIFICATION_CHANNEL_ID= "ay_channel_id_01";
    //String cel =  getIntent().getExtras().getString("telefono");
    String cel =  "+5216182071003";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnConsulta = findViewById(R.id.consulta);
        btnCerrar = findViewById(R.id.cerrar);
        btnDatos = findViewById(R.id.datos);
        ultima = findViewById(R.id.ulti);
        preferences = getSharedPreferences("temp", getApplicationContext().MODE_PRIVATE);
        sendAndRequestResponse();
        activar = findViewById(R.id.switch1);


        notificacion = new NotificationCompat.Builder(MenuActivity.this,NOTIFICATION_CHANNEL_ID);
        notificacion.setAutoCancel(true);


            llamar();

        btnConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createSimpleDialog().show();
            }
        });

        btnDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ActualizarActivity.class);
                startActivity(intent);
            }
        });

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }

    public AlertDialog createSimpleDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
        builder.setTitle("Consulta")
                .setMessage("Como desea ver la informacion?")
                .setPositiveButton("Horas", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MenuActivity.this, HorasActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Meses", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MenuActivity.this, ConsultaActivity.class);
                        startActivity(intent);
                    }
                });
        return builder.create();
    }

    private void sendAndRequestResponse(){
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(MenuActivity.this);

        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    vectorJSON = jsonObject.getJSONArray("datos");

                    ultima.setText(vectorJSON.getJSONObject(0).getString("pulso").toString()+"BPM");


                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG","Error: "+error.toString());
                Toast.makeText(MenuActivity.this, "Error ultimo", Toast.LENGTH_LONG).show();
                //sendAndRequestResponse();
            }
        });
        mRequestQueue.add(mStringRequest);
    }


    Timer timer = new Timer();
    final Handler handler = new Handler();

    public void llamar(){

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                AsyncTask mytask = new AsyncTask() {
                    private String url = "http://192.168.0.100:81/android/ultimopulso.php";
                    JSONArray vectorJSON;
                    @Override
                    protected Object doInBackground(Object[] objects) {

                        new Handler (Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                RequestQueue mRequestQueue;
                                StringRequest mStringRequest;
                                mRequestQueue = Volley.newRequestQueue(MenuActivity.this);

                                mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try{
                                            JSONObject jsonObject = new JSONObject(response);
                                            vectorJSON = jsonObject.getJSONArray("datos");

                                            ultima.setText(vectorJSON.getJSONObject(0).getString("pulso").toString()+"BPM");
                                            pulso=(vectorJSON.getJSONObject(0).getString("pulso").toString());


                                            int pul = Integer.parseInt(pulso);

                                            if (pul>900)
                                            {
                                                notificacion.setSmallIcon(R.mipmap.ic_launcher)
                                                        .setTicker("CardioCheck Alert")
                                                        .setPriority(Notification.PRIORITY_HIGH)
                                                        .setChannelId(NOTIFICATION_CHANNEL_ID)
                                                        .setContentTitle("Alerta")
                                                        .setContentText("Corazon latiendo a ritmo Inusual");
                                                Intent intent= new Intent(MenuActivity.this,MainActivity.class);

                                                PendingIntent pendingIntent=PendingIntent.getActivity(MenuActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                                notificacion.setContentIntent(pendingIntent);

                                                NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                                nm.notify(idUnica,notificacion.build());
                                                idUnica++;
                                                SmsManager sms = SmsManager.getDefault();
                                                sms.sendTextMessage(cel, null, "Cardio Check Alerta de fallo", null, null);



                                            }



                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.i("TAG","Error: "+error.toString());
                                        Toast.makeText(MenuActivity.this, "Error ultimo", Toast.LENGTH_LONG).show();
                                        //sendAndRequestResponse();
                                    }
                                });
                                mRequestQueue.add(mStringRequest);
                            }
                        });

                        return null;
                    }
                };
                mytask.execute();
            }
        };
        timer.schedule(task,0,60000);
    }
}
